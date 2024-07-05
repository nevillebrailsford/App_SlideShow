package applications.slideshow.change;

import application.change.AbstractChange;
import application.change.Failure;
import application.definition.ApplicationConfiguration;
import java.util.logging.Logger;
import javax.swing.tree.TreePath;
import applications.slideshow.model.Directory;
import applications.slideshow.storage.SlideShowManager;

public class AddSlideShowToChange extends AbstractChange {

    private static final String CLASS_NAME = AddSlideShowToChange.class.getName();
    private static final Logger LOGGER = ApplicationConfiguration.logger();

    private TreePath slideShow;
    private Directory newShow;

    public AddSlideShowToChange(TreePath slideShow, Directory newShow) {
        this.slideShow = slideShow;
        this.newShow = newShow;
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
        SlideShowManager.instance().addSlideShowTo(slideShow, newShow);
        LOGGER.exiting(CLASS_NAME, "redoHook");
    }

    @Override
    protected void undoHook() throws Failure {
        LOGGER.entering(CLASS_NAME, "undoHook");
        SlideShowManager.instance().removeSlideShowFrom(slideShow, newShow);
        LOGGER.exiting(CLASS_NAME, "undoHook");
    }

}
