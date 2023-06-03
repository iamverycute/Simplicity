package com.iamverycute.simplicity.browser;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DownloadListActivity extends Activity {
    private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.down);
        List<File> downloadFiles = getDownloadFiles();
        if (downloadFiles != null) {
            adapter = new ListViewAdapter(this, R.layout.item, getDownloadFiles());
            ((ListView) findViewById(R.id.fileList)).setAdapter(adapter);
        } else {
            finish();
        }
    }

    public List<File> getDownloadFiles() {
        File downloadDir = new SimpleUtils.SimpleDirectory().DownloadDir(this);
        if (downloadDir != null) {
            File[] files = downloadDir.listFiles(file -> file.isFile() && Launch.downloadHistories.stream().noneMatch(item -> item.getDownloadFile().getAbsolutePath().equals(file.getAbsolutePath())));
            if (files != null)
                return new LinkedList<>(Arrays.asList(files));
        }
        return null;
    }

    public void OnClick(View v) {
        if (v.getId() == adapter.mode) {
            return;
        }
        TextView tv;
        if (v.getId() == R.id.downing) {
            tv = findViewById(R.id.down_done);
            List<File> downList = new ArrayList<>();
            Launch.downloadHistories.forEach(item -> downList.add(item.getDownloadFile()));
            adapter.updateFileList(downList);
        } else {
            tv = findViewById(R.id.downing);
            adapter.updateFileList(getDownloadFiles());
        }
        tv.setBackgroundColor(0);
        tv.setTextColor(Color.WHITE);
        adapter.notifyDataSetChanged();
        adapter.mode = v.getId();
        ((TextView) v).setTextColor(Color.BLACK);
        v.setBackground(getDrawable(R.color.progress));
    }
}