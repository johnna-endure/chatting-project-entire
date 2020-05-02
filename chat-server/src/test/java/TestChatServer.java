import com.google.gson.Gson;
import entity.ChatRoom;
import io.NIOUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class TestChatServer {
    public static void main(String[] args) throws IOException {
        NIOUtils nioUtils = new NIOUtils();
        Gson gson = new Gson();

        ChatRoom newRoom = new ChatRoom("room1", 2);
        SocketAddress chatRoomServerAddress = new InetSocketAddress("localhost", 9001);
        SocketChannel client = SocketChannel.open(chatRoomServerAddress);
        nioUtils.write("POST /room\\n\\n"+gson.toJson(newRoom), client);
        String data = nioUtils.read(client);
        client.close();
        System.out.println(data);
    }

    @Test
    public void testPort() throws IOException {
        ServerSocketChannel serverSocketChannel1 = ServerSocketChannel.open();
        serverSocketChannel1.bind(new InetSocketAddress("localhost", 0));

        System.out.println(serverSocketChannel1.getLocalAddress());

        ServerSocketChannel serverSocketChannel2 = ServerSocketChannel.open();
        serverSocketChannel2.bind(new InetSocketAddress("localhost", 0));

        System.out.println(serverSocketChannel2.getLocalAddress());
    }

    @Test
    public void testSocketChannel() throws IOException {

    }
}
