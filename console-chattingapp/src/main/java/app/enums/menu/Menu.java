package app.enums.menu;

public enum Menu {
    CREATE_ROOM(1),
    REFRESH_ROOM_LIST(2),
    JOIN_ROOM(3),
    EXIT_APP(4);

    private final int menuNum;

    Menu(int menuNum) {
        this.menuNum = menuNum;
    }

    public int getMenuNum() {
        return menuNum;
    }
}
