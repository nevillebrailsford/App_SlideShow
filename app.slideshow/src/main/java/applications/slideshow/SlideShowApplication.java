package applications.slideshow;

import application.base.app.ApplicationBaseForGUI;
import application.base.app.Parameters;
import application.base.app.gui.BottomColoredPanel;
import application.base.app.gui.ColorProvider;
import application.base.app.gui.GUIConstants;
import application.change.Change;
import application.change.ChangeManager;
import application.definition.ApplicationConfiguration;
import application.definition.ApplicationDefinition;
import application.inifile.IniFile;
import application.preferences.PreferencesDialog;
import application.replicate.CopyAndPaste;
import application.storage.StoreDetails;
import application.thread.ThreadServices;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.util.Optional;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import applications.slideshow.actions.SlideShowActionFactory;
import applications.slideshow.change.AddDirectoryChange;
import applications.slideshow.change.AddSlideShowChange;
import applications.slideshow.change.AddSlideShowToChange;
import applications.slideshow.change.RemoveDirectoryChange;
import applications.slideshow.change.RemoveSlideShowFromChange;
import applications.slideshow.dialog.SlideShowPreferences;
import applications.slideshow.gui.SlideShowMenu;
import applications.slideshow.gui.SlideShowTree;
import applications.slideshow.model.Directory;
import applications.slideshow.show.SlideShowDisplay;
import applications.slideshow.storage.SlideShowLoad;
import applications.slideshow.storage.SlideShowManager;

