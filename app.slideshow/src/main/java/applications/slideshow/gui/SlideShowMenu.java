package applications.slideshow.gui;

import application.definition.ApplicationConfiguration;
import application.menu.AbstractMenuBar;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import applications.slideshow.ISlideShowApplication;
import applications.slideshow.actions.SlideShowActionFactory;

public class SlideShowMenu extends AbstractMenuBar {
    private static final long serialVersionUID = 1L;
    private static final String CLASS_NAME = SlideShowMenu.class.getName();
    private static Logger LOGGER = ApplicationConfiguration.logger();

    private JMenuItem newSlideShow;

    private JMenuItem copyItem;
    private JMenuItem pasteItem;
    private JMenuItem deleteItem;

    private JMenu slideShowMenu;
    private JMenuItem startItem;
    private JMenuItem pauseItem;
    private JMenuItem resumeItem;
    private JMenuItem stopItem;

    public SlideShowMenu(ISlideShowApplication application) {
        super(SlideShowActionFactory.instance(application));
        LOGGER.entering(CLASS_NAME, "init");
        LOGGER.exiting(CLASS_NAME, "init");
    }

    @Override
    public void addBeforePreferences(JMenu fileMenu) {
        newSlideShow = new JMenuItem(SlideShowActionFactory.instance().addSlideShowAction());
        fileMenu.add(newSlideShow);
        fileMenu.addSeparator();
    }

    @Override
    public void addToEditMenu(JMenu editMenu) {
        copyItem = new JMenuItem(SlideShowActionFactory.instance().copyAction());
        pasteItem = new JMenuItem(SlideShowActionFactory.instance().pasteAction());
        deleteItem = new JMenuItem(SlideShowActionFactory.instance().deleteAction());
        editMenu.addSeparator();
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.add(deleteItem);
    }

    @Override
    public void addAdditionalMenus(JMenuBar menuBar) {
        startItem = new JMenuItem(SlideShowActionFactory.instance().startSlideShowAction());
        pauseItem = new JMenuItem(SlideShowActionFactory.instance().pauseSlideShowAction());
        resumeItem = new JMenuItem(SlideShowActionFactory.instance().resumeSlideShowAction());
        stopItem = new JMenuItem(SlideShowActionFactory.instance().stopSlideShowAction());
        slideShowMenu = new JMenu("Slide Show");
        slideShowMenu.add(startItem);
        slideShowMenu.add(pauseItem);
        slideShowMenu.add(resumeItem);
        slideShowMenu.add(stopItem);
        menuBar.add(slideShowMenu);
        slideShowStopped();
    }

    public void slideShowStarted() {
        SlideShowActionFactory.instance().startSlideShowAction().setEnabled(false);
        SlideShowActionFactory.instance().pauseSlideShowAction().setEnabled(true);
        SlideShowActionFactory.instance().resumeSlideShowAction().setEnabled(false);
        SlideShowActionFactory.instance().stopSlideShowAction().setEnabled(true);
    }

    public void slideShowPaused() {
        SlideShowActionFactory.instance().startSlideShowAction().setEnabled(false);
        SlideShowActionFactory.instance().pauseSlideShowAction().setEnabled(false);
        SlideShowActionFactory.instance().resumeSlideShowAction().setEnabled(true);
        SlideShowActionFactory.instance().stopSlideShowAction().setEnabled(true);
    }

    public void slideShowResumed() {
        slideShowStarted();
    }

    public void slideShowStopped() {
        SlideShowActionFactory.instance().startSlideShowAction().setEnabled(false);
        SlideShowActionFactory.instance().pauseSlideShowAction().setEnabled(false);
        SlideShowActionFactory.instance().resumeSlideShowAction().setEnabled(false);
        SlideShowActionFactory.instance().stopSlideShowAction().setEnabled(false);
    }

}
