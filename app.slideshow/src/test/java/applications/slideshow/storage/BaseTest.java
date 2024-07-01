package applications.slideshow.storage;

import static org.junit.jupiter.api.Assertions.assertTrue;
import application.notification.Notification;
import application.notification.NotificationListener;
import application.storage.LoadState;
import application.storage.Storage;
import application.storage.StorageNotificationType;
import application.storage.StoreState;
import java.io.File;
import org.junit.jupiter.api.io.TempDir;
import applications.slideshow.model.Directory;

public abstract class BaseTest {
    static boolean runMonitor = false;

    @TempDir
    File rootDirectory;

    Directory slideShow = new Directory("titlea");
    Directory slideShow2 = new Directory("titleb");
    Directory duplicateShow = new Directory("title");
    Directory directory = new Directory(new File("path"));

    Object waitForFinish = new Object();
    boolean storeSuccess = false;
    boolean loadSuccess = false;
    boolean failedIO = false;

    SlideShowStore slideShowStore = null;
    SlideShowLoad slideShowLoad = null;

    File modelFile = null;

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
            case Load -> {
                LoadState state = (LoadState) notification.subject().get();
                switch (state) {
                    case Complete -> loadData();
                    case Failed -> failed();
                    case Started -> ignore();
                }
            }
        }
    }

    private void ignore() {
    }

    private void storeData() {
        synchronized (waitForFinish) {
            storeSuccess = true;
            waitForFinish.notifyAll();
        }
    }

    private void loadData() {
        synchronized (waitForFinish) {
            loadSuccess = true;
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
        loadSuccess = false;
        failedIO = false;
    }

    void addASlideShow(Directory slideShow) throws Exception {
        SlideShowManager.instance().addSlideShow(slideShow);
        synchronized (waitForFinish) {
            waitForFinish.wait();
        }
    }

    void addASlideShowTo(Directory show, Directory newShow) throws Exception {
        SlideShowManager.instance().addSlideShowTo(show, newShow);
        synchronized (waitForFinish) {
            waitForFinish.wait();
        }
    }

    void addADirectory(Directory slideShow, Directory directory) throws Exception {
        SlideShowManager.instance().addDirectory(slideShow, directory);
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

    Storage initStorage() {
        modelFile = new File(rootDirectory, "model.dat");
        slideShowStore.setFileName(modelFile.getAbsolutePath());
        Storage storage = new Storage();
        return storage;
    }

    void writeToStore(Storage storage) throws InterruptedException {
        synchronized (waitForFinish) {
            storage.storeData(slideShowStore);
            waitForFinish.wait();
        }
    }

    void storeSlideShow(Directory slideShow) throws InterruptedException {
        synchronized (waitForFinish) {
            SlideShowManager.instance().addSlideShow(slideShow);
            waitForFinish.wait();
        }
    }

    void storeFolder(Directory slideShow, Directory directory) throws InterruptedException {
        synchronized (waitForFinish) {
            SlideShowManager.instance().addDirectory(slideShow, directory);
            waitForFinish.wait();
        }
    }

    void createNestedDirectory() throws InterruptedException {
        for (int i = 1; i < 3; i++) {
            Directory ss = new Directory("title" + i);
            synchronized (waitForFinish) {
                SlideShowManager.instance().addSlideShow(ss);
                waitForFinish.wait();
            }
            for (int j = 1; j < 5; j++) {
                Directory dir = new Directory(new File("path" + i * j));
                synchronized (waitForFinish) {
                    SlideShowManager.instance().addDirectory(ss, dir);
                    waitForFinish.wait();
                }
                for (int k = 1; k < 2; k++) {
                    Directory iss = new Directory("inner" + i * j * k);
                    synchronized (waitForFinish) {
                        SlideShowManager.instance().addSlideShowTo(ss, iss);
                        waitForFinish.wait();
                    }
                    for (int l = 1; l < 2; l++) {
                        Directory idir = new Directory(new File("ipath" + i * j * k * l));
                        synchronized (waitForFinish) {
                            SlideShowManager.instance().addDirectory(iss, idir);
                            waitForFinish.wait();
                        }
                    }
                }
            }
        }
    }

}
