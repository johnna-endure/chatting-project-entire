package room;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * JUnit 테스트에서 블락킹을 허용하지 않는다??
 */
public class TestRoomThread {
    private static Logger logger = LogManager.getLogger(TestRoomThread.class);

    public static void main(String[] args) throws IOException, InterruptedException {

        RoomThread roomThread = new RoomThread();
        roomThread.start();
        InetSocketAddress address = new InetSocketAddress("localhost", roomThread.getPort());

        MockClient client1 = new MockClient("client1", address);
        client1.start();
        MockClient client2 = new MockClient("client2", address);
        client2.start();

        Thread.sleep(1000);

        while(true) {
            Scanner sc = new Scanner(System.in);
            String message = sc.nextLine();
            client1.writeMessage(message);
        }
    }
}
