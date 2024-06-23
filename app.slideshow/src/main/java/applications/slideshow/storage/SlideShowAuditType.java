package applications.slideshow.storage;

import application.audit.AuditType;

public enum SlideShowAuditType implements AuditType {
    Added("added"), Changed("changed"), Removed("removed");

    private String type;

    SlideShowAuditType(String type) {
        this.type = type;
    }

    @Override
    public String type() {
        return type;
    }
}
