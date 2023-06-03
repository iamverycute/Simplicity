package com.iamverycute.simplicity.browser;

import android.annotation.SuppressLint;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

@SuppressWarnings("all")
public class DownloadItem {
    private File downloadFile;

    public File getDownloadFile() {
        return downloadFile;
    }

    public void setDownloadFile(File downloadFile) {
        this.downloadFile = downloadFile;
    }

    public int progress;

    public int getProgress() {
        return progress;
    }

    @SuppressLint({"SetTextI18n"})
    public void setProgress(int progress) {
        this.progress = progress;
        if (progressView != null) {
            progressView.setText(progress + "%");
            if (progress == 100) {
                if (fileSizeView != null) {
                    fileSizeView.setText("下载完成");
                }
                progressView.post(() -> Toast.makeText(progressView.getContext(), getDownloadFile().getName() + "，下载完成！", Toast.LENGTH_SHORT).show());
                Launch.downloadHistories.remove(this);
            }
        }
    }

    private TextView progressView;

    public TextView getProgressView() {
        return progressView;
    }

    public void setProgressView(TextView progressView) {
        this.progressView = progressView;
    }

    public TextView getFileSizeView() {
        return fileSizeView;
    }

    public void setFileSizeView(TextView fileSizeView) {
        this.fileSizeView = fileSizeView;
    }

    private TextView fileSizeView;
}
