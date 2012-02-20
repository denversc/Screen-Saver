package screensaver;

import java.lang.ref.WeakReference;

import screensaver.ui.GoScreen;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;

public class GoScreenWorkerImpl implements GoScreen.Worker {

    private WeakReference screenshotThreadRef;
    private final Object screenshotThreadRefMutex;

    public GoScreenWorkerImpl() {
        this.screenshotThreadRefMutex = new Object();
    }

    public boolean isScreenshotInProgress() {
        final WeakReference ref = this.screenshotThreadRef;
        if (ref != null) {
            final Thread thread = (Thread) ref.get();
            if (thread != null && thread.isAlive()) {
                return true;
            }
        }
        return false;
    }

    public void takeScreenshot(GoScreen screen) {
        if (screen == null) {
            throw new NullPointerException("screen==null");
        }

        if (this.isScreenshotInProgress()) {
            return;
        }

        final EditField delayField = screen.getDelayField();
        final String delayStr = delayField.getText();
        int delay = 0;
        if (delayStr != null) {
            final String delayStrTrimmed = delayStr.trim();
            if (delayStrTrimmed.length() > 0) {
                try {
                    delay = Integer.parseInt(delayStrTrimmed);
                } catch (final NumberFormatException e) {
                    Dialog.alert("ERROR: invalid delay: " + delayStrTrimmed);
                    delayField.setFocus();
                    return;
                }
            }
        }

        final ScreenshotRunnable.Listener listener =
            new ScreenshotListenerImpl(screen);
        final Runnable runnable = new ScreenshotRunnable(delay, listener);
        final Thread thread = new Thread(runnable);

        synchronized (this.screenshotThreadRefMutex) {
            if (!this.isScreenshotInProgress()) {
                thread.start();
                final WeakReference ref = new WeakReference(thread);
                this.screenshotThreadRef = ref;
            }
        }

    }

    public static class ScreenshotListenerImpl implements
            ScreenshotRunnable.Listener {

        private final GoScreen screen;

        public ScreenshotListenerImpl(GoScreen screen) {
            if (screen == null) {
                throw new NullPointerException("screen==null");
            }
            this.screen = screen;
        }

        public void screenshotTaken(final Bitmap bitmap) {
            if (bitmap == null) {
                throw new NullPointerException("bitmap==null");
            }

            final Application app = this.screen.getApplication();
            if (app == null) {
                throw new IllegalStateException("no application instance");
            }

            final Runnable runnable = new Runnable() {
                public void run() {
                    ScreenshotListenerImpl.this.screen.addScreenshot(bitmap);
                    app.requestForeground();
                }
            };

            app.invokeAndWait(runnable);
        }
    }
}
