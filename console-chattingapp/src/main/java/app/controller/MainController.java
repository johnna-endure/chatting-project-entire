package app.controller;

import app.entity.ChatRoom;
import app.io.NIOUtils;
import app.io.response.Response;
import app.parser.ResponseParser;
import app.scanner.ScannerUtil;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MainController {
    private static final Logger logger = LogManager.getLogger(MainController.class);
    private Gson gson = new Gson();
    private NIOUtils nioUtils = new NIOUtils();
    private SocketAddress chatRoomServerAddress = new InetSocketAddress("localhost", 9001);

    public ChatRoom createRoom() throws IOException {
        logger.debug("[createRoomMenu] 핸들러 호출됨.");
        ChatRoom newRoom = createNewRoomUsingConsoleInput(ScannerUtil.getScanner());
        Response response = sendRequest("POST /room\\n\\n" + gson.toJson(newRoom), chatRoomServerAddress);
        return gson.fromJson(response.getResponseBodyOpt().orElseThrow(), ChatRoom.class);
    }


    private ChatRoom createNewRoomUsingConsoleInput(Scanner scanner) {
        System.out.println("방을 생성합니다. 필요한 정보를 입력해주세요.\n방 이름 : ");
        String roomName = scanner.nextLine();
        System.out.println("최대 인원 : ");
        int maxSize = Integer.parseInt(scanner.nextLine());
        ChatRoom newRoom = new ChatRoom(roomName, maxSize);
        logger.debug("[createNewRoomUsingConsoleInput] 유저 입력으로 생성한 newRoom = {}",newRoom);
        return newRoom;
    }


    public List<ChatRoom> getRoomList() throws IOException {
        logger.debug("[getRoomList] 핸들러 호출됨.");
        Response response = sendRequest("GET /rooms", chatRoomServerAddress);

        Optional<String> bodyOpt = response.getResponseBodyOpt();
        if(bodyOpt.isEmpty()) return Collections.emptyList();

        List<Object> roomList = gson.fromJson(bodyOpt.get(), List.class);
        logger.debug("[getRoomList] json > List :: roomList = {}", roomList);
        return roomList.stream().map(json -> gson.fromJson(String.valueOf(json), ChatRoom.class))
                .collect(Collectors.toList());

    }

    public Optional<ChatRoom> joinRoom() throws IOException {
        logger.debug("[joinRoomMenu] 핸들러 호출됨.");
        System.out.println("참가할 방의 번호를 입력하세요.\n방 번호 : ");
        int roomId = Integer.parseInt(ScannerUtil.getScanner().nextLine());

        Response response = sendRequest("POST /room/"+roomId, chatRoomServerAddress);

        Optional<String> bodyOpt = response.getResponseBodyOpt();
        if(bodyOpt.isEmpty()) {
            return Optional.empty();
        }
        ChatRoom joinRoom = gson.fromJson(bodyOpt.get(), ChatRoom.class);
        return Optional.of(joinRoom);
    }

    public Response sendRequest(String url, SocketAddress address) {
        try (SocketChannel client = SocketChannel.open(address)) {
            nioUtils.sendRequest(url, client);
            return nioUtils.receiveResponse(client);
        } catch (IOException e) {
            e.printStackTrace();
            return new Response(500, "server internal error");
        }
    }
}

