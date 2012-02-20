package screensaver;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;

public class ScreenshotRunnable implements Runnable {

    private final int delay;
    private final Listener listener;

    public ScreenshotRunnable(int delay, Listener listener) {
        if (listener == null) {
            throw new NullPointerException("listener==null");
        } else if (delay < 0) {
            throw new IllegalArgumentException("delay==" + delay);
        }
        this.delay = delay;
        this.listener = listener;
    }

    public void doDelay() {
        final long delayMillis = this.delay * 1000;
        if (delayMillis != 0) {
            try {
                Thread.sleep(delayMillis);
            } catch (final InterruptedException e) {
                // not sure what happened; keep going as if nothing went wrong
            }
        }
    }

    public void run() {
        this.doDelay();
        this.takeScreenshot();
    }

    public void takeScreenshot() {
        final int width = Display.getWidth();
        final int height = Display.getHeight();
        final Bitmap bitmap = new Bitmap(width, height);
        Display.screenshot(bitmap);
        this.listener.screenshotTaken(bitmap);
    }

    public static interface Listener {

        public void screenshotTaken(Bitmap bitmap);

    }
}
