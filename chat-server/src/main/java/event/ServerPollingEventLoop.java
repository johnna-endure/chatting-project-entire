package event;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.logging.Logger;

/*
어떻게 테스트할 것인가? 그전에 앞서서 테스트할 필요가 있는가?를 따져볼 필요가 있다.

fine 레벨의 로깅을 추가해야 할지도.
 */
public class ServerPollingEventLoop extends Thread{
    Selector selector;
    ServerEventHandler serverEventHandler;
    Logger logger = Logger.getLogger(ServerPollingEventLoop.class.getCanonicalName());

    public ServerPollingEventLoop setSelector(Selector selector) {
        this.selector = selector;
        return this;
    }

    public ServerPollingEventLoop setServerEventHandler(ServerEventHandler serverEventHandler){
        this.serverEventHandler = serverEventHandler;
        return this;
    }


    @Override
    public void run() {
//        logger.info("[서버][이벤트루프] : run() 호출됨.");
        while(true) {
            try {
                selector.select();
////                logger.info("[서버][이벤트루프] : select() 호출됨.");
            } catch (IOException e) {
                e.printStackTrace();
            }

            Set<SelectionKey> selectionKeySet = selector.selectedKeys();
//            logger.info("[서버] selectionKeySet size : " + selectionKeySet.size());
            for (SelectionKey key : selectionKeySet) {
//                System.out.println("[서버] delegate 호출");
                serverEventHandler.delegate(key);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
