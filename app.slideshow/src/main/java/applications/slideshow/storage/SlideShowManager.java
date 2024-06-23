package applications.slideshow.storage;

import application.audit.AuditService;
import application.definition.ApplicationConfiguration;
import application.storage.Storage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import applications.slideshow.model.Folder;
import applications.slideshow.model.SlideShow;

public class SlideShowManager {
    private static final String CLASS_NAME = SlideShowManager.class.getName();
    private static final Logger LOGGER = ApplicationConfiguration.logger();

    private static SlideShowManager instance = null;

    private final List<Folder> folders;

    public synchronized static SlideShowManager instance() {
        LOGGER.entering(CLASS_NAME, "instance");
        if (instance == null) {
            instance = new SlideShowManager();
        }
        LOGGER.exiting(CLASS_NAME, "instance", instance);
        return instance;
    }

    private SlideShowManager() {
        LOGGER.entering(CLASS_NAME, "cinit");
        folders = new ArrayList<>();
        LOGGER.exiting(CLASS_NAME, "cinit");
    }

    public void clear() {
        LOGGER.entering(CLASS_NAME, "clear");
        folders.clear();
        updateStorage();
        LOGGER.exiting(CLASS_NAME, "clear");
    }

    public List<Folder> allFolders() {
        LOGGER.entering(CLASS_NAME, "allFolders");
        List<Folder> copyList = folders.stream().sorted().collect(Collectors.toList());
        LOGGER.exiting(CLASS_NAME, "allFolders", copyList);
        return copyList;
    }

    public List<SlideShow> slideShows() {
        LOGGER.entering(CLASS_NAME, "slideShows");
        List<SlideShow> copyList = folders.stream().filter((f) -> f.isSlideShow()).map((f) -> f.asSlideShow()).sorted()
                .collect(Collectors.toList());
        LOGGER.exiting(CLASS_NAME, "slideShows", copyList);
        return copyList;
    }

    public List<Folder> folders(SlideShow show) {
        LOGGER.entering(CLASS_NAME, "folders");
        List<Folder> copyList = show.folders().stream().filter((f) -> f.isFolder()).sorted()
                .collect(Collectors.toList());
        LOGGER.exiting(CLASS_NAME, "folders", copyList);
        return copyList;
    }

    public List<SlideShow> slideShows(SlideShow show) {
        LOGGER.entering(CLASS_NAME, "slideShows");
        List<SlideShow> copyList = show.folders().stream().filter((f) -> f.isSlideShow()).map((f) -> f.asSlideShow())
                .sorted().collect(Collectors.toList());
        LOGGER.exiting(CLASS_NAME, "slideShows", copyList);
        return copyList;
    }

