package applications.slideshow.change;

import application.change.AbstractChange;
import application.change.Failure;
import application.definition.ApplicationConfiguration;
import java.util.logging.Logger;
import applications.slideshow.model.Directory;
import applications.slideshow.storage.SlideShowManager;

public class AddSlideShowChange extends AbstractChange {

    private static final String CLASS_NAME = AddSlideShowChange.class.getName();
    private static final Logger LOGGER = ApplicationConfiguration.logger();

    private Directory slideShow;

    public AddSlideShowChange(Directory slideShow) {
        this.slideShow = slideShow;
    }

    @Override
    protected void doHook() throws Failure {
        LOGGER.entering(CLASS_NAME, "doHook");
        redoHook();
        LOGGER.exiting(CLASS_NAME, "doHook");
    }

    @Override
    protected void redoHook() throws Failure {
        LOGGER.entering(CLASS_NAME, "redoHook");
        SlideShowManager.instance().addSlideShow(slideShow);
        LOGGER.exiting(CLASS_NAME, "redoHook");
    }

    @Override
    protected void undoHook() throws Failure {
        LOGGER.entering(CLASS_NAME, "undoHook");
        SlideShowManager.instance().removeSlideShow(slideShow);
        LOGGER.exiting(CLASS_NAME, "undoHook");
    }

}
