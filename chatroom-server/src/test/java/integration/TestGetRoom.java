package integration;

import com.google.gson.Gson;
import entity.ChatRoom;
import io.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import util.IntegrationTester;

import java.net.InetSocketAddress;

/*
    요청 : GET /room/{id}
 */
public class TestGetRoom {
    private static final IntegrationTester tester = new IntegrationTester();
    private Gson gson = new Gson();

    @BeforeClass
    public static void beforeClass() {
        tester.startServerForTest();
    }

    @Test
    public void testGetRoom_whenSuccess() {
        ChatRoom room = new ChatRoom(1,"room1", 2);
        tester.integrationTest(
                l -> l.add(room),
                "GET /room/1",
                new Response(200, "success",   gson.toJson(room))
        );
    }

    @Test
    public void testGetRoom_whenNoAvailableRoom() {
        ChatRoom room = new ChatRoom(1,"room1", 2);
        tester.integrationTest(   l -> l.add(room),
                "GET /room/2",
                new Response(200, "no room corresponds to this id"));
    }
}
