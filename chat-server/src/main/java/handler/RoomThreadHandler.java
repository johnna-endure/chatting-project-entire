package handler;

import io.NIOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/*
제공해야할 기능

- RoomThread 의 ServerSocketChannel 과 연결 처리 (ACCEPT) : 방에 참가하기 기능
- 방을 나갈 경우 셀렉터의 등록된 해당 채널이 close 되야함. 나가기 처리 (READ)
- 메세지 읽기
- 읽은 메세지를 방의 모든 인원에게 보내기

READ 의 경우는 항상 존재하는 interest이고 write의 경우는 작업이 끝나면 interest를 해제한다.
 */
public class RoomThreadHandler {
    private NIOUtils nioUtils = new NIOUtils();
    private static Logger logger = LogManager.getLogger(RoomThreadHandler.class);
    private Queue<String> messageQueue = new ConcurrentLinkedDeque<>();
    /**
     * serverSocketChannel 의 accept 작업을 수행합니다.
     * @param key ServerSocketChannel 객체가 담겨져 있고, nonblocking 모드임.
     * @throws IOException
     */
    public void joinRoom(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel joinedClient = serverSocketChannel.accept();
        if(joinedClient == null) return;
        logger.debug("클라이언트가 방에 참가했습니다.");
        joinedClient.configureBlocking(false);
        joinedClient.register(key.selector(), SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }

    public void processMessage(SelectionKey key) throws IOException {
        SocketChannel connectedChannel = (SocketChannel) key.channel();
        String message = nioUtils.read(connectedChannel);
        if(message == null) return;

        if(message.equals("!exit")) {
            exit(key);
            return;
        }

        key.selector().selectNow(selectionKey -> sendMessage(message, selectionKey));
    }

    private void exit(SelectionKey key) {
        logger.debug("!exit 명령으로 해당 키가 cancel 됩니다.");
        key.cancel();
    }

    private void sendMessage(String message ,SelectionKey key) {
        if(key.isValid() && key.isWritable())
            nioUtils.write(message, (SocketChannel) key.channel());
    }

}
