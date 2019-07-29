package com.z3pipe.z3location.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * @author yongzhan
 */
public class FileUtil {
    private Context a;
    private boolean b = false;
    private String c;
    private String d;
    private String e;
    private String f;
    private String g;
    public static String rootPath = "//SOP//sop";
    private static FileUtil h;

    public FileUtil(Context var1) {
        this.a = var1;
        this.b = Environment.getExternalStorageState().equals("mounted");
        this.c = Environment.getExternalStorageDirectory().getPath();
        FileUtil var5 = this;
//        if(this.a != null) {
//            ApplicationInfo var6;
//            try {
//                var6 = var5.a.getPackageManager().getApplicationInfo(var5.a.getPackageName(), 0);
//            } catch (NameNotFoundException var4) {
//                var6 = null;
//            }
//
//            String var2 = "";
//
//            try {
//                String[] var7;
//                if((var7 = var6.packageName.split("\\.")) != null) {
//                    var2 = var7[var7.length - 1];
//                }
//            } catch (Exception var3) {
//                ;
//            }
//
//            rootPath = "//SOP//" + var2;
//        } else {
//            rootPath = "//SOP";
//        }

        if(this.b) {
            this.f = this.c + rootPath + "//media//";
            this.g = this.c + rootPath + "//conf//";
            this.d = this.c + rootPath + "//map//";
        } else {
            this.c = Environment.getRootDirectory().getPath();
            this.f = this.c + rootPath + "//media//";
            this.g = this.c + rootPath + "//conf//";
            this.d = this.c + rootPath + "//map//";
        }

        this.hasFileDir(this.f);
        this.hasFileDir(this.getMediaPathforImage());
        this.hasFileDir(this.getMediaPathforCache());
        this.hasFileDir(this.g);
        this.hasFileDir(this.d);
    }

    public static synchronized FileUtil getInstance(Context var0) {
        if(h == null) {
            h = new FileUtil(var0);
        }

        return h;
    }

    public File createSDFile(String var1) throws IOException {
        File var2;
        if(!(var2 = new File(var1)).exists()) {
            var2.createNewFile();
        }

        return var2;
    }

    public boolean deleteSDFile(String var1) {
        File var2;
        return (var2 = new File(var1)).exists() && !var2.isDirectory()?var2.delete():false;
    }

    public boolean hasFile(String var1) {
        return (new File(var1)).exists();
    }

    public boolean hasFileDir(String var1) {
        File var2;
        if(!(var2 = new File(var1)).exists()) {
            var2.mkdirs();
        }

        return var2.exists();
    }

    public static void RecursionDeleteFile(String var0) {
        File var4;
        if((var4 = new File(var0)).isFile()) {
            var4.delete();
        } else {
            if(var4.isDirectory()) {
                File[] var1;
                if((var1 = var4.listFiles()) == null || var1.length == 0) {
                    var4.delete();
                    return;
                }

                File[] var3 = var1;
                int var2 = var1.length;

                for(int var5 = 0; var5 < var2; ++var5) {
                    RecursionDeleteFile(var3[var5].getPath());
                }

                var4.delete();
            }

        }
    }

    public static void DeleteFileInDirectoryWithBeforeDays(String var0, int var1) {
        try {
            File var6;
            if((var6 = new File(var0)).isFile()) {
                if(a(var6, var1)) {
                    var6.delete();
                }

            } else {
                if(var6.isDirectory()) {
                    File[] var4;
                    int var3 = (var4 = var6.listFiles()).length;

                    for(int var2 = 0; var2 < var3; ++var2) {
                        DeleteFileInDirectoryWithBeforeDays(var4[var2].getPath(), var1);
                    }

                    if(a(var6, var1)) {
                        var6.delete();
                    }
                }

            }
        } catch (OutOfMemoryError var5) {
            var5.printStackTrace();
        }
    }

