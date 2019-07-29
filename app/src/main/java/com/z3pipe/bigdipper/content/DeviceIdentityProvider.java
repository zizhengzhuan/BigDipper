package com.z3pipe.bigdipper.content;

import android.content.Context;
import android.os.Environment;

import com.z3pipe.bigdipper.util.DeviceIdentifier;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author zhengzhuanzi
 */
public class DeviceIdentityProvider {
    private String filePath = null;
    private static DeviceIdentityProvider instance;
    private Context mContext;
    private boolean mNewDevice;
    private String deviceId;

    private DeviceIdentityProvider(Context ctx) {
        this.mContext = ctx;
        init();
    }

    public static synchronized DeviceIdentityProvider getInstance(Context ctx) {
        if (instance == null) {
            instance = new DeviceIdentityProvider(ctx);
        }

        return instance;
    }

    private void init() {
        mNewDevice = false;
        initFilePath();
        initID();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public boolean validateDeviceId(final String deviceId) {
        String storedDeviceId = getDeviceId();

        return storedDeviceId != null && storedDeviceId.equals(deviceId);
    }

    public boolean isAuthorizedDevice() {
        return validateDeviceId(generateDeviceId());
    }

    public String generateDeviceId() {
        // try to generate a device id, if it fails generate the pseudo one to be fault tolerant
        try {
            return DeviceIdentifier.getDeviceIdentifier(mContext, true);
        } catch (DeviceIdentifier.DeviceIDException e) {
            e.printStackTrace();
            return DeviceIdentifier.getPseudoDeviceId();
        }
    }

    public boolean isNewDevice() {
        return mNewDevice;
    }

    private void writeBinaryStream() {
        FileOutputStream outStream = null;
        try {
            //获取输出流
            outStream = new FileOutputStream(filePath);
            outStream.write(deviceId.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readBinaryStream() {
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
            byte[] data = outStream.toByteArray();
            //关闭流
            deviceId = new String(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initFilePath() {
        boolean hasSD = Environment.getExternalStorageState().equals("mounted");
        String rootPath = Environment.getExternalStorageDirectory().getPath();
        String path = "";
        if (hasSD) {
            path = rootPath + "//.identifier//";
        } else {
            rootPath = Environment.getRootDirectory().getPath();
            path = rootPath + "//.identifier//";
        }

        File pathDir = new File(path);
        if (!pathDir.exists()) {
            pathDir.mkdirs();
        }

        filePath = path + ".ids.bin";

        try {
            if (!new File(filePath).exists()) {
                new File(filePath).createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initID() {
        readBinaryStream();
        if (null == deviceId || deviceId.length() < 5) {
            deviceId = generateDeviceId();
            mNewDevice = true;
        }
        writeBinaryStream();
    }
}