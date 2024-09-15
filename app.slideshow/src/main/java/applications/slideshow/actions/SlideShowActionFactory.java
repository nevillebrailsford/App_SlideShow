package applications.slideshow.actions;

import application.action.BaseActionFactory;
import javax.swing.JOptionPane;
import applications.slideshow.ISlideShowApplication;

public class SlideShowActionFactory extends BaseActionFactory {

    private CopyAction copyAction = null;
    private PasteAction pasteAction = null;
    private DeleteAction deleteAction = null;
    private AddDirectoryAction addDirectoryAction = null;
    private AddSlideShowAction addSlideShowAction = null;
    private AddSlideShowToAction addSlideShowToAction = null;
    private PauseSlideShowAction pauseSlideShowAction = null;
    private ResumeSlideShowAction resumeSlideShowAction = null;
    private StartSlideShowAction startSlideShowAction = null;
    private StopSlideShowAction stopSlideShowAction = null;

    private static SlideShowActionFactory instance = null;

    public synchronized static SlideShowActionFactory instance(ISlideShowApplication... application) {
        if (instance == null) {
            if (application.length == 0) {
                JOptionPane.showMessageDialog(null, "Application was not specified on first call to instance.",
                        "ActionFactory error.", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            instance = new SlideShowActionFactory();
            instance.application = application[0];
        }
        return instance;
    }

    public SlideShowActionFactory() {
        super();
    }

    /**
     * Obtain the copy action.
     * <p>
     * The action is created in the disabled state
     * </p>
     * 
     * @return - a copy action.
     */
    public CopyAction copyAction() {
        if (copyAction == null) {
            copyAction = new CopyAction((ISlideShowApplication) application);
            copyAction.setEnabled(false);
        }
        return copyAction;
    }

    /**
     * Obtain the delete action.
     * <p>
     * The action is created in the disabled state
     * </p>
     * 
     * @return - a delete action.
     */
    public DeleteAction deleteAction() {
        if (deleteAction == null) {
            deleteAction = new DeleteAction((ISlideShowApplication) application);
            deleteAction.setEnabled(false);
        }
        return deleteAction;
    }

    /**
     * Obtain the paste action.
     * <p>
     * The action is created in the disabled state
     * </p>
     * 
     * @return - a paste action.
     */
    public PasteAction pasteAction() {
        if (pasteAction == null) {
            pasteAction = new PasteAction((ISlideShowApplication) application);
            pasteAction.setEnabled(false);
        }
        return pasteAction;
    }

    public AddDirectoryAction addDirectoryAction() {
        if (addDirectoryAction == null) {
            addDirectoryAction = new AddDirectoryAction((ISlideShowApplication) application);
        }
        return addDirectoryAction;
    }

    public AddSlideShowAction addSlideShowAction() {
        if (addSlideShowAction == null) {
            addSlideShowAction = new AddSlideShowAction((ISlideShowApplication) application);
        }
        return addSlideShowAction;
    }

    public AddSlideShowToAction addSlideShowToAction() {
        if (addSlideShowToAction == null) {
            addSlideShowToAction = new AddSlideShowToAction((ISlideShowApplication) application);
        }
        return addSlideShowToAction;
    }

    public PauseSlideShowAction pauseSlideShowAction() {
        if (pauseSlideShowAction == null) {
            pauseSlideShowAction = new PauseSlideShowAction((ISlideShowApplication) application);
        }
        return pauseSlideShowAction;
    }

    public ResumeSlideShowAction resumeSlideShowAction() {
        if (resumeSlideShowAction == null) {
            resumeSlideShowAction = new ResumeSlideShowAction((ISlideShowApplication) application);
        }
        return resumeSlideShowAction;
    }

    public StartSlideShowAction startSlideShowAction() {
        if (startSlideShowAction == null) {
            startSlideShowAction = new StartSlideShowAction((ISlideShowApplication) application);
        }
        return startSlideShowAction;
    }

    public StopSlideShowAction stopSlideShowAction() {
        if (stopSlideShowAction == null) {
            stopSlideShowAction = new StopSlideShowAction((ISlideShowApplication) application);
        }
        return stopSlideShowAction;
    }

    public void copyable(boolean copyable) {
        copyAction().setEnabled(copyable);
    }

    public void pastable(boolean pastable) {
        pasteAction().setEnabled(pastable);
    }

    public void deletable(boolean deleteable) {
        deleteAction().setEnabled(deleteable);
    }

    public void startable(boolean startable) {
        startSlideShowAction().setEnabled(startable);
    }

}
