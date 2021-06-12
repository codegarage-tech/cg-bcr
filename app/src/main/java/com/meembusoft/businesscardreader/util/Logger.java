package com.meembusoft.businesscardreader.util;

import android.util.Log;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class Logger {

    private static LogType logType = LogType.INFO;
    private static boolean isLoggable = false;
    private static boolean isKotlin = false;
    private static String TAG = Logger.class.getSimpleName();

    public enum LogType {
        ERROR, INFO, DEBUG, WARN
    }

    private Logger() {
        throw new RuntimeException("Private constructor cannot be accessed");
    }

    private static void init(Builder builder) {
        Logger.logType = builder.getLogType();
        Logger.TAG = builder.getTag();
        Logger.isLoggable = builder.isIsLoggable();
        Logger.isKotlin = builder.isIsKotlin();
    }

    public static void e(String tag, Object message) {
        String mTag = !isNullOrEmpty(tag) ? TAG + "_" + tag : TAG;
        try {
            if (isLoggable) {
                Log.e(mTag, "|| " + makeLog(message, "e"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(mTag, ex.getMessage());
        }
    }

    public static void e(String tag, Object message, Throwable throwable) {
        String mTag = !isNullOrEmpty(tag) ? TAG + "_" + tag : TAG;
        try {
            if (isLoggable) {
                Log.e(mTag, "|| " + makeLog(message, "e"), throwable);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(mTag, ex.getMessage());
        }
    }

    public static void i(String tag, Object message) {
        String mTag = !isNullOrEmpty(tag) ? TAG + "_" + tag : TAG;
        try {
            if (isLoggable) {
                Log.i(mTag, "|| " + makeLog(message, "i"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(mTag, ex.getMessage());
        }
    }

    public static void w(String tag, Object message) {
        String mTag = !isNullOrEmpty(tag) ? TAG + "_" + tag : TAG;
        try {
            if (isLoggable) {
                Log.w(mTag, "|| " + makeLog(message, "w"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(mTag, ex.getMessage());
        }
    }

    public static void d(String tag, Object message) {
        String mTag = !isNullOrEmpty(tag) ? TAG + "_" + tag : TAG;
        try {
            if (isLoggable)
                Log.d(mTag, "|| " + makeLog(message, "d"));
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(mTag, ex.getMessage());
        }
    }

    public static void log(String tag, Object message) {
        String mTag = !isNullOrEmpty(tag) ? TAG + "_" + tag : TAG;
        try {
            if (isLoggable) {
                String body = "|| " + makeLog(message, "log");
                switch (logType) {
                    case INFO:
                        Log.i(mTag, body);
                        break;
                    case DEBUG:
                        Log.d(mTag, body);
                        break;
                    case ERROR:
                        Log.e(mTag, body);
                        break;
                    case WARN:
                        Log.w(mTag, body);
                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(mTag, ex.getMessage());
        }
    }

    private static String makeLog(Object message, String calledMethodName) {
        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
        int currentIndex = -1;
        for (int i = 0; i < stackTraceElement.length; i++) {
            if (stackTraceElement[i].getMethodName().compareTo(calledMethodName) == 0) {
                currentIndex = ++i;
                break;
            }
        }

        StackTraceElement traceElement = Thread.currentThread().getStackTrace()[currentIndex];
        String fullClassName = traceElement.getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String finalClassName = className.contains("$") ? className.split("\\$")[0] : className;
        String methodName = traceElement.getMethodName();
        int lineNumber = traceElement.getLineNumber();
        String logMessage = message == null ? null : message.toString();
        String postFix = isKotlin ? ".kt:" : ".java:";
        return "(" + finalClassName + postFix + lineNumber + ") || " + logMessage;
    }

    public static class Builder {
        private static LogType logType = LogType.INFO;
        private static boolean isLoggable = true;
        private static boolean isKotlin = false;
        private static String tag = TAG;

        public Builder logType(LogType logType) {
            Builder.logType = logType;
            return this;
        }

        public Builder isLoggable(boolean isLoggable) {
            Builder.isLoggable = isLoggable;
            Builder.isLoggable = true;
            return this;
        }

        public Builder tag(String tag) {
            Builder.tag = tag;
            return this;
        }

        public Builder setIsKotlin(boolean isKotlin) {
            Builder.isKotlin = isKotlin;
            return this;
        }

        public boolean isIsKotlin() {
            return isKotlin;
        }

        public void build() {
            init(this);
        }

        LogType getLogType() {
            return logType;
        }

        boolean isIsLoggable() {
            return isLoggable;
        }

        String getTag() {
            return tag;
        }
    }

    private static boolean isNullOrEmpty(String myString) {
        if (myString == null) {
            return true;
        } else {
            return myString.length() == 0 || myString.equalsIgnoreCase("null") || myString.equalsIgnoreCase("");
        }
    }
}