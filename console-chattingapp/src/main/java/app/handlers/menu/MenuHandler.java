package app.handlers.menu;

import app.annotations.menu.MenuMapping;
import app.entity.ChatRoom;
import app.enums.menu.Menu;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class MenuHandler {
    private static final Logger logger = LogManager.getLogger(MenuHandler.class);
    private Gson gson = new Gson();

    @MenuMapping(menu = Menu.CREATE_ROOM)
    public void createRoomMenu() throws IOException {
        logger.debug("[createRoomMenu] 핸들러 호출됨.");
        SocketChannel client = SocketChannel.open(
                new InetSocketAddress("localhost", 9001));
        ChatRoom chatRoom = new ChatRoom("room1", 2);
        String body = gson.toJson(chatRoom);
        client.write(ByteBuffer.wrap(("POST /room\\n\\n"+body).getBytes()));

        ByteBuffer buffer = ByteBuffer.allocate(1000);
        int reads = client.read(buffer);
        buffer.flip();
        byte[] dst = new byte[reads];
        buffer.get(dst);
        client.close();
        String response = new String(dst);
        logger.debug("[createRoomMenu] response = {}", response);
    }

    @MenuMapping(menu = Menu.REFRESH_ROOM_LIST)
    public void refreshMenu(){
        logger.debug("[refreshMenu] 핸들러 호출됨.");

    }

    @MenuMapping(menu = Menu.JOIN_ROOM)
    public void joinRoomMenu(){
        logger.debug("[joinRoomMenu] 핸들러 호출됨.");

    }

    @MenuMapping(menu = Menu.EXIT_APP)
    public void exitMenu(){
        logger.debug("[exitMenu] 핸들러 호출됨.");

    }
}
