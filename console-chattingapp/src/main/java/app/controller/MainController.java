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
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MainController {
    private static final Logger logger = LogManager.getLogger(MainController.class);
    private Gson gson = new Gson();
    private NIOUtils nioUtils = new NIOUtils();

    public ChatRoom createRoom() throws IOException {
        logger.debug("[createRoomMenu] 핸들러 호출됨.");
        SocketChannel client = SocketChannel.open(
                new InetSocketAddress("localhost", 9001));

        ChatRoom newRoom = createNewRoomUsingConsoleInput(ScannerUtil.getScanner());

        String request = "POST /room\\n\\n" + gson.toJson(newRoom);
        nioUtils.sendRequest(request, client);
        Response response = nioUtils.receiveResponse(client);
        client.close();

        ChatRoom createdRoom = gson.fromJson(response.getResponseBodyOpt().orElseThrow(), ChatRoom.class);
        logger.debug("[createRoom] created room = {}", createdRoom);
        return createdRoom;
    }


    private ChatRoom createNewRoomUsingConsoleInput(Scanner scanner) {
        System.out.println("방을 생성합니다. 필요한 정보를 입력해주세요.");
        System.out.println("방 이름 : ");
        String roomName = scanner.nextLine();
        System.out.println("최대 인원 : ");
        int maxSize = Integer.parseInt(scanner.nextLine());
        ChatRoom newRoom = new ChatRoom(roomName, maxSize);
        logger.debug("[createNewRoomUsingConsoleInput] 유저 입력으로 생성한 newRoom = {}",newRoom);
        return newRoom;
    }


    public List<ChatRoom> getRoomList() throws IOException {
        logger.debug("[getRoomList] 핸들러 호출됨.");
        SocketChannel client = SocketChannel.open(
                new InetSocketAddress("localhost", 9001));
        nioUtils.sendRequest("GET /rooms", client);
        Response response = nioUtils.receiveResponse(client);
        client.close();
        String body = response.getResponseBodyOpt().orElseThrow();
        List<Object> roomList = gson.fromJson(body, List.class);
        logger.debug("[getRoomList] json > List :: roomList = {}", roomList);
        return roomList.stream().map(json -> gson.fromJson(String.valueOf(json), ChatRoom.class))
                .collect(Collectors.toList());

    }

    public Optional<ChatRoom> joinRoom() throws IOException {
        logger.debug("[joinRoomMenu] 핸들러 호출됨.");
        System.out.println("참가할 방의 번호를 입력하세요.");
        System.out.println("방 번호 : ");
        int roomId = Integer.parseInt(ScannerUtil.getScanner().nextLine());
        SocketChannel client = SocketChannel.open(
                new InetSocketAddress("localhost", 9001));
        nioUtils.sendRequest("POST /room/"+roomId, client);
        Response response = nioUtils.receiveResponse(client);
        client.close();
        Optional<String> bodyOpt = response.getResponseBodyOpt();
        if(bodyOpt.isEmpty()) {
            logger.debug("[joinRoomMenu] targetRoom = null");
            return Optional.empty();
        }
        ChatRoom joinRoom = gson.fromJson(bodyOpt.get(), ChatRoom.class);
        logger.debug("[joinRoomMenu] targetRoom = {}", joinRoom);
        return Optional.of(joinRoom);
    }
}

