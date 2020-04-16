package app.activity;

import app.controller.ChatRoomController;
import app.entity.ChatRoom;
import app.scanner.ScannerUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Optional;

public class ChatRoomActivity extends Activity<ChatRoom> {
    private final static Logger logger = LogManager.getLogger(MainActivity.class);
    private ChatRoomController chatRoomController;

    public ChatRoomActivity() {
        chatRoomController = new ChatRoomController();
    }

    @Override
    public void runWithObject(ChatRoom chatRoom) {
        logger.debug("[runWithObject] 전달받은 ChatRoom = {}", chatRoom);
        System.out.printf("방이름 : %s에 입장합니다.\n", chatRoom.getName());
        System.out.println("채팅방을 나가려면 !exit를 입력하세요.");
        while(true) {
            String msg = ScannerUtil.getScanner().nextLine();
            System.out.println("[메세지] : "+msg);
            if(msg.equals("!exit")) {
                if(exitProcess(chatRoom.getId())) {
                    System.out.println("시작 메뉴로 돌아갑니다.");
                    return;
                }
            }
        }
    }

    /**
     *
     * @return 나가기에 성공한 경우 true, 실패한 경우 false 를 반환합니다.
     */
    public boolean exitProcess(int roomId) {
            Optional<ChatRoom> currentRoomOpt = Optional.empty();
            try {
                currentRoomOpt = chatRoomController.exitRoom(roomId);
                if(currentRoomOpt.isEmpty()) {
                    logger.debug("[exitProcess] 채팅룸서버에서 나가기 작업 실패. body = null");
                    System.out.println("나가기 실패. 채팅방으로 돌아갑니다.");
                    return false;
                }
                ChatRoom currentRoom = currentRoomOpt.get();
                System.out.printf("한 명이 나갔습니다. 현재 인원 정보 %d/%d",
                        currentRoom.getCurrentSize(),currentRoom.getMaxSize());
                return true;
            } catch (IOException e) {
                logger.atError().withThrowable(e).log(e.getMessage());
                System.out.println("에러 발생. 채팅방으로 돌아갑니다. 다시 시도해주세요.");
            }
            return false;
    }
    public void setChatRoomController(ChatRoomController chatRoomController) {
        this.chatRoomController = chatRoomController;
    }
}
