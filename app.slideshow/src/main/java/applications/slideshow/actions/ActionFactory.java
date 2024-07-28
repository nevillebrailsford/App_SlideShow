package applications.slideshow.actions;

import applications.slideshow.gui.IApplication;

public class ActionFactory {
    private AddDirectoryAction addDirectoryAction = null;
    private AddSlideShowAction addSlideShowAction = null;
    private AddSlideShowToAction addSlideShowToAction = null;
    private CopyAction copyAction = null;
    private DeleteAction deleteAction = null;
    private ExitApplicationAction exitApplicationAction = null;
    private PasteAction pasteAction = null;
    private PauseSlideShowAction pauseSlideShowAction = null;
    private PreferencesAction preferencesAction = null;
    private RedoAction redoAction = null;
    private ResumeSlideShowAction resumeSlideShowAction = null;
    private StartSlideShowAction startSlideShowAction = null;
    private StopSlideShowAction stopSlideShowAction = null;
    private UndoAction undoAction = null;
    private HelpAboutAction helpAboutAction = null;
    private static ActionFactory instance = null;
    private IApplication application = null;

    public synchronized static ActionFactory instance(IApplication application) {
        if (instance == null) {
            instance = new ActionFactory();
            instance.application = application;
        }
        return instance;
    }

    private ActionFactory() {
    }

    public AddDirectoryAction addDirectoryAction() {
        if (addDirectoryAction == null) {
            addDirectoryAction = new AddDirectoryAction(application);
        }
        return addDirectoryAction;
    }

    public AddSlideShowAction addSlideShowAction() {
        if (addSlideShowAction == null) {
            addSlideShowAction = new AddSlideShowAction(application);
        }
        return addSlideShowAction;
    }

    public AddSlideShowToAction addSlideShowToAction() {
        if (addSlideShowToAction == null) {
            addSlideShowToAction = new AddSlideShowToAction(application);
        }
        return addSlideShowToAction;
    }

    public CopyAction copyAction() {
        if (copyAction == null) {
            copyAction = new CopyAction(application);
        }
        return copyAction;
    }

    public DeleteAction deleteAction() {
        if (deleteAction == null) {
            deleteAction = new DeleteAction(application);
        }
        return deleteAction;
    }

    public ExitApplicationAction exitApplicationAction() {
        if (exitApplicationAction == null) {
            exitApplicationAction = new ExitApplicationAction(application);
        }
        return exitApplicationAction;
    }

    public PasteAction pasteAction() {
        if (pasteAction == null) {
            pasteAction = new PasteAction(application);
        }
        return pasteAction;
    }

    public PauseSlideShowAction pauseSlideShowAction() {
        if (pauseSlideShowAction == null) {
            pauseSlideShowAction = new PauseSlideShowAction(application);
        }
        return pauseSlideShowAction;
    }

    public PreferencesAction preferencesAction() {
        if (preferencesAction == null) {
            preferencesAction = new PreferencesAction(application);
        }
        return preferencesAction;
    }

    public RedoAction redoAction() {
        if (redoAction == null) {
            redoAction = new RedoAction(application);
        }
        return redoAction;
    }

    public ResumeSlideShowAction resumeSlideShowAction() {
        if (resumeSlideShowAction == null) {
            resumeSlideShowAction = new ResumeSlideShowAction(application);
        }
        return resumeSlideShowAction;
    }

    public StartSlideShowAction startSlideShowAction() {
        if (startSlideShowAction == null) {
            startSlideShowAction = new StartSlideShowAction(application);
        }
        return startSlideShowAction;
    }

    public StopSlideShowAction stopSlideShowAction() {
        if (stopSlideShowAction == null) {
            stopSlideShowAction = new StopSlideShowAction(application);
        }
        return stopSlideShowAction;
    }

    public UndoAction undoAction() {
        if (undoAction == null) {
            undoAction = new UndoAction(application);
        }
        return undoAction;
    }

    public HelpAboutAction helpAboutAction() {
        if (helpAboutAction == null) {
            helpAboutAction = new HelpAboutAction(application);
        }
        return helpAboutAction;
    }

}
