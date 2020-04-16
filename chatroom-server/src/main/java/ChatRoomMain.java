import database.ChatRoomDatabase;
import manager.ServerDependencyManager;
import server.ChatRoomServer;

public class ChatRoomMain {
    public static void main(String[] args) {
        ChatRoomServer server = new ChatRoomServer();
        ServerDependencyManager dependencyManager = new ServerDependencyManager(9001);
        dependencyManager.setDependency(server, ChatRoomDatabase::new);
        server.start();
    }
}
