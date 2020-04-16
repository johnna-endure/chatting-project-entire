package server;

import controller.ChatRoomController;
import io.NIOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import parser.reqeuest.RequestParser;
import parser.response.ResponseParser;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Optional;
import java.util.concurrent.*;

public class ChatRoomServer extends Thread{
    private ServerSocketChannel serverSocketChannel;
    private ExecutorService executorService;
    private ChatRoomController controller;
    private NIOUtils nioUtils = new NIOUtils();

    private static final Logger logger = LogManager.getLogger(ChatRoomServer.class);

    @Override
    public void run() {
        while(true) {
            try {
                logger.info("[run] 클라이언트의 새로운 연결을 기다립니다.");
                Optional<SocketChannel> socketChannelOpt = accept(serverSocketChannel);
                if(socketChannelOpt.isEmpty()) continue;
                SocketChannel acceptedSocketChannel = socketChannelOpt.get();
                logger.info("[run] 클라이언트와 연결됨. client = {}",acceptedSocketChannel.getRemoteAddress());
                processRequest(acceptedSocketChannel, controller).get();

            } catch (InterruptedException e) {
                logger.atError().withLocation().withThrowable(e).log(InterruptedException.class.getName());
                return;
            } catch (ExecutionException | IOException e) {
                logger.atError().withLocation().withThrowable(e).log(e.getClass().getName());
                return;
            }
        }
    }


    public Optional<SocketChannel> accept(ServerSocketChannel serverSocketChannel) throws InterruptedException {
        return nioUtils.accept(serverSocketChannel);
    }

    private CompletableFuture<Void> processRequest(
            SocketChannel socketChannel, ChatRoomController controller){
        return CompletableFuture.supplyAsync(() -> nioUtils.read(socketChannel), executorService)
                .thenApply(RequestParser::parse)
                .thenApply(controller::dispatch)
                .thenApply(ResponseParser::parse)
                .thenAccept(message -> {
                    nioUtils.write(message, socketChannel);
                    try {
                        socketChannel.close();
                        logger.info("[run] 응답 보내고 채널 닫음.");
                    } catch (IOException e) {
                        logger.atError().withThrowable(e).log(e.getMessage());
                    }
                });
    }

    public void stopServer() {
        logger.info("[stopServer] 서버를 종료합니다.");
        this.interrupt();
    }

    public ChatRoomServer setController(ChatRoomController controller) {
        this.controller = controller;
        return this;
    }

    public ChatRoomServer setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public ChatRoomServer setServerSocketChannel(ServerSocketChannel serverSocketChannel) {
        this.serverSocketChannel = serverSocketChannel;
        return this;
    }
}
