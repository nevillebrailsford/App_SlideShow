package applications.slideshow;

import application.change.ChangeManager;
import application.change.ChangeStateListener;

public class SlideShowStateListener implements ChangeStateListener {

    private SlideShowApplication application = null;

    public SlideShowStateListener(SlideShowApplication application) {
        this.application = application;
        ChangeManager.instance().addListener(this);
    }

    @Override
    public void stateChanged() {
        application.updateEditItems();
    }

}
