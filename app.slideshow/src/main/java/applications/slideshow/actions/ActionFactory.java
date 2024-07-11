package applications.slideshow.actions;

import applications.slideshow.gui.IApplication;

public class ActionFactory {
    private UndoAction undoAction = null;
    private RedoAction redoAction = null;
    private CopyAction copyAction = null;
    private PasteAction pasteAction = null;
    private DeleteAction deleteAction = null;

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

    public UndoAction undoAction() {
        if (undoAction == null) {
            undoAction = new UndoAction(application);
        }
        return undoAction;
    }

    public RedoAction redoAction() {
        if (redoAction == null) {
            redoAction = new RedoAction(application);
        }
        return redoAction;
    }

    public CopyAction copyAction() {
        if (copyAction == null) {
            copyAction = new CopyAction(application);
        }
        return copyAction;
    }

    public PasteAction pasteAction() {
        if (pasteAction == null) {
            pasteAction = new PasteAction(application);
        }
        return pasteAction;
    }

    public DeleteAction deleteAction() {
        if (deleteAction == null) {
            deleteAction = new DeleteAction(application);
        }
        return deleteAction;
    }
}
