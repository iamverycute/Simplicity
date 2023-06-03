package com.iamverycute.simplicity.browser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class ListViewAdapter extends ArrayAdapter<File> implements View.OnClickListener, View.OnLongClickListener {
    private final int layout;
    private List<File> fileList;
    public int mode = R.id.down_done;

    public ListViewAdapter(Context context, int layout, List<File> fileList) {
        super(context, layout);
        this.layout = layout;
        this.fileList = fileList;
    }

    public void updateFileList(List<File> fileList) {
        this.fileList = fileList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public File getItem(int position) {
        return fileList.get(position);
    }

    @SuppressLint({"DefaultLocale"})
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(layout, null);
        }
        TextView fileSize = convertView.findViewById(R.id.file_size);
        File item = getItem(position);
        if (item != null) {
            TextView fileItem = convertView.findViewById(R.id.file_item);
            fileItem.setText(item.getName());
            if (mode == R.id.down_done) {
                float k = 1048576;
                fileSize.setText(String.format("%.2f%s", (item.length() / k), "MB"));
            } else {
                fileSize.setText(R.string.downloading);
            }
            LinearLayout fileInfo = convertView.findViewById(R.id.file_info);
            fileInfo.setOnClickListener(this);
            fileInfo.setOnLongClickListener(this);
            fileInfo.setTag(position);
        }
        TextView progress_show = convertView.findViewById(R.id.download_progress);
        if (mode == R.id.down_done) {
            progress_show.setVisibility(View.GONE);
        } else {
            progress_show.setVisibility(View.VISIBLE);
            Launch.downloadHistories.stream().filter(f -> f.getDownloadFile() == item).findFirst().ifPresent(v ->{
                v.setProgressView(progress_show);
                v.setFileSizeView(fileSize);
            });
        }
        return convertView;
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        File item = getItem(position);
        if (item != null) {
            if (mode == R.id.down_done || Launch.downloadHistories.stream().noneMatch(f -> f.getDownloadFile().getAbsolutePath().equals(item.getAbsolutePath()))) {
                if (item.getName().endsWith(".apk")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("apk");
                    intent.setDataAndType(FileProvider.getUriForFile(getContext(), getContext().getPackageName(), item), mimeType);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                }
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        int position = (int) view.getTag();
        File item = getItem(position);
        if (item != null) {
            if (mode == R.id.down_done) {
                new SimpleUtils.SimpleDialog(getContext(), getContext().getString(R.string.tips), "确认删除文件：" + item.getName() + "？")
                        .addCancelBtn(R.string.cancel, null).addOKBtn(R.string.confirm, (dialogInterface, i) -> {
                            if (item.delete()) {
                                fileList.remove(item);
                                notifyDataSetChanged();
                            }
                        }).show();
            } else {
                if (Launch.downloadHistories.stream().anyMatch(f -> f.getDownloadFile().getAbsolutePath().equals(item.getAbsolutePath())))
                    new SimpleUtils.SimpleDialog(getContext(), getContext().getString(R.string.tips), "取消下载：" + item.getName() + "？")
                            .addCancelBtn(R.string.cancel, null).addOKBtn(R.string.confirm, (dialogInterface, i) -> Launch.downloadHistories.stream().filter(downloadItem -> downloadItem.getDownloadFile() == item).findFirst().ifPresent(downloadItem -> {
                                //downloadItem.getRequest().cancel();
                                fileList.remove(item);
                                notifyDataSetChanged();
                                Launch.downloadHistories.remove(downloadItem);
                            })).show();
            }
        }
        return false;
    }
}