    private static boolean a(File var0, int var1) {
        long var2 = var0.lastModified();
        Calendar var6;
        (var6 = Calendar.getInstance()).setTimeInMillis(var2);
        Date var7 = var6.getTime();
        Date var8 = new Date();
        long var4 = var7.getTime();
        return (int)((var8.getTime() - var4) / 86400000L) >= var1;
    }

    public String getFILESPATH(Context var1) {
        this.a = var1;
        this.e = this.a.getFilesDir().getPath();
        return this.e;
    }

    public String getSDPATH() {
        return this.c;
    }

    public String getRootPath() {
        return this.c + rootPath;
    }

    public boolean hasSD() {
        return this.b;
    }

    public String getLocalMapDocPath() {
        return this.d;
    }

    public String getMediaPath() {
        return this.f;
    }

    public String getConfPath() {
        return this.g;
    }

    public static String getSDPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    public static String formetFileSize(long var0) {
        DecimalFormat var2 = new DecimalFormat("#.00");
        String var3;
        if(var0 < 1024L) {
            var3 = var0 + " B";
        } else if(var0 < 1048576L) {
            var3 = var2.format((double)var0 / 1024.0D) + " K";
        } else if(var0 < 1073741824L) {
            var3 = var2.format((double)var0 / 1048576.0D) + " M";
        } else {
            var3 = var2.format((double)var0 / 1.073741824E9D) + " G";
        }

        return var3;
    }

    public static String combinPath(String var0, String var1) {
        return var0 + (var0.endsWith(File.separator)?"": File.separator) + var1;
    }

    public static boolean copyFile(File var0, File var1) throws Exception {
        if(var0.isFile()) {
            FileInputStream var2 = null;
            FileOutputStream var3 = null;
            BufferedInputStream var6 = null;
            BufferedOutputStream var8 = null;
            try {
                var2 = new FileInputStream(var0);
                var3 = new FileOutputStream(var1);
                var6 = new BufferedInputStream(var2);
                var8 = new BufferedOutputStream(var3);
                byte[] var4 = new byte[8192];

                for(int var5 = var6.read(var4); var5 != -1; var5 = var6.read(var4)) {
                    var8.write(var4, 0, var5);
                }
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                if (var8 != null){
                    var8.close();
                }
                if (var6 != null){
                    var6.close();
                }
                if (var3 != null){
                    var3.close();
                }
                if (var2 != null){
                    var2.close();
                }
            }
        }

        if(var0.isDirectory()) {
            File[] var7 = var0.listFiles();
            var1.mkdir();

            for(int var9 = 0; var9 < var7.length; ++var9) {
                copyFile(var7[var9].getAbsoluteFile(), new File(var1.getAbsoluteFile() + File.separator + var7[var9].getName()));
            }
        }

        return true;
    }

    public static boolean moveFile(File var0, File var1) throws Exception {
        if(copyFile(var0, var1)) {
            deleteFile(var0);
            return true;
        } else {
            return false;
        }
    }

    public static void deleteFile(File var0) {
        File[] var1;
        if(var0.isDirectory() && (var1 = var0.listFiles()) != null && var1.length > 0) {
            for(int var2 = 0; var2 < var1.length; ++var2) {
                deleteFile(var1[var2]);
            }
        }

        var0.delete();
    }

    public static String getMIMEType(String var0) {
        if((var0 = var0.substring(var0.lastIndexOf(".") + 1, var0.length()).toLowerCase()).equals("apk")) {
            return "application/vnd.android.package-archive";
        } else {
            if(!var0.equals("mp4") && !var0.equals("avi") && !var0.equals("3gp") && !var0.equals("rmvb")) {
                if(!var0.equals("m4a") && !var0.equals("mp3") && !var0.equals("mid") && !var0.equals("xmf") && !var0.equals("ogg") && !var0.equals("wav")) {
                    if(!var0.equals("jpg") && !var0.equals("gif") && !var0.equals("png") && !var0.equals("jpeg") && !var0.equals("bmp")) {
                        if(!var0.equals("txt") && !var0.equals("log")) {
                            var0 = "*";
                        } else {
                            var0 = "text";
                        }
                    } else {
                        var0 = "image";
                    }
                } else {
                    var0 = "audio";
                }
            } else {
                var0 = "video";
            }

            return var0 + "/*";
        }
    }

