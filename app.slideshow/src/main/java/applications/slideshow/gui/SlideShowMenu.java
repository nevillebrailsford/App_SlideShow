package applications.slideshow.gui;

import application.definition.ApplicationConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import applications.slideshow.actions.ActionFactory;

public class SlideShowMenu extends JMenuBar {
    private static final long serialVersionUID = 1L;
    private static final String CLASS_NAME = SlideShowMenu.class.getName();
    private static Logger LOGGER = ApplicationConfiguration.logger();

    private ActionFactory actionFactory;

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

    private JMenu slideShowMenu = new JMenu("Slide Show");
    private JMenuItem startItem;
    private JMenuItem pauseItem;
    private JMenuItem resumeItem;
    private JMenuItem stopItem;

    private JMenu helpMenu = new JMenu("Help");
    private JMenuItem aboutItem;

    public SlideShowMenu(IApplication application) {
        LOGGER.entering(CLASS_NAME, "init");
        actionFactory = ActionFactory.instance(application);
        add(fileMenu);
        add(editMenu);
        add(slideShowMenu);
        add(helpMenu);
        newSlideShow = new JMenuItem(actionFactory.addSlideShowAction());
        preferences = new JMenuItem(actionFactory.preferencesAction());
        exit = new JMenuItem(actionFactory.exitApplicationAction());
        fileMenu.add(newSlideShow);
        fileMenu.addSeparator();
        fileMenu.add(preferences);
        fileMenu.add(exit);

        undoItem = new JMenuItem(actionFactory.undoAction());
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        actionFactory.undoAction().setEnabled(false);
        redoItem = new JMenuItem(actionFactory.redoAction());
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        actionFactory.redoAction().setEnabled(false);
        copyItem = new JMenuItem(actionFactory.copyAction());
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        actionFactory.copyAction().setEnabled(false);
        pasteItem = new JMenuItem(actionFactory.pasteAction());
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        actionFactory.pasteAction().setEnabled(false);
        deleteItem = new JMenuItem(actionFactory.deleteAction());
        actionFactory.deleteAction().setEnabled(false);
        deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.add(deleteItem);

        startItem = new JMenuItem(actionFactory.startSlideShowAction());
        pauseItem = new JMenuItem(actionFactory.pauseSlideShowAction());
        resumeItem = new JMenuItem(actionFactory.resumeSlideShowAction());
        stopItem = new JMenuItem(actionFactory.stopSlideShowAction());
        slideShowMenu.add(startItem);
        slideShowMenu.add(pauseItem);
        slideShowMenu.add(resumeItem);
        slideShowMenu.add(stopItem);
        slideShowStopped();

        aboutItem = new JMenuItem(actionFactory.helpAboutAction());
        helpMenu.add(aboutItem);

        LOGGER.exiting(CLASS_NAME, "init");
    }

    public void slideShowStarted() {
        actionFactory.startSlideShowAction().setEnabled(false);
        actionFactory.pauseSlideShowAction().setEnabled(true);
        actionFactory.resumeSlideShowAction().setEnabled(false);
        actionFactory.stopSlideShowAction().setEnabled(true);
    }

    public void slideShowPaused() {
        actionFactory.startSlideShowAction().setEnabled(false);
        actionFactory.pauseSlideShowAction().setEnabled(false);
        actionFactory.resumeSlideShowAction().setEnabled(true);
        actionFactory.stopSlideShowAction().setEnabled(true);
    }

    public void slideShowResumed() {
        slideShowStarted();
    }

    public void slideShowStopped() {
        actionFactory.startSlideShowAction().setEnabled(true);
        actionFactory.pauseSlideShowAction().setEnabled(false);
        actionFactory.resumeSlideShowAction().setEnabled(false);
        actionFactory.stopSlideShowAction().setEnabled(false);
    }

    public void undoable(boolean undoable) {
        actionFactory.undoAction().setEnabled(undoable);
    }

    public void redoabLe(boolean redoable) {
        actionFactory.redoAction().setEnabled(redoable);
    }

    public void copyable(boolean copyable) {
        actionFactory.copyAction().setEnabled(copyable);
    }

    public void pastable(boolean pastable) {
        actionFactory.pasteAction().setEnabled(pastable);
    }

    public void deletable(boolean deleteable) {
        actionFactory.deleteAction().setEnabled(deleteable);
    }

}
