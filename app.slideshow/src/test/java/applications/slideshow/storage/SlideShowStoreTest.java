package applications.slideshow.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import application.definition.ApplicationConfiguration;
import application.definition.ApplicationDefinition;
import application.logging.LogConfigurer;
import application.notification.NotificationCentre;
import application.notification.NotificationMonitor;
import application.storage.Storage;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SlideShowStoreTest extends BaseTest {

    private static final int NUMBER_OF_LINES_IN_SAMPLE = 115;

    @BeforeEach
    void setUp() throws Exception {
        resetFlags();
        ApplicationDefinition app = new ApplicationDefinition("test") {
            @Override
            public Level level() {
                return Level.OFF;
            }
        };
        ApplicationConfiguration.registerApplication(app, rootDirectory.getAbsolutePath());
        LogConfigurer.setUp();
        NotificationCentre.addListener(listener);
        if (runMonitor)
            new NotificationMonitor(System.out);
        slideShowStore = new SlideShowStore();
    }

    @AfterEach
    void tearDown() throws Exception {
        synchronized (waitForFinish) {
            SlideShowManager.instance().clear();
            waitForFinish.wait();
        }
        ApplicationConfiguration.clear();
        LogConfigurer.shutdown();
        NotificationCentre.clear();
    }

    @Test
    void testStore() throws Exception {
        Storage storage = initStorage();
        createNestedDirectory();
        writeToStore(storage);
        List<String> lines = Files.readAllLines(modelFile.toPath());
        assertEquals(NUMBER_OF_LINES_IN_SAMPLE, lines.size());
    }

}
