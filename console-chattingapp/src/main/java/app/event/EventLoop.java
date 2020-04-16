package app.event;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.logging.Logger;

/*
어떻게 테스트할 것인가? 그전에 앞서서 테스트할 필요가 있는가?를 따져볼 필요가 있다.

fine 레벨의 로깅을 추가해야 할지도.
 */
public class EventLoop extends Thread{
    Selector selector;
    EventHandler eventHandler;

    Logger logger = Logger.getLogger(EventLoop.class.getCanonicalName());

    @Override
    public void run() {
        while(true) {
            try {
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Set<SelectionKey> selectionKeySet = selector.selectedKeys();
            for (SelectionKey key : selectionKeySet) {
                eventHandler.delegate(key);
            }

            // 0.5초 쉰다.
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public EventLoop setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
        return this;
    }
    public EventLoop setSelector(Selector selector) {
        this.selector = selector;
        return this;
    }
}
