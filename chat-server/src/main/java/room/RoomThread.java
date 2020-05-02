package room;

import handler.RoomThreadHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;

/**
통신가능한 랜덤 포트를 가지며 get 함수를 통해 접근 가능하다.
통신은 Selector 를 이용한 이벤트 방식으로 통신한다.
 */
public class RoomThread extends Thread{
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private RoomThreadHandler handlers;
    private InetSocketAddress address = new InetSocketAddress("localhost", 0);
    private int port = -1;
    private boolean runningFlag = true;
    private static Logger logger= LogManager.getLogger(RoomThread.class);

    public RoomThread() { init(); }

    public void init(){
        try {
            selector = Selector.open();

            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(address);

            handlers = new RoomThreadHandler();

            InetSocketAddress address = (InetSocketAddress) serverSocketChannel.getLocalAddress();
            this.port = address.getPort();
            logger.debug("[init] roomThread 초기화 :: port = {}", port);
        } catch (IOException e) {
            logger.atError().withThrowable(e).log(e.getMessage());
        }
    }

    @Override
    public void run() {
        logger.debug("RoomThread[port={}] 시작.", port);
        registerAcceptInterest();

        while(runningFlag) {
            try {
                int selectedCount = selector.select();
//                logger.debug("RoomThread[port={}] selected channel count = {}" , port, selectedCount);
                if(selectedCount == 0) continue;

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey key : selectionKeys) {
                    if(key.isValid() && key.isAcceptable()) {
                        handlers.joinRoom(key);
                    }
                    if(key.isValid() && key.isReadable()){
                        handlers.processMessage(key);
                    }
                }
                sleep(1000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.debug("[RoomThread] 종료됩니다.");
    }

    private void registerAcceptInterest() {
        try {
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }

    public void exitThread(){
        runningFlag = false;
        selector.wakeup();
    }

    public int getPort() {
        return port;
    }

    public Selector getSelector() {
        return selector;
    }
}
