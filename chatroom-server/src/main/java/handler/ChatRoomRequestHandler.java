package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import database.ChatRoomDatabase;
import entity.ChatRoom;
import io.NIOUtils;
import io.request.Method;
import io.request.Request;
import io.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import parser.response.ResponseParser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Optional;

public class ChatRoomRequestHandler {
    private ChatRoomDatabase database;
    private SocketAddress chatServerAddress = new InetSocketAddress("localhost", 9003);
    private Gson gson = new Gson();
    private NIOUtils nioUtils = new NIOUtils();
    private static final Logger logger = LogManager.getLogger(ChatRoomRequestHandler.class);

    public ChatRoomRequestHandler setDatabase(ChatRoomDatabase database) {
        this.database = database;
        return this;
    }

    /**
     *
     * @param id 찾을 방의 id
     * @return 성공시 상태코드 200과 body에 찾은 방의 객체, 실패시 상태코드 204를 반환.
     */
    @RequestMapping(method = Method.GET, url = "/room/{id}")
    public Response getRoom(int id){
        logger.info("[getRoom] Request[method = GET, url = /room/{} ", id);
        Optional<ChatRoom> chatRoomOpt = database.getRoom(id);
        if(chatRoomOpt.isEmpty()){
            return new Response(204, "no room corresponds to this id");
        }
        ChatRoom chatRoom = chatRoomOpt.get();
        return  new Response(200, "success", gson.toJson(chatRoom));
    }

    /**
     * 성공,실패 둘다 상태코드 200을 반환한다. body에 방들의 객체 정보가 들어있다.
     * @return
     */
    @RequestMapping(method = Method.GET, url = "/rooms")
    public Response getRooms() {
        logger.info("[getRooms] Request[method = GET, url = /rooms ");
        List<ChatRoom> roomList = database.getRooms();
        return new Response(200, "success", gson.toJson(roomList));
    }

    /**
     *
     * @param request
     * @return 성공시 : 상태코드 = 201, body = 생성한 ChatRoom 정보 <br>
     *     실패시 : 상태코드 = 400[요청이 잘못된 경우], 500[RoomThread 생성 실패]
     */
    @RequestMapping(method = Method.POST, url = "/room")
    public Response createRoom(Request request) {
        logger.info("[createRoom] Request = {}", request);
        Optional<String> requestBodyOpt = request.getBodyOpt();
        if(requestBodyOpt.isEmpty()) {
            return new Response(400, "bad request:empty body");
        }
        try{
            ChatRoom newRoom = gson.fromJson(requestBodyOpt.get(), ChatRoom.class);
            //roomThread 생성 로직
            Response responseAboutRoomThread = createRoomThread("POST /room\\n\\n"+gson.toJson(newRoom));
            if(responseAboutRoomThread.getStatusCode() != 201) return responseAboutRoomThread;

            newRoom = gson.fromJson(
                    responseAboutRoomThread.getResponseBodyOpt().get(), ChatRoom.class);
            newRoom = database.createRoom(newRoom);
            return new Response(201, "created", gson.toJson(newRoom));
        }catch (JsonSyntaxException e) {
            logger.atError().withLocation().withThrowable(e).log("mismatch arguments. Request = {}", request);
            return new Response(400, "bad request:mismatch ChatRoom field with body");
        }
    }

    /**
     * ChatRoomServer 에서 판단해 방 생성이 가능한 경우, 방정보에서 현재원을 +1 해서 방정보를 갱신합니다.
     *
     * @param id ChatRoom 의 id
     * @return <p> 채팅방에 참가 가능한 경우 200의 상태코드와 방 정보 객체를 body 에 포함한 응답을 반환합니다.
     * 실패한 경우 204의 상태코드와 설명을 가진 응답을 반환합니다.</p>
     */
    @RequestMapping(method = Method.POST, url = "/room/{id}")
    public Response joinRoom(int id) {
        logger.info("[joinRoom] Request[method = POST, url = /room/{}",id);
        Optional<ChatRoom> targetRoomOpt = database.getRoom(id);
        if(targetRoomOpt.isEmpty()){
            return new Response(204, "no room corresponds to id");
        }

        ChatRoom targetRoom = targetRoomOpt.get();
        if(targetRoom.getCurrentSize() == targetRoom.getMaxSize()) {
            return new Response(204, "room is full");
        }
        targetRoom.setCurrentSize(targetRoom.getCurrentSize()+1);
        return new Response(200, "success", gson.toJson(targetRoom));
    }

    /**
     * 응답의 body에 현재방의 현재원을 1명 줄인 ChatRoom을 보낸다.
     * body가 null 인 경우 나가기 실패로 간주한다.
     * @param id 방의 id
     * @return 성공한 경우 상태코드=200, body=바뀐 chatRoom, 실패한 경우 상태코드=400
     */
    @RequestMapping(method = Method.POST, url = "/room/{id}/exit")
    public Response exitRoom(int id) {
        logger.info("[exitRoom] method = POST, url =/room/{}/exit", id);
        Optional<ChatRoom> chatRoomOpt = database.getRoom(id);
        if(chatRoomOpt.isEmpty()) {
            return new Response(400, "해당 id에 맞는 방이 없습니다.");
        }
        ChatRoom targetRoom = chatRoomOpt.get();
        targetRoom.setCurrentSize(targetRoom.getCurrentSize()-1);
        //방의 인원이 0일 경우 방을 삭제
        if(targetRoom.getCurrentSize() == 0) {
            return deleteRoom(id);
        }
        return new Response(200, "exit success", gson.toJson(targetRoom));
    }

    /*
    ChatServer 에 RoomThread delete 요청 보내야된다.
    이 부분 많이 미흡하다. 나중에 데이터 변경에 따른 트랜잭션을 어떻게 구현할지 생각해보자.
     */
    @RequestMapping(method = Method.DELETE, url = "/room/{id}")
    public Response deleteRoom(int id) {
        logger.info("[deleteRoom] method = DELETE, url = /room/{}", id);
        Optional<ChatRoom> targetRoomOpt = database.getRoom(id);
        if(targetRoomOpt.isEmpty()){
            return new Response(204,"no room correspond to id");
        }
        // 일단 이 부분은 문제가 많다. 트랜잭션의 부재.
        ChatRoom targetRoom = targetRoomOpt.get();
        boolean isDeleted = database.deleteRoom(targetRoom);
        isDeleted = isDeleted & roomThreadDeletion(targetRoom.getPort());

        return isDeleted ? new Response(200, "deletion success") :
                new Response(500, "deletion failed");
    }

    private boolean roomThreadDeletion(int port) {
        Response response = sendRequest("DELETE /room/"+port, chatServerAddress);
        return response.getStatusCode() == 200;
    }

    private Response createRoomThread(String url){
        Response response = sendRequest(url, chatServerAddress);
        if(response.getStatusCode() != 201)
            new Response(500, "create room thread fail");
        return response;
    }

    public Response sendRequest(String url, SocketAddress address) {
        try(SocketChannel sender = SocketChannel.open(chatServerAddress)) {
            nioUtils.write(url, sender);
            return ResponseParser.messageConvertToResponse(nioUtils.read(sender));
        } catch (IOException e) {
            logger.atError().withLocation().withThrowable(e).log(e.getMessage());
            return new Response(500, "server internal error");
        }

    }
}
