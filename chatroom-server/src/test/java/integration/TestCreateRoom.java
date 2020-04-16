package integration;

import com.google.gson.Gson;
import entity.ChatRoom;
import io.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import util.IntegrationTester;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * method : POST
 * url : /room
 */
public class TestCreateRoom {
    private static final IntegrationTester tester = new IntegrationTester();
    private Gson gson = new Gson();

    @BeforeClass
    public static void beforeClass() {
        tester.startServerForTest();
    }
    @Test
    public void testCreateRoom_whensSuccess() {
        ChatRoom createRoom = new ChatRoom("room1",2);
        ChatRoom expectedRoom = new ChatRoom(1, "room1",2);
        String requestBody = gson.toJson(createRoom);
        String expectedBody = gson.toJson(expectedRoom);
        tester.integrationTest(l -> doNothing(),
                "POST /room\\n\\n"+requestBody,
                new Response(201, "created", expectedBody)
        );
    }
    /*
    여러번 요청한 경우, id의 자동 increment 가 동작하는가?
     */
    @Test
    public void testCreateRoom_whenMultipleCallCheckAutoIdIncrement() throws IOException {
        ChatRoom firstChatRoom = new ChatRoom("room1",2);

        SocketChannel client = SocketChannel.open(tester.getServerAddress());
        tester.sendRequest("POST /room\\n\\n"
                + gson.toJson(firstChatRoom), client);
        tester.receiveResponse(client);
        client.close();

        ChatRoom secondRoom = new ChatRoom("room2", 2);
        ChatRoom expectedRoom = new ChatRoom(1,"room2", 2);
        tester.integrationTest(l -> doNothing(),
                "POST /room\\n\\n"
                        + gson.toJson(secondRoom),
                new Response(201, "created", gson.toJson(expectedRoom))
                );
    }

    @Test
    public void testCreateRoom_whenBadRequestWithEmptyBody() throws IOException {
        tester.integrationTest(l -> doNothing(),
                "POST /room\\n\\n",
                new Response(400, "bad request:empty body"));
    }

    @Test
    public void testCreateRoom_whenBadRequestWithMismatchArguments() {
        tester.integrationTest(l -> doNothing(),
                "POST /room\\n\\nabcd",
                new Response(400, "bad request:mismatch ChatRoom field with body"));
    }

    public void doNothing() {}


}
