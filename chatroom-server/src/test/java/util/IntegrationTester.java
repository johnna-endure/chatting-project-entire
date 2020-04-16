package util;

import database.ChatRoomDatabase;
import entity.ChatRoom;
import io.NIOUtils;
import io.response.Response;
import manager.ServerDependencyManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import parser.response.ResponseParser;
import server.ChatRoomServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTester {
    private NIOUtils nioUtils = new NIOUtils();
    private SocketAddress serverAddress;
    private static ChatRoomDatabase testDatabase;


    private static final Logger logger = LogManager.getLogger(IntegrationTester.class);

    public void startServerForTest() {
        testDatabase = new ChatRoomDatabase();
        ChatRoomServer server = new ChatRoomServer();
        ServerDependencyManager dependencyManager = new ServerDependencyManager(0);
        dependencyManager.setDependency(server, () -> testDatabase);

        serverAddress = dependencyManager.getServerSocketAddress();
        server.start();
    }

    private SocketChannel startClientForTest() {
        try {
            logger.debug("[startClientForTest] client open. address = {}", serverAddress);
            return SocketChannel.open(serverAddress);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("client open error");
        }
    }

    public void integrationTest(Consumer<List<ChatRoom>> settingData,
                                String requestURL, Response expectedResponse){
        testDatabase.setDataForTestOnly(settingData);

        SocketChannel client = startClientForTest();
        sendRequest(requestURL, client);
        String actual = receiveResponse(client);
        String expected = ResponseParser.parse(expectedResponse);
        logger.debug("[integrationTest] actual = {}, expected =  {}", actual, expected);
        assertThat(actual).isEqualTo(expected);
    }

    public void sendRequest(String requestURL, SocketChannel client) {
        nioUtils.write(requestURL, client);
    }

    public String receiveResponse(SocketChannel client) {
        return nioUtils.read(client);
    }

    public SocketAddress getServerAddress() {
        return serverAddress;
    }
}
