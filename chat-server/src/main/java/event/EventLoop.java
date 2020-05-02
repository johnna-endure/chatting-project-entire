package event;

import java.nio.channels.Selector;

public class EventLoop extends Thread{
    private Selector selector;

    public Selector getSelector() {
        return selector;
    }
}
