package app;

import app.controller.MenuController;
import app.enums.menu.Menu;
import app.handlers.menu.MenuHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleApp {
    private final static Logger logger = LogManager.getLogger(ConsoleApp.class);
    private MenuController menuController;

    public void start() {
        logger.info("[start] app start");
        run();
    }

    private void run() {
        while(true) {
            //메뉴가 나타나기 전에 위에 채팅방 리스트가 표시된다.
            System.out.println("메뉴를 선택하세요.");
            System.out.println("1. 채팅방 만들기, 2.새로고침 3.채탕방 참가하기 4.종료");
            Scanner scanner = new Scanner(System.in);
            int menuNum = scanner.nextInt(); // 미스매치 예외 처리가능하도록 메서드로 감쌀것.
            Optional<Menu> menuOptional = Arrays.stream(Menu.values())
                    .filter(menu -> menu.getMenuNum() == menuNum)
                    .findFirst();

            if(menuOptional.isEmpty()) {
                //예외처리
                System.out.println("메뉴 입력 범위를 초과했습니다.");
                continue;
            }
            dispatch(menuOptional.get());
        }
    }

    private void dispatch(Menu menu) {
        try {
            menuController.dispatch(menu);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }
}
