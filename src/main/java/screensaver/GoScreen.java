package screensaver;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;

public class GoScreen extends MainScreen {

    private final Worker worker;
    private final ButtonField screenshotButton;
    private final EditField delayField;
    private final Manager screenshotManager;

    public GoScreen(Worker worker) {
        super(USE_ALL_WIDTH);

        if (worker == null) {
            throw new NullPointerException("worker==null");
        }
        this.worker = worker;

        this.setTitle("ScreenSaver");

        this.screenshotButton =
            new ButtonField("Take Screenshot", ButtonField.CONSUME_CLICK
                | ButtonField.NEVER_DIRTY | Field.USE_ALL_WIDTH);
        this.screenshotButton.setRunnable(new ScreenshotButtonRunnable());

        this.delayField =
            new EditField("Delay (seconds): ", "10",
                TextField.DEFAULT_MAXCHARS, BasicEditField.FILTER_NUMERIC
                    | Field.USE_ALL_WIDTH);

        this.screenshotManager = new VerticalFieldManager();

        this.add(this.screenshotButton);
        this.add(this.delayField);
        this.add(this.screenshotManager);
    }

    public Field addScreenshot(Bitmap bitmap) {
        if (bitmap == null) {
            throw new NullPointerException("bitmap==null");
        }
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final int scaledWidth = width / 3;
        final int scaledHeight = height / 3;
        final Bitmap scaledBitmap = new Bitmap(scaledWidth, scaledHeight);
        bitmap.scaleInto(scaledBitmap, Bitmap.FILTER_LANCZOS);

        final BitmapField bitmapField =
            new BitmapField(scaledBitmap, Field.FOCUSABLE);
        final Border border =
            BorderFactory.createRoundedBorder(new XYEdges(10, 10, 10, 10));
        bitmapField.setBorder(Field.VISUAL_STATE_FOCUS, border);

        this.screenshotManager.insert(bitmapField, 0);
        return bitmapField;
    }

    public EditField getDelayField() {
        return this.delayField;
    }

    protected boolean onSavePrompt() {
        // prevent save/discard/cancel dialog when screen is closed
        return true;
    }

    private class ScreenshotButtonRunnable implements Runnable {
        public void run() {
            GoScreen.this.worker.takeScreenshot(GoScreen.this);
        }
    }

    public static interface Worker {

        public void takeScreenshot(GoScreen screen);

    }
}
