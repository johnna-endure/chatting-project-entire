package manager;

import controller.ChatRoomController;
import database.ChatRoomDatabase;
import entity.ChatRoom;
import handler.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.ChatRoomServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class ServerDependencyManager {
    private static final Logger logger = LogManager.getLogger(ServerDependencyManager.class);
    private final int serverPort;
    private ServerSocketChannel serverSocketChannel;

    public ServerDependencyManager(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setDependency(ChatRoomServer server, Supplier<ChatRoomDatabase> dbSupplier) {
        try {
            logger.info("[setDependency] 의존성 컴포넌트 초기화 작업 시작.");
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress("localhost", serverPort));
            logger.info("[setDependency] 서버 소켓 바인딩 완료 :: address = {}",
                    serverSocketChannel.getLocalAddress());
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            ChatRoomController controller = new ChatRoomController();
            RequestHandler requestHandler = new RequestHandler().setDatabase(dbSupplier.get());

            controller.setRequestHandler(requestHandler);

            server.setController(controller)
                    .setExecutorService(executorService)
                    .setServerSocketChannel(serverSocketChannel);
            logger.info("[setDependency] 의존성 컴포넌트 초기화 작업 종료.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SocketAddress getServerSocketAddress() {
        try {
            return serverSocketChannel.getLocalAddress();
        } catch (IOException e) {
            logger.atError().withLocation().withThrowable(e).log(e.getMessage());
            throw new RuntimeException();
        }
    }
}
