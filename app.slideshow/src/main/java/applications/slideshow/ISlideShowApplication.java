package applications.slideshow;

import application.base.app.IApplication;

public interface ISlideShowApplication extends IApplication {

    void addSlideShowAction();

    void addDirectoryAction();

    void addSlideShowToAction();

    void startSlideShowAction();

    void pauseSlideShowAction();

    void resumeSlideShowAction();

    void stopSlideShowAction();

    void copyAction();

    void pasteAction();

    void deleteAction();

    void showEnding();

}
