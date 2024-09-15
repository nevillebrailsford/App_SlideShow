package applications.slideshow.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import applications.slideshow.ISlideShowApplication;

public class StartSlideShowAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private ISlideShowApplication application;

    public StartSlideShowAction(ISlideShowApplication application) {
        super("Start slide show");
        this.application = application;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        application.startSlideShowAction();
    }

}
