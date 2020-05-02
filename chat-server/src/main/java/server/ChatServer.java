package server;

import com.google.gson.Gson;
import controller.ChatServerController;
import io.NIOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import parser.reqeuest.RequestParser;
import parser.response.ResponseParser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/*
다른 서버와 통신하기 위하 서버입니다.
동기-논블럭킹 방식
포트번호 9003
 */
public class ChatServer extends Thread{
    private ServerSocketChannel serverSocketChannel;
    private SocketAddress address = new InetSocketAddress("localhost", 9003);
    private NIOUtils nioUtils = new NIOUtils();
    private Gson gson = new Gson();

    private ChatServerController chatServerController = new ChatServerController();
    private static final Logger logger = LogManager.getLogger(ChatServer.class);

    public ChatServer() {init();}

    public void init() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                logger.debug("accept 대기 중.");
                SocketChannel connectedChannel = serverSocketChannel.accept();
                logger.debug("accept 완료 :: remote = {}", connectedChannel);
                CompletableFuture.supplyAsync(() -> nioUtils.read(connectedChannel))
                        .thenApply(RequestParser::parse)
                        .thenApply(request -> chatServerController.dispatch(request))
                        .thenApply(ResponseParser::parse)
                        .thenAccept(data -> {
                            nioUtils.write(data, connectedChannel);
                            try {
                                connectedChannel.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).get();
            } catch (IOException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopServer() {
        logger.info("[stopServer] 서버를 종료합니다.");
        this.interrupt();
    }

    public void setChatServerController(ChatServerController chatServerController) {
        this.chatServerController = chatServerController;
    }
}