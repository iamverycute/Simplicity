package com.iamverycute.simplicity.browser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class ListViewAdapter extends ArrayAdapter<File> implements View.OnClickListener, View.OnLongClickListener {
    private final int layout;
    private final List<File> fileList;

    public ListViewAdapter(Context context, int layout, List<File> fileList) {
        super(context, layout);
        this.layout = layout;
        this.fileList = fileList;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public File getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(layout, null);
        }
        File item = getItem(position);
        if (item != null) {
            TextView fileItem = (TextView) convertView.findViewById(R.id.leftTextView);
            if (fileItem != null) {
                fileItem.setText(item.getName());
                fileItem.setOnClickListener(this);
                fileItem.setOnLongClickListener(this);
                fileItem.setTag(position);
            }
        }
        return convertView;
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        File item = getItem(position);
        if (item != null) {
            if (item.getName().endsWith(".apk")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String type = "application/vnd.android.package-archive";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri downloadedApk = FileProvider.getUriForFile(getContext(), getContext().getPackageName(), item);
                    intent.setDataAndType(downloadedApk, type);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    intent.setDataAndType(Uri.fromFile(item), type);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                getContext().startActivity(intent);
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        int position = (int) view.getTag();
        File item = getItem(position);
        if (item != null)
            new AlertDialog.Builder(getContext()).setIcon(android.R.drawable.stat_sys_warning).setMessage("提示").setMessage("确认删除文件：" + item.getName() + "？")
                    .setNegativeButton("取消", null).setPositiveButton("确认", (dialogInterface, i) -> {
                        if (item.delete()) {
                            fileList.remove(item);
                            notifyDataSetChanged();
                        }
                    })
                    .show();
        return false;
    }
}