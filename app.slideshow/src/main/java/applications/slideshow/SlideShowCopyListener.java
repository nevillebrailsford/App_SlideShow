package applications.slideshow;

import application.replicate.CopyAndPaste;
import application.replicate.CopyAndPasteListener;

public class SlideShowCopyListener implements CopyAndPasteListener {

    private SlideShowApplication application = null;

    public SlideShowCopyListener(SlideShowApplication application) {
        this.application = application;
        CopyAndPaste.instance().addListener(this);
    }

    @Override
    public void copyChanged() {
        application.updateCopyItems();
    }

}
