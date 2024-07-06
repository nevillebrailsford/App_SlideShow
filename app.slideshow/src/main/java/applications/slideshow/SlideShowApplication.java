package applications.slideshow;

import application.base.app.ApplicationBaseForGUI;
import application.base.app.Parameters;
import application.base.app.gui.BottomColoredPanel;
import application.base.app.gui.ColorProvider;
import application.base.app.gui.GUIConstants;
import application.base.app.gui.PreferencesDialog;
import application.change.ChangeManager;
import application.definition.ApplicationConfiguration;
import application.definition.ApplicationDefinition;
import application.inifile.IniFile;
import application.storage.LoadData;
import application.storage.StoreDetails;
import application.thread.ThreadServices;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.util.Optional;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import applications.slideshow.actions.AddDirectoryAction;
import applications.slideshow.actions.AddSlideShowToAction;
import applications.slideshow.actions.ExitApplicationAction;
import applications.slideshow.actions.StartSlideShowAction;
import applications.slideshow.change.AddDirectoryChange;
import applications.slideshow.change.AddSlideShowChange;
import applications.slideshow.change.AddSlideShowToChange;
import applications.slideshow.dialog.SlideShowPreferences;
import applications.slideshow.gui.IApplication;
import applications.slideshow.gui.SlideShowMenu;
import applications.slideshow.model.Directory;
import applications.slideshow.storage.SlideShowLoad;
import applications.slideshow.storage.SlideShowManager;

public class SlideShowApplication extends ApplicationBaseForGUI implements IApplication, TreeModelListener {
    private static final long serialVersionUID = 1L;
    private static final String CLASS_NAME = SlideShowApplication.class.getName();

    private static Logger LOGGER = null;

    private static final String HOME = "C:/Users/nevil/OneDrive/Pictures/";
    private JFrame parent;
    private JButton exit;

    private int currentX = 0;
    private int currentY = 0;

    private JTree tree = null;

    private SlideShowDisplay slideShowDisplay = null;

    public SlideShowApplication() {
    }

