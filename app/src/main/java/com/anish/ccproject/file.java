package com.anish.ccproject;

public class file {
    public String filename,owned,shared;

    public file() {
    }

    public file(String filename, String owned, String shared) {
        this.filename = filename;
        this.owned = owned;
        this.shared = shared;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getOwned() {
        return owned;
    }

    public void setOwned(String owned) {
        this.owned = owned;
    }

    public String getShared() {
        return shared;
    }

    public void setShared(String shared) {
        this.shared = shared;
    }
}
