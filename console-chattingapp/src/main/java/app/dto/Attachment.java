package app.dto;

public class Attachment {
    private final String eventName, message;
    private final int roomNum;

    public Attachment(String eventName, String message, int roomNum) {
        this.eventName = eventName;
        this.message = message;
        this.roomNum = roomNum;
    }

    public String getMessage() {
        return message;
    }

    public String getEventName() {
        return eventName;
    }

    public int getRoomNum() {
        return roomNum;
    }
}
