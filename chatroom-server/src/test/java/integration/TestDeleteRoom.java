package integration;

import com.google.gson.Gson;
import entity.ChatRoom;
import io.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import util.IntegrationTester;

import java.util.List;
import java.util.function.Consumer;

public class TestDeleteRoom {
    private static final IntegrationTester tester = new IntegrationTester();
    private Gson gson = new Gson();
    Consumer<List<ChatRoom>> settingTestData = l -> {
        l.add(new ChatRoom(1, "room1", 2));
        l.add(new ChatRoom(2, "room2", 2));
        l.add(new ChatRoom(3, "room3", 2));
    };

    @BeforeClass
    public static void beforeClass() {
        tester.startServerForTest();
    }

    @Test
    public void testDeleteRoom_whenSuccess() {
        tester.integrationTest(
                settingTestData,
                "DELETE /room/1",
                new Response(200, "deletion success")
        );
    }

    @Test
    public void testDeleteRoom_whenIdIsIncorrect() {
        tester.integrationTest(
                settingTestData,
                "DELETE /room/4",
                new Response(200,"no room correspond to id")
        );
    }

}
