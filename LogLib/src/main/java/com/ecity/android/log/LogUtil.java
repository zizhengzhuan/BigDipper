package com.ecity.android.log;

import android.util.Log;

public class LogUtil {
    public static final int LOG_LEVEL_VERBOSE = Log.VERBOSE;
    public static final int LOG_LEVEL_DEBUG = Log.DEBUG;
    public static final int LOG_LEVEL_INFO = Log.INFO;
    public static final int LOG_LEVEL_WARN = Log.WARN;
    public static final int LOG_LEVEL_ERROR = Log.ERROR;

    private static String logPath;
    private static String logFileName;
    private static String logFileSuffix;
    private static final String DEFAULT_TAG = "LogUtil";
    private static int mlogLevel = LOG_LEVEL_INFO;

    private static long SINGLE_FILE_MAX_SIZE = 5 * 1024 * 1024;//  5 MB

    public static void init(String path, String fileName, String fileSuffix, int logLevel) {
        if (isBlank(path) || isBlank(fileName) || isBlank(fileSuffix)) {
            String msg = "Can not init logger with the given path='" + path + "', fileName='" + fileName + "' and fileSuffix='" + fileSuffix + "'.";
            throw new RuntimeException(msg);
        }

        if (isBlank(logPath)) {
            logPath = path;
        }
        if (isBlank(logFileName)) {
            logFileName = fileName;
        }
        if (isBlank(logFileSuffix)) {
            logFileSuffix = fileSuffix;
        }

        mlogLevel = logLevel;

        com.ecity.android.log.Logger.setEnabled(true);
        com.ecity.android.log.Logger.setLog2ConsoleEnabled(true);
        com.ecity.android.log.Logger.setLog2FileEnabled(true);
        com.ecity.android.log.Logger.setFilePathGenerator(new FilePathGenerator.LimitMaxSizeFilePathGenerator(logPath, logFileName, logFileSuffix, SINGLE_FILE_MAX_SIZE));

    }

    public static void v(Object tag, String msg) {
        log(Log.VERBOSE, tag, msg, null);
    }

    public static void d(Object tag, String msg) {
        log(Log.DEBUG, tag, msg, null);
    }

    public static void i(Object tag, String msg) {
        log(Log.INFO, tag, msg, null);
    }

    public static void w(Object tag, String msg) {
        log(Log.WARN, tag, msg, null);
    }

    public static void e(Object tag, String msg) {
        log(Log.ERROR, tag, msg, null);
    }

    public static void v(Object tag, Throwable tr) {
        v(tag, "", tr);
    }

    public static void d(Object tag, Throwable tr) {
        d(tag, "", tr);
    }

    public static void i(Object tag, Throwable tr) {
        i(tag, "", tr);
    }

    public static void w(Object tag, Throwable tr) {
        w(tag, "", tr);
    }

    public static void e(Object tag, Throwable tr) {
        e(tag, "", tr);
    }

    public static void v(Object tag, String msg, Throwable tr) {
        log(Log.VERBOSE, tag, msg, tr);
    }

    public static void d(Object tag, String msg, Throwable tr) {
        log(Log.DEBUG, tag, msg, tr);
    }

    public static void i(Object tag, String msg, Throwable tr) {
        log(Log.INFO, tag, msg, tr);
    }

    public static void w(Object tag, String msg, Throwable tr) {
        log(Log.WARN, tag, msg, tr);
    }

    public static void e(Object tag, String msg, Throwable tr) {
        log(Log.ERROR, tag, msg, tr);
    }

    private static void log(int priority, Object tag, String msg, Throwable tr) {
        if (priority < mlogLevel) {
            return;
        }

        if (isBlank(logPath) || isBlank(logFileName) || isBlank(logFileSuffix)) {
            String errorMsg = "Log function is not available because log file info has not been prepared. logPath='" + logPath + "', logFileName='" + logFileName
                    + "' and logFileSuffix='" + logFileSuffix + "'.";
            throw new RuntimeException(errorMsg);
        }

        String tmpTag = "";
        if (tag != null) {
            if (tag instanceof String) {
                tmpTag = (String) tag;
            } else {
                tmpTag = tag.getClass().getSimpleName();
            }
        } else {
            tag = DEFAULT_TAG;
        }

        if (priority <= Log.VERBOSE) {
            Logger.v(tmpTag, msg, tr);
        } else if (priority <= Log.DEBUG) {
            Logger.d(tmpTag, msg, tr);
        } else if (priority <= Log.INFO) {
            Logger.i(tmpTag, msg, tr);
        } else if (priority <= Log.WARN) {
            Logger.w(tmpTag, msg, tr);
        } else {
            Logger.e(tmpTag, msg, tr);
        }
    }

    private static boolean isBlank(String str) {
        return (str == null) || (str.trim().length() == 0);
    }
}
