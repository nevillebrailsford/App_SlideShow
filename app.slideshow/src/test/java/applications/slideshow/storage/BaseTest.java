package applications.slideshow.storage;

import static org.junit.jupiter.api.Assertions.assertTrue;
import application.notification.Notification;
import application.notification.NotificationListener;
import application.storage.StorageNotificationType;
import application.storage.StoreState;
import java.io.File;
import org.junit.jupiter.api.io.TempDir;
import applications.slideshow.model.Folder;
import applications.slideshow.model.SlideShow;

public abstract class BaseTest {

    @TempDir
    File rootDirectory;

    SlideShow slideShow = new SlideShow("title");
    SlideShow slideShow2 = new SlideShow("title2");
    SlideShow duplicateShow = new SlideShow("title");
    Folder folder = new Folder(new File("path"));

    Object waitForFinish = new Object();
    boolean storeSuccess = false;
    boolean storeFailed = false;
    boolean failedIO = false;

    NotificationListener listener = new NotificationListener() {
        @Override
        public void notify(Notification notification) {
            if (notification.notificationType() instanceof StorageNotificationType) {
                assertTrue(notification.subject().isPresent());
                handleStorage(notification);
            }
        }
    };

    private void handleStorage(Notification notification) {
        StorageNotificationType type = (StorageNotificationType) notification.notificationType();
        switch (type) {
            case Store -> {
                StoreState state = (StoreState) notification.subject().get();
                switch (state) {
                    case Complete -> storeData();
                    case Failed -> failed();
                    case Started -> ignore();
                }
            }
            case Load -> ignore();
        }
    }

    private void ignore() {
    }

    private void storeData() {
        synchronized (waitForFinish) {
            waitForFinish.notifyAll();
        }
    }

    private void failed() {
        synchronized (waitForFinish) {
            failedIO = true;
            waitForFinish.notifyAll();
        }
    }

    void resetFlags() {
        storeSuccess = false;
        storeFailed = false;
        failedIO = false;
    }

    void addASlideShow(SlideShow slideShow) throws Exception {
        SlideShowManager.instance().addSlideShow(slideShow);
        synchronized (waitForFinish) {
            waitForFinish.wait();
        }
    }

    void addAFolder(SlideShow slideShow, Folder folder) throws Exception {
        SlideShowManager.instance().addFolder(slideShow, folder);
        synchronized (waitForFinish) {
            waitForFinish.wait();
        }
    }

    void clearSlideShows() throws Exception {
        SlideShowManager.instance().clear();
        synchronized (waitForFinish) {
            waitForFinish.wait();
        }
    }

}
