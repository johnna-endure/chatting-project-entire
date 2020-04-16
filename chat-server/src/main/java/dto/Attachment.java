package dto;

public class Attachment {
    private final String eventName, message;

    public Attachment(String eventName, String message) {
        this.eventName = eventName;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getEventName() {
        return eventName;
    }
}
