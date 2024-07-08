package applications.slideshow.gui;

import application.definition.ApplicationConfiguration;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import applications.slideshow.actions.AddSlideShowAction;
import applications.slideshow.actions.ExitApplicationAction;
import applications.slideshow.actions.PauseSlideShowAction;
import applications.slideshow.actions.PreferencesAction;
import applications.slideshow.actions.RedoAction;
import applications.slideshow.actions.ResumeSlideShowAction;
import applications.slideshow.actions.StartSlideShowAction;
import applications.slideshow.actions.StopSlideShowAction;
import applications.slideshow.actions.UndoAction;

public class SlideShowMenu extends JMenuBar {
    private static final long serialVersionUID = 1L;
    private static final String CLASS_NAME = SlideShowMenu.class.getName();
    private static Logger LOGGER = ApplicationConfiguration.logger();

    @SuppressWarnings("unused")
    private IApplication application;

    private JMenu fileMenu = new JMenu("File");
    private JMenuItem preferences;
    private JMenuItem newSlideShow;
    private JMenuItem exit;

    private JMenu editMenu = new JMenu("Edit");
    private JMenuItem undoItem;
    private JMenuItem redoItem;
    private JMenuItem copyItem;
    private JMenuItem pasteItem;
    private JMenuItem deleteItem;

    private JMenu viewMenu = new JMenu("View");
    private JMenuItem collapseItem;
    private JMenuItem expandItem;

    private JMenu slideShowMenu = new JMenu("Slide Show");
    private JMenuItem startItem;
    private JMenuItem pauseItem;
    private JMenuItem resumeItem;
    private JMenuItem stopItem;

    public SlideShowMenu(IApplication application) {
        LOGGER.entering(CLASS_NAME, "init");
        this.application = application;
        add(fileMenu);
        add(editMenu);
        add(viewMenu);
        add(slideShowMenu);
        newSlideShow = new JMenuItem(new AddSlideShowAction(application));
        preferences = new JMenuItem(new PreferencesAction(application));
        exit = new JMenuItem(new ExitApplicationAction(application));
        fileMenu.add(newSlideShow);
        fileMenu.addSeparator();
        fileMenu.add(preferences);
        fileMenu.add(exit);

        undoItem = new JMenuItem(new UndoAction(application));
        undoItem.setEnabled(false);
        redoItem = new JMenuItem(new RedoAction(application));
        redoItem.setEnabled(false);
        copyItem = new JMenuItem("Copy");
        pasteItem = new JMenuItem("Paste");
        deleteItem = new JMenuItem("Delete");
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.add(deleteItem);

        collapseItem = new JMenuItem("Collpase All Slide Shows");
        expandItem = new JMenuItem("Expand All Slide Shows");
        viewMenu.add(collapseItem);
        viewMenu.add(expandItem);

        startItem = new JMenuItem(new StartSlideShowAction(application));
        pauseItem = new JMenuItem(new PauseSlideShowAction(application));
        resumeItem = new JMenuItem(new ResumeSlideShowAction(application));
        stopItem = new JMenuItem(new StopSlideShowAction(application));
        slideShowMenu.add(startItem);
        slideShowMenu.add(pauseItem);
        slideShowMenu.add(resumeItem);
        slideShowMenu.add(stopItem);
        slideShowStopped();

        LOGGER.exiting(CLASS_NAME, "init");
    }

    public void slideShowStarted() {
        startItem.setEnabled(false);
        pauseItem.setEnabled(true);
        resumeItem.setEnabled(false);
        stopItem.setEnabled(true);
    }

    public void slideShowPaused() {
        startItem.setEnabled(false);
        pauseItem.setEnabled(false);
        resumeItem.setEnabled(true);
        stopItem.setEnabled(true);
    }

    public void slideShowResumed() {
        slideShowStarted();
    }

    public void slideShowStopped() {
        startItem.setEnabled(true);
        pauseItem.setEnabled(false);
        resumeItem.setEnabled(false);
        stopItem.setEnabled(false);
    }

    public void undoable(boolean undoable) {
        undoItem.setEnabled(undoable);
    }

    public void redoabLe(boolean redoable) {
        redoItem.setEnabled(redoable);
    }

}
