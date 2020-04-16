import app.activity.MainActivity;
import app.manager.dependency.DependencyManager;
import app.scanner.ScannerUtil;

public class Main {
    public static void main(String[] args) {
        DependencyManager dependencyManager = new DependencyManager();
        dependencyManager.initializeApp();
        dependencyManager.getMainActivity().start();

        ScannerUtil.getScanner().close();
    }
}
