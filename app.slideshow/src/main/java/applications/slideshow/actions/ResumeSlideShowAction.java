package applications.slideshow.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import applications.slideshow.gui.IApplication;

public class ResumeSlideShowAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private IApplication application;

    public ResumeSlideShowAction(IApplication application) {
        super("Resume slide show");
        this.application = application;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        application.resumeSlideShowAction();
    }

}
