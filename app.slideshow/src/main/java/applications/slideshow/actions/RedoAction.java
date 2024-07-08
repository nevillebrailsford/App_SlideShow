package applications.slideshow.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import applications.slideshow.gui.IApplication;

public class RedoAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private IApplication application;

    public RedoAction(IApplication application) {
        super("redo");
        this.application = application;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        application.redoAction();
    }

}
