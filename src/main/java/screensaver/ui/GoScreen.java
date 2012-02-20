package screensaver.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;

public class GoScreen extends MainScreen {

    private final Worker worker;
    private final ButtonField screenshotButton;
    private final EditField delayField;
    private final ScreenshotsField screenshots;

    public GoScreen(Worker worker) {
        super(NO_VERTICAL_SCROLL);

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

        this.screenshots = new ScreenshotsField();

        final Background background =
            BackgroundFactory.createSolidBackground(Color.DIMGRAY);
        this.setBackground(background);

        this.add(this.screenshotButton);
        this.add(this.delayField);
        this.add(this.screenshots);
    }

    public void addScreenshot(Bitmap bitmap) {
        if (bitmap == null) {
            throw new NullPointerException("bitmap==null");
        }

        final int scaledWidth = bitmap.getWidth() / 2;
        final int scaledHeight = bitmap.getHeight() / 2;
        final Bitmap scaledBitmap = new Bitmap(scaledWidth, scaledHeight);
        bitmap.scaleInto(scaledBitmap, Bitmap.FILTER_LANCZOS);

        this.screenshots.add(scaledBitmap);
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
