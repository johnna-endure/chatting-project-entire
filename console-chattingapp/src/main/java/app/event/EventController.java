package app.event;

import java.nio.channels.Selector;

/*
컴포넌트들이 관계를 설정하고 Main 에 공개되어 사용되어지는 클래스.
Main 에서 selector 를 주입받고 start()로 시작.
 */
public class EventController {
    private EventLoop eventLoop;
    private EventHandler eventHandler;
    private Selector selector;

    public void start(){
        makeRelation();
        eventLoop.start();
    }

    private void makeRelation() {
        eventHandler = new EventHandler().setSelector(selector);
        eventLoop = new EventLoop()
                .setSelector(selector)
                .setEventHandler(eventHandler);
    }

    public EventController setSelector(Selector selector) {
        this.selector = selector;
        return this;
    }
}
