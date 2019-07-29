package com.z3pipe.z3core.base;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @author zhengzhuanzi on 2018/9/17.
 */

public abstract class BaseSdFileReadWriter {
    /**
     * 写到文件中
     * 这种写入方式为覆盖写入，会导致之前的数据覆盖
     *
     * @param filePath 文件路径
     * @param data     写入的数据内容
     */
    protected boolean writeBinaryStream(String filePath, String data) {
        return writeBinaryStream(filePath, data, false);
    }

    /**
     * 写到文件中
     *
     * @param filePath 文件路径
     * @param data     写入的数据内容
     * @param append   是否追加
     */
    protected boolean writeBinaryStream(String filePath, String data, boolean append) {
        return writeBinary(filePath, data.getBytes(), append);
    }

    /**
     * 写到文件中
     *
     * @param filePath 文件路径
     * @param data     写入的数据内容
     * @param append   是否追加
     */
    protected boolean writeBinary(String filePath, byte[] data, boolean append) {
        FileOutputStream outStream = null;
        boolean result;
        try {
            //获取输出流
            outStream = new FileOutputStream(filePath, append);
            outStream.write(data);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 从文件中读取
     *
     * @return
     */
    protected String readBinaryStream(String filePath) {
        try {
            //关闭流
            return new String(readBinary(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 从文件中读取
     *
     * @return
     */
    protected byte[] readBinary(String filePath) {
        FileInputStream inStream = null;
        ByteArrayOutputStream outStream = null;
        try {
            File file = new File(filePath);
            //获得输入流
            inStream = new FileInputStream(file);
            //new一个缓冲区
            byte[] buffer = new byte[1024];
            int len = 0;
            //使用ByteArrayOutputStream类来处理输出流
            outStream = new ByteArrayOutputStream();
            while ((len = inStream.read(buffer)) != -1) {
                //写入数据
                outStream.write(buffer, 0, len);
            }
            //得到文件的二进制数据
            return outStream.toByteArray();
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
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