    public static void clearTileCacheFiles() {
    }

    public void saveBitmap(Bitmap var1, String var2) {
        try {
            if(!this.isFileExist("")) {
                this.createSDDir("");
            }

            File var5;
            if((var5 = new File(this.f + "images//", var2 + ".JPEG")).exists()) {
                var5.delete();
            }

            FileOutputStream var6 = new FileOutputStream(var5);
            var1.compress(CompressFormat.JPEG, 90, var6);
            var6.flush();
            var6.close();
        } catch (FileNotFoundException var3) {
            var3.printStackTrace();
        } catch (IOException var4) {
            var4.printStackTrace();
        }
    }

    public File createSDDir(String var1) throws IOException {
        File var2 = new File(this.f + "images//" + var1);
        Environment.getExternalStorageState().equals("mounted");
        return var2;
    }

    public String getMediaPathforCache() {
        this.hasFileDir(this.f + "cache//");
        return this.f + "cache//";
    }

    public String getMediaPathforImage() {
        this.hasFileDir(this.f + "images//");
        return this.f + "images//";
    }

    public String getConfigFeedForm(){
        this.hasFileDir(this.g + "feedform//");
        return this.g + "feedform//";
    }

    public String getConfigCacheFeedbackFormContent(){
        this.hasFileDir(this.g + "cachedFeedbackContent//");
        return this.g + "cachedFeedbackContent//";
    }

    public String getSopConfigCache(){
        this.hasFileDir(this.g + "sopConfigCache//");
        return this.g + "sopConfigCache//";
    }

    public String getConfigTask(){
        this.hasFileDir(this.g + "task//");
        return this.g + "task//";
    }

    public String getMapPathforCache(String var1, int var2, int var3) {
        var1 = this.d + "cacehs//" + var1 + "//L" + var2 + "//C" + var3 + "//";
        this.hasFileDir(var1);
        return var1;
    }

    public boolean isFileExist(String var1) {
        File var2;
        (var2 = new File(this.f + "images//" + var1)).isFile();
        return var2.exists();
    }

    public void delFile(String var1) {
        File var2;
        if((var2 = new File(this.f + "images//" + var1)).isFile()) {
            var2.delete();
        }

        var2.exists();
    }

    public void deleteDir(String var1) {
        File var6;
        if((var6 = new File(var1)).exists() && var6.isDirectory()) {
            File[] var5;
            int var4 = (var5 = var6.listFiles()).length;

            for(int var3 = 0; var3 < var4; ++var3) {
                File var2;
                if((var2 = var5[var3]).isFile()) {
                    var2.delete();
                } else if(var2.isDirectory()) {
                    this.deleteCacheDir();
                }
            }

            var6.delete();
        }
    }

    public void deleteImagesDir() {
        File var1;
        if((var1 = new File(this.f + "images//")).exists() && var1.isDirectory()) {
            File[] var5;
            int var4 = (var5 = var1.listFiles()).length;

            for(int var3 = 0; var3 < var4; ++var3) {
                File var2;
                if((var2 = var5[var3]).isFile()) {
                    var2.delete();
                } else if(var2.isDirectory()) {
                    this.deleteCacheDir();
                }
            }

            var1.delete();
        }
    }

    public void deleteCacheDir() {
        File var1;
        if((var1 = new File(this.f + "cache//")).exists() && var1.isDirectory()) {
            File[] var5;
            int var4 = (var5 = var1.listFiles()).length;

            for(int var3 = 0; var3 < var4; ++var3) {
                File var2;
                if((var2 = var5[var3]).isFile()) {
                    var2.delete();
                } else if(var2.isDirectory()) {
                    this.deleteCacheDir();
                }
            }

            var1.delete();
        }
    }

    public boolean fileIsExists(String var1) {
        try {
            return (new File(var1)).exists();
        } catch (Exception var2) {
            return false;
        }
    }
}