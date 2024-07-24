package applications.slideshow.change;

import application.change.AbstractChange;
import application.change.Failure;
import application.definition.ApplicationConfiguration;
import java.util.logging.Logger;
import javax.swing.tree.TreePath;
import applications.slideshow.model.Directory;
import applications.slideshow.storage.SlideShowManager;

public class RemoveDirectoryChange extends AbstractChange {

    private static final String CLASS_NAME = RemoveDirectoryChange.class.getName();
    private static final Logger LOGGER = ApplicationConfiguration.logger();

    private TreePath slideShow;
    private Directory directory;

    public RemoveDirectoryChange(TreePath slideShow, Directory directory) {
        this.slideShow = slideShow;
        this.directory = directory;
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
        SlideShowManager.instance().removeDirectory(slideShow, directory);
        LOGGER.exiting(CLASS_NAME, "redoHook");
    }

    @Override
    protected void undoHook() throws Failure {
        LOGGER.entering(CLASS_NAME, "undoHook");
        SlideShowManager.instance().addDirectory(slideShow, directory);
        LOGGER.exiting(CLASS_NAME, "undoHook");
    }

}
