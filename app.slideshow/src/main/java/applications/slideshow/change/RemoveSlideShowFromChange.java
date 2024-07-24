package applications.slideshow.change;

import application.change.AbstractChange;
import application.change.Failure;
import application.definition.ApplicationConfiguration;
import java.util.logging.Logger;
import javax.swing.tree.TreePath;
import applications.slideshow.model.Directory;
import applications.slideshow.storage.SlideShowManager;

public class RemoveSlideShowFromChange extends AbstractChange {

    private static final String CLASS_NAME = RemoveSlideShowFromChange.class.getName();
    private static final Logger LOGGER = ApplicationConfiguration.logger();

    private TreePath slideShow;
    private Directory oldShow;

    public RemoveSlideShowFromChange(TreePath slideShow, Directory oldShow) {
        this.slideShow = slideShow;
        this.oldShow = oldShow;
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
        SlideShowManager.instance().removeSlideShowFrom(slideShow, oldShow);
        LOGGER.exiting(CLASS_NAME, "redoHook");
    }

    @Override
    protected void undoHook() throws Failure {
        LOGGER.entering(CLASS_NAME, "undoHook");
        SlideShowManager.instance().addSlideShowTo(slideShow, oldShow);
        LOGGER.exiting(CLASS_NAME, "undoHook");
    }

}
