package channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.logging.Logger;

public class MockServer extends Thread{
    public Selector selector;
    public ServerSocketChannel serverSocketChannel;
    SocketAddress address;
    public boolean isContinue = true;

    Logger logger = Logger.getLogger(MockServer.class.getCanonicalName());

    public MockServer(SocketAddress address) {
        this.address = address;
    }

    public void init()  {
        try {

            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
//            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(isContinue) {
            try {
                selector.select();
                System.out.println("select 지남");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            selectionKeys.stream()
                    .filter(SelectionKey::isAcceptable)
                    .forEach(this::acceptHandler);
        }
    }

    private void acceptHandler(SelectionKey key) {
        System.out.println("accept 핸들러 들어옴");
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        try {
            SocketChannel socketChannel = serverSocketChannel.accept();
            if(socketChannel == null) logger.info("[MockServer] 클라이언트와 연결 실패");
            else logger.info("[MockServer] 클라이언트와 연결됨");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
