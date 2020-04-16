package event;

import java.nio.channels.Selector;

/*
컴포넌트들이 관계를 설정하고 Main 에 공개되어 사용되어지는 클래스.
Main 에서 selector 를 주입받고 start()로 시작.
 */
public class ServerEventController {
    private Selector selector;
    private ServerEventHandler serverEventHandler;
    private ServerPollingEventLoop serverEventLoop;

    public ServerEventController setSelector(Selector selector) {
        this.selector = selector;
        return this;
    }

    public void start(){
        makeRelation();
        serverEventLoop.start();
    }

    private void makeRelation() {
        serverEventHandler = new ServerEventHandler().setSelector(selector);
        serverEventLoop = new ServerPollingEventLoop()
                .setServerEventHandler(serverEventHandler)
                .setSelector(selector);
    }
}
