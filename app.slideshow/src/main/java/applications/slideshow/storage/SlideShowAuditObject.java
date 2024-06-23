package applications.slideshow.storage;

import application.audit.AuditObject;

public enum SlideShowAuditObject implements AuditObject {
    Folder("Folder"), SlideShow("SlideShow");

    private String object;

    SlideShowAuditObject(String object) {
        this.object = object;
    }

    @Override
    public String object() {
        return object;
    }

}
