package com.z3pipe.z3location.util;

import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Textwriter {
    private volatile static int recordCount = 0;
    private volatile static int recordIndex = 0;
    
    public static void write(String path, String content) {
        if (isBlank(path) || isBlank(content)) {
            return;
        }
        try {
            FileWriter writer = new FileWriter(path, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * 从文件内读取字符Json字符传数据
     * @param path 文件路径
     * @return JSONObject对象
     */
    public static String readString(String path) {
        File file = new File(path);

        if (null == file || !file.exists()) {
            return null;
        }
        RandomAccessFile raf = null;
        try {
            //从文件读取字符串
            raf = new RandomAccessFile(file, "rwd");
            raf.seek(0);
            byte[] buffer = new byte[(int) raf.length()];
            raf.read(buffer);
            String contentStr = new String(buffer,"UTF-8");
            if (isBlank(contentStr)) {
                return null;
            }
            return contentStr;
        } catch (Exception e) {
            Log.e("Textwriter", e.getMessage());
            return null;
        }finally{
            try {
                raf.close();
            } catch (IOException e) {
                Log.e("Textwriter", e.getMessage());
                return null;
            }
        }
    }

    private static boolean isBlank(String string) {
        if(null == string || string.trim().length() == 0) {
            return true;
        }

        return false;
    }
}
