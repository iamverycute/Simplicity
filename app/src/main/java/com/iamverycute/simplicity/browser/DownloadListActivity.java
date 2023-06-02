package com.iamverycute.simplicity.browser;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.widget.ListView;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DownloadListActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.down);
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }
        File downloadDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Download");
        ListViewAdapter adapter = new ListViewAdapter(this, R.layout.item, Arrays.stream(downloadDir.listFiles()).filter(File::isFile).collect(Collectors.toList()));
        ListView view = (ListView) findViewById(R.id.fileList);
        view.setAdapter(adapter);
    }
}
