package selector;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class TestSelector {

    @Test
    public void testSelector() throws IOException {
        Selector selector = Selector.open();

        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.bind(new InetSocketAddress("localhost", 9000));
        socketChannel.configureBlocking(false);

        socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        selector.selectNow();

        socketChannel.register(selector, SelectionKey.OP_READ);

    }
}
