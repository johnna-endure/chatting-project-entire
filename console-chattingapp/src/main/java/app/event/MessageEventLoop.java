package app.event;

import app.io.NIOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class MessageEventLoop extends Thread{
    private Selector selector;
    private SocketChannel chatMsgSender;
    private NIOUtils nioUtils = new NIOUtils();
    private boolean runningFlag = true;

    private static Logger logger = LogManager.getLogger(MessageEventLoop.class);

    public MessageEventLoop(SocketChannel chatMsgSender) {
        this.chatMsgSender = chatMsgSender;
        init();
    }

    private void init() {
        try {
            selector = Selector.open();
            chatMsgSender.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while(runningFlag) {
                selector.select(key -> printMessage(key));

                sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void printMessage(SelectionKey key) {
        logger.debug("[printMessage] 호출");
        String message = readMessage(key);
        if(message == null) return;
        System.out.println("[메세지] " + message);
    }

    private String readMessage(SelectionKey key) {
        if(key.isValid() && key.isReadable()) {
            return nioUtils.read((SocketChannel) key.channel());
        }
        return null;
    }

}
