package app.event;

import app.dto.Attachment;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/*
복수의 op를 가진 채널은 없다고 하자.
 */
public class EventHandler {
    private Map<String, Method> handlerMap;
    private Selector selector;
    private Gson gson = new Gson();
    private SocketAddress address = new InetSocketAddress("localhost", 9000);

    Logger logger = Logger.getLogger(EventHandler.class.getCanonicalName());

    public EventHandler setSelector(Selector selector) {
        this.selector = selector;
        return this;
    }

    public EventHandler() {
        handlerMap = getHandlerMap();
    }

    private Map<String, Method> getHandlerMap() {
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method m : methods) { m.setAccessible(true); }

        return Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(Event.class))
                .collect(Collectors.toMap(
                        method -> method.getName().toLowerCase(),
                        method -> method));
    }
    /*
    map 을 이용해서 key 에 맞는 핸들러 매핑. key 에는 eventName:String 값이 등록되어있다.
     */
    public void delegate(SelectionKey key) {
        Attachment attachment = gson.fromJson((String) key.attachment(), Attachment.class);

        String lowerCaseEventName = attachment.getEventName().toLowerCase();
        Optional<String> messageOpt = Optional.ofNullable(attachment.getMessage());

        Method method = handlerMap.keySet().stream()
                .filter(eventNameKey -> eventNameKey.equals(lowerCaseEventName))
                .map(eventNameKey -> handlerMap.get(eventNameKey))
                .findFirst().orElseThrow(() -> new RuntimeException("조건에 맞는 핸들러를 찾을 수 없습니다."));

        invoke(method, key);
    }

    private void invoke(Method method, SelectionKey key){
        try {
            method.invoke(this, key);
        } catch (Exception e) { // <- 수정 필요함. 편의를 위해 최상위 예외로 캐치
            e.printStackTrace();
            throw new RuntimeException("핸들러 invoke 오류");
        }
    }

    @Event(eventName = "writeMessage")
    public void writeMessage(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        Attachment attachment = gson.fromJson((String)key.attachment(), Attachment.class);
        String message = attachment.getMessage();

        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(),0 , 200);

        try {
            socketChannel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            key.cancel();
        }
    }
}
