package applications.slideshow.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import applications.slideshow.ISlideShowApplication;

public class ResumeSlideShowAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private ISlideShowApplication application;

    public ResumeSlideShowAction(ISlideShowApplication application) {
        super("Resume slide show");
        this.application = application;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        application.resumeSlideShowAction();
    }

}