    public void addSlideShow(SlideShow newShow) {
        LOGGER.entering(CLASS_NAME, "addSlideShow", newShow);
        if (newShow == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: newShow is null");
            LOGGER.throwing(CLASS_NAME, "addSlideShow", exc);
            LOGGER.exiting(CLASS_NAME, "addSlideShow");
            throw exc;
        }
        if (slideShows().contains(newShow)) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: newShow already exists");
            LOGGER.throwing(CLASS_NAME, "addSlideShow", exc);
            LOGGER.exiting(CLASS_NAME, "addSlideShow");
            throw exc;
        }
        try {
            synchronized (folders) {
                folders.add(newShow);
                AuditService.writeAuditInformation(SlideShowAuditType.Added, SlideShowAuditObject.SlideShow,
                        newShow.title());
                updateStorage();
            }
        } catch (Exception e) {
            LOGGER.warning("Caught exception: " + e.getMessage());
            LOGGER.throwing(CLASS_NAME, "addSlideShow", e);
            LOGGER.exiting(CLASS_NAME, "addSlideShow");
            throw e;
        }
        LOGGER.exiting(CLASS_NAME, "addSlideShow");
    }

    public void addSlideShowTo(SlideShow show, SlideShow newShow) {
        LOGGER.entering(CLASS_NAME, "addSlideShowTo", new Object[] { show, newShow });
        if (show == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: show is null");
            LOGGER.throwing(CLASS_NAME, "addSlideShowTo", exc);
            LOGGER.exiting(CLASS_NAME, "addSlideShowTo");
            throw exc;
        }
        if (newShow == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: newShow is null");
            LOGGER.throwing(CLASS_NAME, "addSlideShowTo", exc);
            LOGGER.exiting(CLASS_NAME, "addSlideShowTo");
            throw exc;
        }
        if (show.equals(newShow)) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: show equals newShow");
            LOGGER.throwing(CLASS_NAME, "addSlideShowTo", exc);
            LOGGER.exiting(CLASS_NAME, "addSlideShowTo");
            throw exc;
        }
        if (!slideShows().contains(show)) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: show not known");
            LOGGER.throwing(CLASS_NAME, "addSlideShowTo", exc);
            LOGGER.exiting(CLASS_NAME, "addSlideShowTo");
            throw exc;
        }
        if (show.slideShows().contains(newShow)) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: newShow already in show");
            LOGGER.throwing(CLASS_NAME, "addSlideShowTo", exc);
            LOGGER.exiting(CLASS_NAME, "addSlideShowTo");
            throw exc;
        }
        try {
            synchronized (folders) {
                SlideShow slideShow = findSlideShow(show);
                if (slideShow != null) {
                    slideShow.addFolder(slideShow);
                    AuditService.writeAuditInformation(SlideShowAuditType.Added, SlideShowAuditObject.SlideShow,
                            newShow.title() + " added to " + show.title());
                    updateStorage();
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Caught exception: " + e.getMessage());
            LOGGER.throwing(CLASS_NAME, "addFolder", e);
            LOGGER.exiting(CLASS_NAME, "addFolder");
            throw e;
        }
        LOGGER.exiting(CLASS_NAME, "addSlideShowTo");
    }

    public void addFolder(SlideShow slideShow, Folder newFolder) {
        LOGGER.entering(CLASS_NAME, "addFolder", new Object[] { slideShow, newFolder });
        if (slideShow == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: slideShow is null");
            LOGGER.throwing(CLASS_NAME, "addFolder", exc);
            LOGGER.exiting(CLASS_NAME, "addFolder");
            throw exc;
        }
        if (newFolder == null) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: newFolder is null");
            LOGGER.throwing(CLASS_NAME, "addFolder", exc);
            LOGGER.exiting(CLASS_NAME, "addFolder");
            throw exc;
        }
        if (!slideShows().contains(slideShow)) {
            IllegalArgumentException exc = new IllegalArgumentException("SlideShowManager: slideShow not known");
            LOGGER.throwing(CLASS_NAME, "addFolder", exc);
            LOGGER.exiting(CLASS_NAME, "addFolder");
            throw exc;
        }
        try {
            synchronized (folders) {
                SlideShow show = findSlideShow(slideShow);
                if (show != null) {
                    slideShow.addFolder(newFolder);
                    AuditService.writeAuditInformation(SlideShowAuditType.Added, SlideShowAuditObject.Folder,
                            newFolder.path());
                    updateStorage();
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Caught exception: " + e.getMessage());
            LOGGER.throwing(CLASS_NAME, "addFolder", e);
            LOGGER.exiting(CLASS_NAME, "addFolder");
            throw e;
        }
        LOGGER.exiting(CLASS_NAME, "addFolder");
    }

    private SlideShow findSlideShow(SlideShow show) {
        LOGGER.entering(CLASS_NAME, "findSlideShow", show);
        List<SlideShow> shows = slideShows().stream().filter((s) -> s.equals(show)).collect(Collectors.toList());
        SlideShow result = shows.size() == 1 ? shows.get(0) : null;
        LOGGER.exiting(CLASS_NAME, "findSlideShow", result);
        return result;
    }

    private void updateStorage() {
        LOGGER.entering(CLASS_NAME, "updateStorage");
        SlideShowStore slideShowStore = new SlideShowStore();
        File modelDirectory = obtainModelDirectory();
        File dataFile = new File(modelDirectory, ModelConstants.SLIDE_SHOW_FILE);
        slideShowStore.setFileName(dataFile.getAbsolutePath());
        Storage storage = new Storage();
        storage.storeData(slideShowStore);
        LOGGER.exiting(CLASS_NAME, "updateStorage");
    }

    private File obtainModelDirectory() {
        LOGGER.entering(CLASS_NAME, "obtainModelDirectory");
        File rootDirectory = ApplicationConfiguration.rootDirectory();
        File applicationDirectory = new File(rootDirectory,
                ApplicationConfiguration.applicationDefinition().applicationName());
        File modelDirectory = new File(applicationDirectory, ModelConstants.MODEL);
        if (!modelDirectory.exists()) {
            LOGGER.fine("Model directory " + modelDirectory.getAbsolutePath() + " does not exist");
            if (!modelDirectory.mkdirs()) {
                LOGGER.warning("Unable to create model directory");
                modelDirectory = null;
            } else {
                LOGGER.fine("Created model directory " + modelDirectory.getAbsolutePath());
            }
        } else {
            LOGGER.fine("Model directory " + modelDirectory.getAbsolutePath() + " does exist");
        }
        LOGGER.exiting(CLASS_NAME, "obtainModelDirectory", modelDirectory);
        return modelDirectory;
    }

}