public class SlideShowApplication extends ApplicationBaseForGUI
        implements ISlideShowApplication, TreeSelectionListener {
    private static final long serialVersionUID = 1L;
    private static final String CLASS_NAME = SlideShowApplication.class.getName();

    private static Logger LOGGER = null;

    private JFrame parent;
    private JButton exit;

    private int currentX = 0;
    private int currentY = 0;

    private JTree tree = null;
    private SlideShowMenu menu = null;

    private SlideShowDisplay slideShowDisplay = null;

    final String undoAction = "undoAction";
    final String redoAction = "redoAction";
    final String copyAction = "copyAction";
    final String pasteAction = "pasteAction";
    final String deleteAction = "deleteAction";
    final String startAction = "startAction";

    public static String getHomeDirectory() {
        String home = IniFile.value(Constants.HOME_DIRECTORY);
        if (home == null || home.isEmpty()) {
            home = System.getProperty("user.home");
        }
        return home;
    }

    public SlideShowApplication() {
    }

    @Override
    public StoreDetails configureStoreDetails() {
        dataLoader = new SlideShowLoad();
        StoreDetails storeDetails = new StoreDetails(dataLoader, Constants.MODEL, Constants.SLIDE_SHOW_FILE);
        return storeDetails;
    }

    @Override
    public ApplicationDefinition createApplicationDefinition(Parameters parameters) {
        ApplicationDefinition definition = new ApplicationDefinition(parameters.getNamed().get("name")) {

            @Override
            public boolean hasModelFile() {
                return true;
            }

            @Override
            public Optional<Color> bottomColor() {
                String bottom = IniFile.value(GUIConstants.BOTTOM_COLOR);
                if (bottom.isEmpty() || bottom.equals("default")) {
                    bottom = "lightsteelblue";
                    IniFile.store(GUIConstants.BOTTOM_COLOR, bottom);
                }
                Color bottomColor = ColorProvider.get(bottom);
                return Optional.ofNullable(bottomColor);
            }

            @Override
            public Optional<Color> topColor() {
                String top = IniFile.value(GUIConstants.TOP_COLOR);
                if (top.isEmpty() || top.equals("default")) {
                    top = "lightcyan";
                    IniFile.store(GUIConstants.TOP_COLOR, top);
                }
                Color topColor = ColorProvider.get(top);
                return Optional.ofNullable(topColor);
            }
        };
        return definition;
    }

    @Override
    public void start(JFrame parent) {
        LOGGER = ApplicationConfiguration.logger();
        LOGGER.entering(CLASS_NAME, "start");
        this.parent = parent;
        System.out.println(
                "Application " + ApplicationConfiguration.applicationDefinition().applicationName() + " is starting");
        parent.setLayout(new BorderLayout());
        menu = new SlideShowMenu(this);
        setActionKeyBindings();
        parent.setJMenuBar(menu);
        tree = createJTree();
        JScrollPane treeView = new JScrollPane();
        treeView.setPreferredSize(new Dimension(800, 500));
        treeView.setViewportView(tree);
        add(treeView, BorderLayout.CENTER);
        JPanel buttonPanel = new BottomColoredPanel();
        buttonPanel.setLayout(new FlowLayout());
        exit = new JButton(SlideShowActionFactory.instance().exitApplicationAction());
        buttonPanel.add(exit);
        add(buttonPanel, BorderLayout.PAGE_END);
        pack();
        new SlideShowCopyListener(this);
        LOGGER.exiting(CLASS_NAME, "start");
    }

    @Override
    public void terminate() {
        LOGGER.entering(CLASS_NAME, "terminate");
        System.out.println(
                "Application " + ApplicationConfiguration.applicationDefinition().applicationName() + " is stopping");
        LOGGER.exiting(CLASS_NAME, "terminate");
    }

    /**
     * Main entry point for program.
     * 
     * @param args - any number of arguments passed in from command line.
     */
    public static void main(String[] args) {
        System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
        launch(args);
    }

    @Override
    public void preferencesAction() {
        LOGGER.entering(CLASS_NAME, "preferencesAction");
        PreferencesDialog dialog = new SlideShowPreferences(parent);
        dialog.setVisible(true);
        dialog.dispose();
        LOGGER.exiting(CLASS_NAME, "preferencesAction");
    }

    @Override
    public void addSlideShowAction() {
        LOGGER.entering(CLASS_NAME, "addSlideShowAction");
        String title = JOptionPane.showInputDialog(this, "Please provide a title");
        if (title != null && !title.isEmpty()) {
            try {
                Directory newShow = new Directory(title);
                AddSlideShowChange addSlideShowChange = new AddSlideShowChange(newShow);
                submitChange(addSlideShowChange);
            } catch (Throwable e) {
                System.out.println(e);
            }
        }
        LOGGER.exiting(CLASS_NAME, "addSlideShowAction");
    }

    @Override
    public void addDirectoryAction() {
        LOGGER.entering(CLASS_NAME, "addDirectoryAction");
        TreePath selPath = tree.getPathForLocation(currentX, currentY);
        if (selPath == null) {
            LOGGER.exiting(CLASS_NAME, "addDirectoryAction", selPath);
            return;
        }
        TreeNode node = (TreeNode) selPath.getLastPathComponent();
        Directory slideShow = (Directory) node;
        LOGGER.fine("User chose slide show " + slideShow);
        if (slideShow.isSlideShow()) {
            JFileChooser jfc = createFileChooserDialog();
            int returnValue = jfc.showOpenDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                LOGGER.fine("User chose approve option");
                File[] files = jfc.getSelectedFiles();
                AddDirectoryChange addDirectoryChange = new AddDirectoryChange(selPath, files);
                submitChange(addDirectoryChange);
            }
        } else {
            JOptionPane.showMessageDialog(this, slideShow + " is not a slide show");
        }
        LOGGER.exiting(CLASS_NAME, "addDirectoryAction");
    }

    @Override
    public void addSlideShowToAction() {
        LOGGER.entering(CLASS_NAME, "addSlideShowToAction");
        TreePath selPath = tree.getSelectionPath();
        if (selPath == null) {
            LOGGER.exiting(CLASS_NAME, "addSlideShowToAction", selPath);
            return;
        }
        TreeNode node = (TreeNode) selPath.getLastPathComponent();
        Directory slideShow = (Directory) node;
        LOGGER.fine("User chose slide show " + slideShow);
        if (slideShow.isSlideShow()) {
            String title = JOptionPane.showInputDialog("Please enter slide show title:  ");
            LOGGER.fine("User entered " + title);
            Directory newSlideShow = new Directory(title);
            AddSlideShowToChange addSlideShowToChange = new AddSlideShowToChange(selPath, newSlideShow);
            submitChange(addSlideShowToChange);
        } else {
            JOptionPane.showMessageDialog(this, slideShow + " is not a slide show");
        }

        LOGGER.exiting(CLASS_NAME, "addSlideShowToAction");
    }

    @Override
    public void startSlideShowAction() {
        LOGGER.entering(CLASS_NAME, "startSlideShowAction");
        if (slideShowDisplay != null) {
            JOptionPane.showMessageDialog(this, "Slide Show is already running", "Start Slide Show",
                    JOptionPane.INFORMATION_MESSAGE);
            LOGGER.exiting(CLASS_NAME, "startSlideShowAction");
            return;
        }
        TreePath selPath = null;
        File[] files = null;
        if (tree.getSelectionCount() == 0) {
            if (currentX > 0 && currentY > 0) {
                selPath = tree.getPathForLocation(currentX, currentY);
            }
        } else {
            selPath = tree.getSelectionPath();
        }
        if (selPath == null) {
            JOptionPane.showMessageDialog(this, "Nothing selected");
            LOGGER.exiting(CLASS_NAME, "startSlideShowAction");
            return;
        }
        files = SlideShowManager.instance().files(selPath);
        if (files.length > 0) {
            menu.slideShowStarted();
            String displaySeconds = getSlideDuration();
            String screenWidth = getScreenWidth();
            String screenHeight = getScreenHeight();
            slideShowDisplay = new SlideShowDisplay(files, displaySeconds, screenWidth, screenHeight, this);
        } else {
            JOptionPane.showMessageDialog(this, "Nothing to display");
        }
        LOGGER.exiting(CLASS_NAME, "startSlideShowAction");
    }

    @Override
    public void pauseSlideShowAction() {
        LOGGER.entering(CLASS_NAME, "pauseSlideShowAction");
        if (slideShowDisplay != null) {
            menu.slideShowPaused();
            slideShowDisplay.pause();
        }
        LOGGER.exiting(CLASS_NAME, "pauseSlideShowAction");
    }

    @Override
    public void resumeSlideShowAction() {
        LOGGER.entering(CLASS_NAME, "resumeSlideShowAction");
        if (slideShowDisplay != null) {
            menu.slideShowResumed();
            slideShowDisplay.resume();
        }
        LOGGER.exiting(CLASS_NAME, "resumeSlideShowAction");
    }

    @Override
    public void stopSlideShowAction() {
        LOGGER.entering(CLASS_NAME, "stopSlideShowAction");
        if (slideShowDisplay != null) {
            menu.slideShowStopped();
            slideShowDisplay.stopShow();
            slideShowDisplay = null;
        }
        LOGGER.exiting(CLASS_NAME, "stopSlideShowAction");
    }

    @Override
    public void exitApplicationAction() {
        LOGGER.entering(CLASS_NAME, "exitApplicationAction");
        try {
            if (slideShowDisplay != null) {
                slideShowDisplay.stopShow();
            }
            super.exitApplicationAction();
        } catch (Exception e) {
        }
        LOGGER.exiting(CLASS_NAME, "exitApplicationAction");
    }

    @Override
    public void copyAction() {
        LOGGER.entering(CLASS_NAME, "copyAction");
        if (!tree.isSelectionEmpty()) {
            CopyAndPaste.instance().copy(tree.getSelectionPath());
        }
        LOGGER.exiting(CLASS_NAME, "copyAction");
    }

    @Override
    public void pasteAction() {
        LOGGER.entering(CLASS_NAME, "pasteAction");
        TreePath path = (TreePath) CopyAndPaste.instance().paste();
        Directory pasteDirectory = (Directory) path.getLastPathComponent();
        TreePath pathToAddTo = tree.getSelectionPath();
        Directory newDirectory = pasteDirectory.copy();
        if (newDirectory.isSlideShow()) {
            AddSlideShowToChange addSlideShowToChange = new AddSlideShowToChange(pathToAddTo, newDirectory);
            submitChange(addSlideShowToChange);
        } else {
            AddDirectoryChange addDirectoryChange = new AddDirectoryChange(pathToAddTo,
                    new File[] { newDirectory.path() });
            submitChange(addDirectoryChange);
        }
        LOGGER.exiting(CLASS_NAME, "pasteAction");
    }

    @Override
    public void deleteAction() {
        LOGGER.entering(CLASS_NAME, "deleteAction");
        TreePath path = tree.getSelectionPath();
        Directory deleteDirectory = (Directory) path.getLastPathComponent();
        TreePath pathToDeleteFrom = path.getParentPath();
        if (deleteDirectory.isSlideShow()) {
            RemoveSlideShowFromChange removeSlideShowFromChange = new RemoveSlideShowFromChange(pathToDeleteFrom,
                    deleteDirectory);
            submitChange(removeSlideShowFromChange);
        } else {
            RemoveDirectoryChange removeDirectoryChange = new RemoveDirectoryChange(pathToDeleteFrom, deleteDirectory);
            submitChange(removeDirectoryChange);
        }
        LOGGER.exiting(CLASS_NAME, "deleteAction");
    }

    private void submitChange(Change change) {
        LOGGER.entering(CLASS_NAME, "submitChange");
        ThreadServices.instance().executor().submit(() -> {
            ChangeManager.instance().execute(change);
        });
        LOGGER.exiting(CLASS_NAME, "submitChange");
    }

    @Override
    public void showEnding() {
        menu.slideShowStopped();
        slideShowDisplay = null;
    }

    /**
     * Called when CopyAndPsateListener determines that an item is available to be
     * copied.
     */
    public void updateCopyItems() {
        LOGGER.entering(CLASS_NAME, "updateCopyItems");
        SlideShowActionFactory.instance().pastable(CopyAndPaste.instance().paste() != null);
        LOGGER.exiting(CLASS_NAME, "updateCopyItems");
    }

    private JFileChooser createFileChooserDialog() {
        String home = getHomeDirectory();
        JFileChooser fc = new JFileChooser(home);
        fc.setDialogTitle("Select which directories you'd like to view.");
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setMultiSelectionEnabled(true);
        return fc;
    }

    private JTree createJTree() {
        SlideShowTree tree = new SlideShowTree(SlideShowManager.instance());
        tree.getSelectionModel().addTreeSelectionListener(this);
        tree.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                currentX = e.getX();
                currentY = e.getY();
            }
        });
        setKeyBindings(tree);
        tree.clearSelection();
        return tree;
    }

    private String getSlideDuration() {
        String result = IniFile.value(Constants.DISPLAY_SECONDS);
        if (result == null || result.isEmpty()) {
            result = Constants.DEFAULT_SCREENSECONDS;
        }
        return result;
    }

    private String getScreenWidth() {
        String result = IniFile.value(Constants.SCREEN_WIDTH);
        if (result == null || result.isEmpty()) {
            result = Constants.DEFAULT_SCREEN_WIDTH;
        }
        return result;
    }

    private String getScreenHeight() {
        String result = IniFile.value(Constants.SCREEN_HEIGHT);
        if (result == null || result.isEmpty()) {
            result = Constants.DEFAULT_SCREEN_HEIGHT;
        }
        return result;
    }

    private void setKeyBindings(JTree tree) {
        tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK), undoAction);
        tree.getActionMap().put(undoAction, SlideShowActionFactory.instance().undoAction());
        tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK), redoAction);
        tree.getActionMap().put(redoAction, SlideShowActionFactory.instance().redoAction());
        tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK), copyAction);
        tree.getActionMap().put(copyAction, SlideShowActionFactory.instance().copyAction());
        tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK), pasteAction);
        tree.getActionMap().put(pasteAction, SlideShowActionFactory.instance().pasteAction());
        tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK), deleteAction);
        tree.getActionMap().put(deleteAction, SlideShowActionFactory.instance().deleteAction());
        tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK), startAction);
        tree.getActionMap().put(startAction, SlideShowActionFactory.instance().startSlideShowAction());
    }

    private void setActionKeyBindings() {
        KeyStroke undo = KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK);
        SlideShowActionFactory.instance().undoAction().putValue(Action.ACCELERATOR_KEY, undo);
        KeyStroke redo = KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK);
        SlideShowActionFactory.instance().redoAction().putValue(Action.ACCELERATOR_KEY, redo);
        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK);
        SlideShowActionFactory.instance().copyAction().putValue(Action.ACCELERATOR_KEY, copy);
        KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK);
        SlideShowActionFactory.instance().pasteAction().putValue(Action.ACCELERATOR_KEY, paste);
        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK);
        SlideShowActionFactory.instance().deleteAction().putValue(Action.ACCELERATOR_KEY, delete);
        KeyStroke start = KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK);
        SlideShowActionFactory.instance().startSlideShowAction().putValue(Action.ACCELERATOR_KEY, start);
    }

    // Tree selection listener
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        SlideShowActionFactory.instance().copyable(tree.getSelectionPath() != null);
        SlideShowActionFactory.instance().pastable(CopyAndPaste.instance().paste() != null);
        SlideShowActionFactory.instance().deletable(tree.getSelectionPath() != null);
        SlideShowActionFactory.instance().startable(tree.getSelectionPath() != null);
    }

}
