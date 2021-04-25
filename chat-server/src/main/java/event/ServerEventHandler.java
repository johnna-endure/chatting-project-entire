package event;

import com.google.gson.Gson;
import io.NIOUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/*
복수의 op를 가진 채널은 없다고 하자.
 */
public class ServerEventHandler {
    private Map<String, Method> handlerMap;
    private Selector selector;
    private NIOUtils nioUtils = new NIOUtils();

    Logger logger = Logger.getLogger(ServerEventHandler.class.getCanonicalName());

    public ServerEventHandler setSelector(Selector selector) {
        this.selector = selector;
        return this;
    }

    public ServerEventHandler() {
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
        if(!key.isValid()) return;
        //json 으로 바꿔야함
        String attachedEventName = (String) key.attachment();
        String lowerCaseEventName = attachedEventName.toLowerCase();

        Method method = handlerMap.keySet().stream()
                .filter(eventNameKey -> eventNameKey.equals(lowerCaseEventName))
                .map(eventNameKey -> handlerMap.get(eventNameKey))
                .findFirst().orElseThrow(() ->
                        new RuntimeException("조건에 맞는 핸들러를 찾을 수 없습니다.")); //런타임 예외는 임시로

        invoke(method, key);
    }

    private void invoke(Method method, SelectionKey key){
        try {
            method.invoke(this, key);
        } catch (Exception e) { // <- 수정 필요함. 편의를 위해 최상위 예외로 캐치
            e.printStackTrace();
            throw new RuntimeException("핸들러 invoke 오류"); // 임시
        }
    }
    /*
    클라이언트의 연결을 accept 하고 read/write 준비가 된 채널을 등록한다.
    하나의 클라이언트에 대해서 accept 실패한다고 서버를 멈출 수는 없다.
     */
    @Event(eventName = "accept")
    public void accept(SelectionKey key) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        try {
            SocketChannel socketChannel = serverSocketChannel.accept(); //Optional 구현 필요
            socketChannel.configureBlocking(false);
            //채팅방 구현시 att 파라미터는 json 으로 수정해야
            socketChannel.register(selector, SelectionKey.OP_READ, "readMessage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Event(eventName = "readMessage")
    public void readMessage(SelectionKey key) {
        //모든 핸들러에서 채널의 유효성 여부 판단해야됨
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(200);
        try {
            int reads = socketChannel.read(buffer);
            buffer.flip();
            byte[] byteMessage = new byte[reads];
            buffer.get(byteMessage);

            String message = new String(byteMessage);
            System.out.println("[클라이언트로부터 받은 메세지] : " + message);

            //메세지를 보낸 채널에 바로 응답을 보냄. 테스트용
            sendMessage(key, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Event(eventName = "sendMessage")
    public void sendMessage(SelectionKey key, String message) {
        System.out.println("sendMessage 메서드 호출");
        Gson gson = new Gson();
        SocketChannel channel = (SocketChannel) key.channel();
        nioUtils.write(message, channel);
    }

}
