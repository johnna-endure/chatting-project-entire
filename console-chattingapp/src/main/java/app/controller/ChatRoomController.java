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
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Optional;

public class ChatRoomController {
    private static Logger logger = LogManager.getLogger(ChatRoomController.class);
    private NIOUtils nioUtils = new NIOUtils();
    private Gson gson = new Gson();
    private SocketAddress chatRoomServerAddress = new InetSocketAddress("localhost", 9001);
    /**
     * 내부적으로 채팅룸 서버에 요청을 보냅니다.
     *
     * void 반환은 문제가 있다. 일단 패스
     *
     * @param roomId 나가려고 하는 방의 id
     * @throws IOException
     */
    public void exitRoom(int roomId) {
        System.out.println("방을 나가시겠습니까? [y/n]");
        String command = ScannerUtil.getScanner().nextLine();
        if(command.equals("y")){
            Response response = sendRequest("POST /room/"+roomId+"/exit", chatRoomServerAddress);
            if(response.getStatusCode() == 200) {
                System.out.println("방에서 나갑니다.");
                return;
            }
        }
        System.out.println("나가기 실패");
    }

    public Response sendRequest(String url, SocketAddress address) {
        try(SocketChannel client = SocketChannel.open(address)) {
            nioUtils.sendRequest(url,client);
            return nioUtils.receiveResponse(client);
        } catch (IOException e) {
            e.printStackTrace();
            return new Response(500,"internal server error");
        }
    }

}
