package applications.slideshow.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import applications.slideshow.ISlideShowApplication;

public class DeleteAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private ISlideShowApplication application;

    public DeleteAction(ISlideShowApplication application) {
        super("Delete");
        this.application = application;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        application.deleteAction();
    }

}
