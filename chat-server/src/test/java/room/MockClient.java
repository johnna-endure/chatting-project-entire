package room;

import io.NIOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class MockClient extends Thread{
    private NIOUtils nioUtils = new NIOUtils();
    private String name;
    private SocketChannel client;
    private InetSocketAddress address;
    private Selector selector;
    private boolean runningFlag = true;

    private static Logger logger = LogManager.getLogger(MockClient.class);


    public MockClient(String name, InetSocketAddress address) {
        this.name = name;
        this.address = address;
        init();
    }

    private void init() {
        try {
            selector = Selector.open();
            client = SocketChannel.open();
            client.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            boolean isConnected = connect(client);
            logger.info("{}::isConnected = {}", name, isConnected);
            if(!isConnected) return;

            client.register(selector, SelectionKey.OP_READ);
            while(runningFlag) {
                logger.info("selection 루프 진입.");
                int selectionCount = selector.select();
                logger.info("{}::selectionCount = {}",name, selectionCount);

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey key : selectionKeys) {
                    if(key.isValid() && key.isReadable()) {
                        readHandler(key);
                    }
                }
                sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    private boolean connect(SocketChannel client) throws IOException {
        client.connect(address);
        return client.finishConnect();
    }

    private void readHandler(SelectionKey key){
        logger.info("{}::readHandler 호출됨.", name);
        String message = nioUtils.read((SocketChannel) key.channel());
        if(message == null) return;
        logger.info("{}::읽은 메세지 = {}", name, message);
    }

    public void writeMessage(String message) {
        nioUtils.write(message, client);
    }
}
