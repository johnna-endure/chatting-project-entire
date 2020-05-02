package app.activity;

import app.controller.ChatRoomController;
import app.entity.ChatRoom;
import app.event.MessageEventLoop;
import app.io.NIOUtils;
import app.scanner.ScannerUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class ChatRoomActivity extends Activity<ChatRoom> {
    private final static Logger logger = LogManager.getLogger(MainActivity.class);
    private ChatRoomController chatRoomController;
    private NIOUtils nioUtils = new NIOUtils();
    private SocketChannel chatMessageSender;
    private MessageEventLoop messageEventLoop;

    public ChatRoomActivity() {
        init();
    }

    private void init() {
        chatRoomController = new ChatRoomController();
        try {
            chatMessageSender = SocketChannel.open();
            chatMessageSender.configureBlocking(false);
        } catch (IOException e) {
            logger.atError().withThrowable(e).log(e.getMessage());
        }
    }

    @Override
    public void runWithObject(ChatRoom chatRoom) {
        connectProcess(chatRoom, chatMessageSender); // 예외처리 보류

        messageEventLoop = new MessageEventLoop(chatMessageSender);
        messageEventLoop.start();

        logger.debug("[runWithObject] 전달받은 ChatRoom = {}", chatRoom);
        System.out.printf("방이름 : %s에 입장합니다.\n채팅방을 나가려면 !exit를 입력하세요.\n ", chatRoom.getName());
        while(true) {
            String msg = ScannerUtil.getScanner().nextLine();
            if(msg.equals("!exit")) {
                exitProcess(chatRoom.getId());
                return;
            }
            //메세지 처리 로직
            sendMessage(msg);
        }
    }

    /**
     * 연결에 실패해도 다시 연결하고 싶을 경우, 다시 연결을 시도할 수 있는
     * 루프를 가지고 있습니다.
     *
     * 연결에 실패해서 방을 나가게 될 경우 ChatRoom 서버에서 exitRoom 요청을 보내야함 <- 미구현
     *
     * @param chatRoom 현재 방정보를 가진 객체
     * @param sender 클라이언트 채널
     * @return Message 서버에 연결된 경우 true, 실패한 경우 false 반환
     */
    private boolean connectProcess(ChatRoom chatRoom, SocketChannel sender) {
        while(true){
            if(!connectMessageServer(chatRoom.getPort(), sender)) {
                System.out.println("채팅 서버와 연결 실패. 다시 연결하겠습니까? [y/n]");
                String command = ScannerUtil.getScanner().nextLine();
                if(command.equals("n")){
                    return false;
                } else if(command.equals("y")){
                    continue;
                }
                return false;
            }else{
                System.out.println("연결 성공");
                return true;
            }
        }
    }

    private boolean connectMessageServer(int port, SocketChannel sender) {
        try {
            sender.connect(new InetSocketAddress("localhost", port));
            return sender.finishConnect();
        } catch (IOException e) {
            logger.atError().withThrowable(e).log(e.getMessage());
            return false;
        }
    }

    private void sendMessage(String message) {
        nioUtils.write(message, chatMessageSender);
    }

    /**
     * 채팅룸서버와 챗서버 둘다 나가기 요청이 들어가야된다.
     */
    public void exitProcess(int roomId) {
        nioUtils.sendRequest("!exit",chatMessageSender);
        chatRoomController.exitRoom(roomId);
    }



    public void setChatRoomController(ChatRoomController chatRoomController) {
        this.chatRoomController = chatRoomController;
    }
}
