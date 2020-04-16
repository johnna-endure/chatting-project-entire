package app.enums.menu;

public enum MainMenu {
    CREATE_ROOM(1),
    GET_ROOM_LIST(2),
    JOIN_ROOM(3),
    EXIT_APP(4);

    private final int menuNum;

    MainMenu(int menuNum) {
        this.menuNum = menuNum;
    }

    public int getMenuNum() {
        return menuNum;
    }
}
