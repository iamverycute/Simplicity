package com.iamverycute.simplicity.browser;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.widget.EditText;

import java.io.File;

public class SimpleUtils {
    static class SimpleDirectory {
        public File DownloadDir(Activity context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intent);
                    return null;
                }
            } else {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x01);
                    return null;
                }
            }
            File downloadDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Download");
            if (downloadDir.exists() && downloadDir.canRead() && downloadDir.canWrite()) {
                return downloadDir;
            }
            return null;
        }
    }

    static class SimpleDialog {
        private final AlertDialog.Builder builder;

        public SimpleDialog(Context context, String title, String msg) {
            builder = new AlertDialog.Builder(context).setTitle(title).setMessage(msg);
        }

        public SimpleDialog addOKBtn(int resId, DialogInterface.OnClickListener onClickListener) {
            builder.setPositiveButton(resId, onClickListener);
            return this;
        }

        public SimpleDialog addCancelBtn(int resId, DialogInterface.OnClickListener onClickListener) {
            builder.setNegativeButton(resId, onClickListener);
            return this;
        }

        public SimpleDialog isHttpBasic() {
            builder.setView(R.layout.input);
            return this;
        }

        static class InputValues {
            public EditText arg0;
            public EditText arg1;
        }

        public InputValues showForResult() {
            AlertDialog view = builder.show();
            InputValues values = new InputValues();
            values.arg0 = view.findViewById(R.id.arg0);
            values.arg1 = view.findViewById(R.id.arg1);
            return values;
        }

        public void show() {
            builder.show();
        }
    }
}
