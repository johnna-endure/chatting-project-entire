package handler;

import annotations.RequestMapping;
import com.google.gson.Gson;
import entity.ChatRoom;
import io.request.Method;
import io.request.Request;
import io.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import room.RoomThread;
import room.RoomThreadPool;

import java.util.Optional;

public class ChatServerHandler {
    private Logger logger = LogManager.getLogger(ChatServerHandler.class);
    private RoomThreadPool roomThreadPool;
    private Gson gson = new Gson();

    public ChatServerHandler() {
        init();
    }

    public void init() {
        roomThreadPool = new RoomThreadPool();
    }

    /**
     * RoomThread를 생성해 RoomThreadPool에 추가합니다.
     * ChatServer 계층에서 할 수 있는 기능은 RoomThreadPool에 RoomThread를 생성하거나
     * 제거하는 기능입니다.
     *
     *
     * @param request
     * @return 생성에 성공한 경우 상태코드 201, 실패한 경우 상태코드 400 의 응답을 보냅니다.
     */
    @RequestMapping(method = Method.POST, url = "/room")
    public Response createRoomThread(Request request) {
        Optional<String> bodyOpt = request.getBodyOpt();
        if(bodyOpt.isEmpty()) {
            return new Response(400, "request body is null");
        }
        ChatRoom newRoom = gson.fromJson(bodyOpt.get(), ChatRoom.class);
        RoomThread newRoomThread = createRoomThread();
        logger.debug("created roomThread. port is {}", newRoomThread.getPort());
        roomThreadPool.putRoomThread(newRoomThread.getPort(), newRoomThread);

        newRoom.setPort(newRoomThread.getPort());
        return new Response(201,"success createRoomThread", gson.toJson(newRoom));
    }

    /**
     * RoomThreadPool 에 존재하는 RoomThread 를 제거합니다.
     * @param port RoomThreadPool 에서 RoomThread 를 찾기 위한 key 로 port 값을 사용합니다.
     * @return 성공시 상태코드 200의 응답을 보냅니다.
     *
     * 삭제에 실패한 경우의 예외처리가 필요함.
     */
    @RequestMapping(method = Method.DELETE, url = "/room/{port}")
    public Response deleteRoomThread(int port) {
        logger.debug("[deleteRoomThread] 호출 :: port[parameter] = {}", port);
        roomThreadPool.deleteRoomThread(port);
        return new Response(200,"RoomThread 삭제됨.");
    }


    private RoomThread createRoomThread() {
        RoomThread roomThread = new RoomThread();
        roomThread.start();
        return roomThread;
    }

    public void setRoomThreadPool(RoomThreadPool roomThreadPool) {
        this.roomThreadPool = roomThreadPool;
    }
}