    @Override
    public void configureStoreDetails() {
        dataLoader = new SlideShowLoad();
        storeDetails = new StoreDetails(dataLoader, Constants.MODEL, Constants.SLIDE_SHOW_FILE);
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
        SlideShowMenu sm = new SlideShowMenu(this);
        parent.setJMenuBar(sm);
        tree = new JTree(SlideShowManager.instance());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane treeView = new JScrollPane();
        treeView.setPreferredSize(new Dimension(800, 500));
        treeView.setViewportView(tree);
        add(treeView, BorderLayout.CENTER);
        JPanel buttonPanel = new BottomColoredPanel();
        buttonPanel.setLayout(new FlowLayout());
        exit = new JButton(new ExitApplicationAction(this));
        buttonPanel.add(exit);
        add(buttonPanel, BorderLayout.PAGE_END);
        pack();
        JPopupMenu menu = new JPopupMenu();
        menu.add(new JMenuItem(new AddDirectoryAction(this)));
        menu.add(new JMenuItem(new AddSlideShowToAction(this)));
        menu.add(new JMenuItem(new StartSlideShowAction(this)));
        tree.setComponentPopupMenu(menu);
        tree.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                currentX = e.getX();
                currentY = e.getY();
            }
        });
        SlideShowManager.instance().addTreeModelListener(this);
        expandAllNodes(tree, 0, tree.getRowCount());
        tree.clearSelection();
        ImageIcon dirIcon = createImageIcon("directory-64.png");
        ImageIcon showIcon = createImageIcon("slide-show-64.png");
        tree.setCellRenderer(new TreeCellRenderer(showIcon, dirIcon));
        LOGGER.exiting(CLASS_NAME, "start");
    }

    @Override
    public void terminate() {
        LOGGER.entering(CLASS_NAME, "terminate");
        System.out.println(
                "Application " + ApplicationConfiguration.applicationDefinition().applicationName() + " is stopping");
        LOGGER.exiting(CLASS_NAME, "terminate");
    }

    @Override
    public boolean loadExistingModel(LoadData arg0, String arg1, String arg2) {
        return super.loadExistingModel(arg0, arg1, arg2);
    }

    public static void main(String[] args) {
        System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
        launch(args);
    }

    @Override
    public void preferencesAction() {
        LOGGER.entering(CLASS_NAME, "preferencesAction");
        PreferencesDialog dialog = new SlideShowPreferences(parent);
        dialog.dispose();
        dialog = new SlideShowPreferences(parent);
        dialog.setVisible(true);
        dialog.dispose();
        LOGGER.exiting(CLASS_NAME, "preferencesAction");
    }

    @Override
    public void addSlideShowAction() {
        LOGGER.entering(HOME, "addSlideShowAction");
        String title = JOptionPane.showInputDialog(this, "Please provide a title");
        if (title != null && !title.isEmpty()) {
            try {
                Directory newShow = new Directory(title);
                AddSlideShowChange addSlideShowChange = new AddSlideShowChange(newShow);
                ThreadServices.instance().executor().submit(() -> {
                    ChangeManager.instance().execute(addSlideShowChange);
                });
            } catch (Throwable e) {
                System.out.println(e);
            }
        }
        LOGGER.exiting(HOME, "addSlideShowAction");
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
                ThreadServices.instance().executor().submit(() -> {
                    ChangeManager.instance().execute(addDirectoryChange);
                });
            }
        } else {
            JOptionPane.showMessageDialog(this, slideShow + " is not a slide show");
        }
        LOGGER.exiting(CLASS_NAME, "addDirectoryAction");
    }

    @Override
    public void addSlideShowToAction() {
        LOGGER.entering(CLASS_NAME, "addSlideShowToAction");
        TreePath selPath = tree.getPathForLocation(currentX, currentY);
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
            ThreadServices.instance().executor().submit(() -> {
                ChangeManager.instance().execute(addSlideShowToChange);
            });
        } else {
            JOptionPane.showMessageDialog(this, slideShow + " is not a slide show");
        }

        LOGGER.exiting(CLASS_NAME, "addSlideShowToAction");
    }

    @Override
    public void startSlideShowAction() {
        LOGGER.entering(CLASS_NAME, "startSlideShowAction");
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
            slideShowDisplay = new SlideShowDisplay(files);
        } else {
            JOptionPane.showMessageDialog(this, "Nothing to display");
        }
        LOGGER.exiting(CLASS_NAME, "startSlideShowAction");
    }

    @Override
    public void pauseSlideShowAction() {
        LOGGER.entering(CLASS_NAME, "pauseSlideShowAction");
        slideShowDisplay.pause();
        LOGGER.exiting(CLASS_NAME, "pauseSlideShowAction");
    }

    @Override
    public void resumeSlideShowAction() {
        LOGGER.entering(CLASS_NAME, "resumeSlideShowAction");
        slideShowDisplay.resume();
        LOGGER.exiting(CLASS_NAME, "resumeSlideShowAction");
    }

    @Override
    public void stopSlideShowAction() {
        LOGGER.entering(CLASS_NAME, "stopSlideShowAction");
        slideShowDisplay.stopShow();
        slideShowDisplay = null;
        LOGGER.exiting(CLASS_NAME, "stopSlideShowAction");
    }

    @Override
    public void exitApplicationAction() {
        LOGGER.entering(CLASS_NAME, "exitApplicationAction");
        try {
            if (slideShowDisplay != null) {
                slideShowDisplay.stopShow();
            }
            shutdown();
        } catch (Exception e) {
        }
        LOGGER.exiting(CLASS_NAME, "exitApplicationAction");
    }

    private static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = SlideShowApplication.class.getResource(path);
        if (imgURL != null) {
            ImageIcon result = new ImageIcon(imgURL);
            Image image = result.getImage();
            Image newImage = image.getScaledInstance(24, 24, java.awt.Image.SCALE_SMOOTH);
            result = new ImageIcon(newImage);
            return result;
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    private JFileChooser createFileChooserDialog() {
        JFileChooser fc = new JFileChooser(HOME);
        fc.setDialogTitle("Select which directories you'd like to view.");
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setMultiSelectionEnabled(true);
        return fc;
    }

    @Override
    public void treeNodesChanged(TreeModelEvent e) {
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
    }

    @Override
    public void treeStructureChanged(TreeModelEvent e) {
        expandAllNodes(tree, 0, tree.getRowCount());
    }

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }

        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

}
