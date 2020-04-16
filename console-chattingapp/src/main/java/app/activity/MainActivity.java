package app.activity;

import app.controller.MainController;
import app.entity.ChatRoom;
import app.enums.menu.MainMenu;
import app.io.response.Response;
import app.scanner.ScannerUtil;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MainActivity extends Activity<ChatRoom> {
    private final static Logger logger = LogManager.getLogger(MainActivity.class);
    private Gson gson = new Gson();
    private MainController mainController;

    @Override
    public void run() {
        logger.debug("[run] 호출됨.");
        System.out.println("채팅앱 시작");
        while(true){
            System.out.println("메뉴를 선택하세요.");
            System.out.println("1.채팅방 생성 2. 채팅방 리스트 3.참가하기 4.종료");

            Scanner scanner = ScannerUtil.getScanner();
            int menuNum = Integer.parseInt(scanner.nextLine());
            if(menuNum == MainMenu.CREATE_ROOM.getMenuNum()) {
                Optional<ChatRoom> chatRoomOpt = createRoom();
                if(chatRoomOpt.isEmpty()) {
                    System.out.println("시작 메뉴로 다시 돌아갑니다.");
                    continue;
                }
                ChatRoomActivity chatRoomActivity = new ChatRoomActivity();
                chatRoomActivity.startWithObject(chatRoomOpt.get());
            }
            if(menuNum == MainMenu.GET_ROOM_LIST.getMenuNum()){
                printRoomList();
            }
            if(menuNum == MainMenu.JOIN_ROOM.getMenuNum()) {
                logger.debug("JOIN_ROOM 메뉴 선택됨.");
                Optional<ChatRoom> targetRoomOpt = joinRoom();
                if(targetRoomOpt.isEmpty()) {
                    System.out.println("방에 참가할 수 없습니다.");
                    continue;
                }
                ChatRoomActivity chatRoomActivity = new ChatRoomActivity();
                chatRoomActivity.startWithObject(targetRoomOpt.get());
            }
        }
    }

    private Optional<ChatRoom> createRoom(){
        logger.debug("createRoom 메뉴 선택됨.");
        try {
            ChatRoom createdRoom = mainController.createRoom();
            logger.debug("createdRoom = {}", createdRoom);
            return Optional.of(createdRoom);
            //Response에서 방 정보 추출하고 Room Activity 로 이동.
        } catch (IOException e) {
            logger.atError().withThrowable(e).log(e.getMessage());
            System.out.println("채팅방 생성에 실패했습니다.");
            return Optional.empty();
        }
    }

    private void printRoomList() {
        System.out.println("채팅방 리스트");
        try {
            List<ChatRoom> roomList = mainController.getRoomList();
            for (ChatRoom chatRoom : roomList) {
                System.out.printf("번호 : %d, 방이름 : %s %d/%d\n",
                        chatRoom.getId(), chatRoom.getName(),
                        chatRoom.getCurrentSize(), chatRoom.getMaxSize());
            }
        } catch (IOException e) {
            logger.atError().withThrowable(e).log(e.getMessage());
        }
    }
    private Optional<ChatRoom> joinRoom() {
        try {
            return mainController.joinRoom();
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

}
