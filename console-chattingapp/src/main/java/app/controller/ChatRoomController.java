package app.controller;

import app.entity.ChatRoom;
import app.io.NIOUtils;
import app.io.response.Response;
import app.scanner.ScannerUtil;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Optional;

public class ChatRoomController {
    private static Logger logger = LogManager.getLogger(ChatRoomController.class);
    private NIOUtils nioUtils = new NIOUtils();
    private Gson gson = new Gson();

    public Optional<ChatRoom> exitRoom(int roomId) throws IOException {
        SocketChannel client = SocketChannel.open(
                new InetSocketAddress("localhost", 9001));

        System.out.println("방을 나가시겠습니까? [y/n]");
        String command = ScannerUtil.getScanner().nextLine();
        if(command.equals("y")){
            nioUtils.sendRequest("POST /room/"+roomId+"/exit",client);
            Response response = nioUtils.receiveResponse(client);
            client.close();
            Optional<String> bodyOpt = response.getResponseBodyOpt();
            if(bodyOpt.isEmpty()) {
                System.out.println("나가기 중 에러 발생.");
                logger.debug("[exitRoom] exit error 발생. Response = {}", response);
                return Optional.empty();
            }
            logger.debug("[exitRoom] 나가기 성공.");
            return Optional.of(gson.fromJson(bodyOpt.get(), ChatRoom.class));
        }
        return Optional.empty();
    }

}
