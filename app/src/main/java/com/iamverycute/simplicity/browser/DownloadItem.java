package com.iamverycute.simplicity.browser;

import java.io.File;

public class DownloadItem {
    private File downloadFile;

    public File getDownloadFile() {
        return downloadFile;
    }

    public void setDownloadFile(File downloadFile) {
        this.downloadFile = downloadFile;
    }

    public BinaryRequest getRequest() {
        return request;
    }

    public void setRequest(BinaryRequest request) {
        this.request = request;
    }

    private BinaryRequest request;
}
