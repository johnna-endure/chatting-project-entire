package integration;

import com.google.gson.Gson;
import entity.ChatRoom;
import io.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import util.IntegrationTester;

import java.util.ArrayList;
import java.util.List;

public class TestGetRooms {
    private static final IntegrationTester tester = new IntegrationTester();
    private Gson gson = new Gson();

    @BeforeClass
    public static void beforeClass() {
        tester.startServerForTest();
    }

    @Test
    public void testGetRooms_whenSuccess() {
        List<ChatRoom> expectedRooms = new ArrayList<>();
        expectedRooms.add(new ChatRoom(1,"room1",2));
        expectedRooms.add(new ChatRoom(2,"room2",2));
        expectedRooms.add(new ChatRoom(3,"room3",2));
        String body = gson.toJson(expectedRooms);
        tester.integrationTest(
                l -> {
                    l.add(new ChatRoom(1,"room1",2));
                    l.add(new ChatRoom(2,"room2",2));
                    l.add(new ChatRoom(3,"room3",2));
                },
                "GET /rooms",
                new Response(200, "success",
                        gson.toJson(expectedRooms))
        );
    }

    @Test
    public void testGetRooms_whenNoRoomIsThere() {
        tester.integrationTest(
                l -> doNothing(),
                "GET /rooms",
                new Response(200, "success")
                );
    }

    private void doNothing() { }
}
