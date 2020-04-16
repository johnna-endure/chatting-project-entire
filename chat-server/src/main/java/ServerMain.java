import channel.ServerChannelManager;
import dto.Attachment;
import event.ServerEventController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Optional;
import java.util.Scanner;

public class ServerMain {
    private Selector selector;
    private ServerChannelManager channelManager;
    private ServerEventController eventController;

    public void init() {
        try { selector = Selector.open(); }
        catch (IOException e) {
            e.printStackTrace();
        }

        channelManager = new ServerChannelManager(
                new InetSocketAddress("localhost", 9000));
        eventController = new ServerEventController().setSelector(selector);
        eventController.start();
    }

    public Selector getSelector() {
        return selector;
    }
    public ServerChannelManager getServerChannelManager() {
        return channelManager;
    }
    public ServerEventController getEventController() {
        return eventController;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("채팅 서버를 시작합니다.");
        ServerMain main = new ServerMain();
        main.init();

        Selector selector = main.getSelector();
        ServerChannelManager channelManager = main.getServerChannelManager();

        main.bindLoop();
        main.accept();

    }

    public void bindLoop() {
        while(true) {
            System.out.println("서버 바인딩 중...");
            Optional<ServerSocketChannel> socketChannelOptional = channelManager.bind();
            if(socketChannelOptional.isPresent()) {
                System.out.println("바인딩 성공.");
                break;
            }
            System.out.println("바인딩 실패. 다시 시도할까요?[y/n]");
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            if(command.equals("n")){
                System.out.println("서버가 종료됩니다.");
                System.exit(0);
            }
            if(command.equals("y")){
                System.out.println("바인딩을 다시 시도합니다.");
            }
        }
    }

    public void accept() {
        ServerSocketChannel readyAcceptChannel = channelManager.getServerSocketChannel();
        Attachment attachment = new Attachment("accept", null);
        try {
            readyAcceptChannel.register(selector, SelectionKey.OP_ACCEPT, attachment);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }
}
