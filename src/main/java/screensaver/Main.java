package screensaver;

import screensaver.ui.GoScreen;

import net.rim.device.api.ui.UiApplication;

public class Main extends UiApplication {

    public static void main(String[] args) {
        final GoScreen.Worker goScreenWorker = new GoScreenWorkerImpl();
        final GoScreen screen = new GoScreen(goScreenWorker);
        final Main app = new Main();
        app.pushScreen(screen);
        app.enterEventDispatcher();
    }
}
