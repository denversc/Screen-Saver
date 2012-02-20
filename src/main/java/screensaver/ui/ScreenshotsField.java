package screensaver.ui;

import java.util.Vector;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.extension.component.PictureScrollField;
import net.rim.device.api.ui.extension.component.PictureScrollField.HighlightStyle;

class ScreenshotsField extends PictureScrollField {

    private final Vector scrollEntries;

    public ScreenshotsField() {
        this.scrollEntries = new Vector();
        this.setLabelsVisible(false);
        this.setHighlightStyle(HighlightStyle.ILLUMINATE_WITH_ROUND_BORDER);
    }

    public void add(Bitmap bitmap) {
        if (bitmap == null) {
            throw new NullPointerException("bitmap==null");
        }

        final PictureScrollField.ScrollEntry entry =
            new PictureScrollField.ScrollEntry();
        entry.setPicture(bitmap);
        this.scrollEntries.addElement(entry);

        final PictureScrollField.ScrollEntry[] entries;
        final int size;
        synchronized (this.scrollEntries) {
            size = this.scrollEntries.size();
            entries = new PictureScrollField.ScrollEntry[size];
            this.scrollEntries.copyInto(entries);
        }

        this.setData(entries, size - 1);
    }
}