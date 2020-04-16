package channel;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.Optional;

public class ServerChannelManager {
    private ServerSocketChannel serverSocketChannel;
    private SocketAddress address;

    public ServerChannelManager(SocketAddress address) {
        this.address = address;
    }

    public ServerSocketChannel getServerSocketChannel() {
        return serverSocketChannel;
    }

    public Optional<ServerSocketChannel> bind() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel = serverSocketChannel.bind(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(serverSocketChannel);
    }

}
