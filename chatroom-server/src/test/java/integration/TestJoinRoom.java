package integration;

import com.google.gson.Gson;
import entity.ChatRoom;
import io.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import util.IntegrationTester;

import java.io.IOException;

/*
method = Method.GET, url = "/room/{id}" 테스트
 */
public class TestJoinRoom {
    static IntegrationTester tester = new IntegrationTester();
    Gson gson = new Gson();

    @BeforeClass
    public static void beforeClass() {
        tester.startServerForTest();
    }

    @Test
    public void testJoinRoom_WhenRoomIsFull() throws IOException {
        tester.integrationTest(
                list -> list.add(new ChatRoom(1,"room1", 2).setCurrentSize(2)),
                "POST /room/1",
                new Response(200,
                        "room is full"));
    }

    @Test
    public void testJoinRoom_WhenIncorrectId() {
        tester.integrationTest(
                list -> list.add(new ChatRoom(1,"room1", 2).setCurrentSize(2)),
                "POST /room/2",
                new Response(200,
                "no room corresponds to id"));
    }

    @Test
    public void testJoinRoom_WhenUserCanJoinTheRoom() {
        String expectedBody = gson.toJson(new ChatRoom(1,"room1", 2).setCurrentSize(2));
        tester.integrationTest(
                list -> list.add(new ChatRoom(1,"room1", 2).setCurrentSize(1)),
                "POST /room/1",
                new Response(200,
                        "success", expectedBody));
    }
}
