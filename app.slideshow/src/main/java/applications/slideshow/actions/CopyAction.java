package applications.slideshow.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import applications.slideshow.gui.IApplication;

public class CopyAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private IApplication application;

    public CopyAction(IApplication application) {
        super("Copy");
        this.application = application;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        application.copyAction();
    }

}
