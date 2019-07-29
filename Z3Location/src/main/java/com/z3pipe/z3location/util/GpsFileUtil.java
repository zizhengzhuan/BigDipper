package com.z3pipe.z3location.util;

import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GpsFileUtil {

    private static ExecutorService executor;

    private static ExecutorService getExecutor() {
        if (executor == null) {
            executor = Executors.newSingleThreadExecutor();
        }
        return executor;
    }

    /**
     * 写到文件中
     *
     * @param filePath 文件路径
     * @param data     写入的数据内容
     * @param append   是否追加
     */
    public static void writeBinaryStream(final String filePath, final String data, final boolean append) {
        getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                FileOutputStream outStream = null;
                try {
                    //获取输出流
                    outStream = new FileOutputStream(filePath, append);
                    outStream.write(data.getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (outStream != null) {
                            outStream.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
