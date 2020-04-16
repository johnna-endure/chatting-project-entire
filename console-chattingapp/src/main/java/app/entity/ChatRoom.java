package app.entity;

public class ChatRoom {
    private int id;
    private final String name;
    private final int maxSize;
    private int currentSize;

    public ChatRoom(String name, int maxSize) {
      this(-1,name,maxSize);
    }
    //테스트용으로만 직접 사용되어야함.
    public ChatRoom(int id, String name, int maxSize) {
        this.id = id;
        this.name = name;
        this.maxSize = maxSize;
        currentSize = 1;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public ChatRoom setCurrentSize(int currentSize) {
        this.currentSize = currentSize;
        return this;
    }

    public int getCurrentSize() {
        return currentSize;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", maxSize=" + maxSize +
                ", currentSize=" + currentSize +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return this.id == ((ChatRoom) obj).getId();
    }
}
