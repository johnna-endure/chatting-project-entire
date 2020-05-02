package app.manager.dependency;

import app.activity.ChatRoomActivity;
import app.activity.MainActivity;
import app.controller.ChatRoomController;
import app.controller.MainController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DependencyManager {
    private MainController mainController;
    private MainActivity mainActivity;

    private static final Logger logger = LogManager.getLogger(DependencyManager.class);

    public void initializeApp() {
        logger.debug("[initializeApp] 호출됨.");
        mainActivity = new MainActivity();
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }
}
