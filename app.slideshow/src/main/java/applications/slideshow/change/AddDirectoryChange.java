package applications.slideshow.change;

import application.change.AbstractChange;
import application.change.Failure;
import application.definition.ApplicationConfiguration;
import java.io.File;
import java.util.logging.Logger;
import javax.swing.tree.TreePath;
import applications.slideshow.model.Directory;
import applications.slideshow.storage.SlideShowManager;

public class AddDirectoryChange extends AbstractChange {

    private static final String CLASS_NAME = AddDirectoryChange.class.getName();
    private static final Logger LOGGER = ApplicationConfiguration.logger();

    private TreePath slideShow;
    private Directory[] directories;

    public AddDirectoryChange(TreePath slideShow, File[] directories) {
        this.slideShow = slideShow;
        this.directories = new Directory[directories.length];
        for (int i = 0; i < directories.length; i++) {
            this.directories[i] = new Directory(directories[i]);
        }
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
        for (Directory dir : directories) {
            SlideShowManager.instance().addDirectory(slideShow, dir);
        }
        LOGGER.exiting(CLASS_NAME, "redoHook");
    }

    @Override
    protected void undoHook() throws Failure {
        LOGGER.entering(CLASS_NAME, "undoHook");
        for (Directory dir : directories) {
            SlideShowManager.instance().removeDirectory(slideShow, dir);
        }
        LOGGER.exiting(CLASS_NAME, "undoHook");
    }

}
