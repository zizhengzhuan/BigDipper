package com.z3pipe.bigdipper.util;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

/**
 * Created by Administrator on 2018/12/24.
 */

public class ShareHelper {

    // 調用系統方法分享文件
    public static void shareFile(Activity context, File file) {
        if (null != file && file.exists()) {

            //超过Build.VERSION_CODES.M. 23版本，需要使用fileprovider.
            if (Build.VERSION.SDK_INT > 23){

                Intent share = new Intent(Intent.ACTION_SEND);

                 String authority = context.getApplication().getPackageName() + ".fileprovider";
                // String authority =context.getPackageName() + ".fileprovider";
                Uri photoOutputUri = FileProvider.getUriForFile(
                        context,
                        authority,
                        file);
                share.putExtra(Intent.EXTRA_STREAM, photoOutputUri);

                share.setType(getMimeType(file.getAbsolutePath()));//此处可发送多种文件
                share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    context.startActivity(Intent.createChooser(share, "分享文件"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                share.setType(getMimeType(file.getAbsolutePath()));//此处可发送多种文件
                share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    context.startActivity(Intent.createChooser(share, "分享文件"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            System.out.println("分享文件不存在");
        }
    }

    // 根据文件后缀名获得对应的MIME类型。
    private static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }

}
