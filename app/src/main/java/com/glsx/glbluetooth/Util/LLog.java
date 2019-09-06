package com.glsx.glbluetooth.Util;

import android.os.Environment;
import android.util.Log;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;


/**
 * Created by: lixingzhou
 * Created Date: 2017/5/31 11:00
 * Description: 日志工具
 */
public class LLog implements Runnable {
    private static String TAG = "SE";

    private static final int ALL = 0;
    private static final int VERBOSE = 1;
    private static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int WARN = 4;
    private static final int ERROR = 5;

    //日志输出等级：大于此等级的日志才会输出
    private static final int LOGLEVEL = ALL;
    //日志写文件等级：大于此等级的日志才会写入文件
    private static final int WRITELOGLEVEL = VERBOSE;

    //日志写文件开关 true为开,false为关
    private static final boolean LOG_WRITE_TO_FILE = true;
    //日志打印logcat开关  true为开,false为关
    private static boolean LOG_LOGCAT_SWITCH = true;

    private String LOG_PATH_SDCARD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "com.glsx.glcamera" + File.separator;// 日志文件在sdcard中的路径
    private static final String LOG_FILENAME = ".txt";// 输出的日志文件名称

    private static LLog instance = null;
    private boolean isInited = false;//初始化标识
    private boolean isExited = false;//禁用标识
    private BufferedWriter output = null;
    private Queue<String> queue = null;

    private Thread thread = null;

    public static boolean isShow() {
        return LOG_LOGCAT_SWITCH;
    }


    private LLog() {
        queue = new LinkedList<String>();
    }

    public synchronized static LLog getInstance() {
        if (instance == null) {
            instance = new LLog();
        }
        return instance;
    }

    /**
     * 初始化日志工具写文件(写日志必须调用  否则无法写日志到文件)
     *
     * @return 状态标识
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public synchronized boolean init() {
        if (isInited) {
            return true;
        }
        try {
            String needWriteFile = ParseUtil.timeStampToStr(System.currentTimeMillis(), 2);
            File f = new File(LOG_PATH_SDCARD_DIR, needWriteFile + LOG_FILENAME);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            output = new BufferedWriter(new FileWriter(f, true));
            isExited = false;
            startThread();
            isInited = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isInited;
    }

    /**
     * 禁用日志输出
     */
    public synchronized void dispose() {
        if (!isInited) {
            return;
        }
        isExited = true;
        isInited = false;
        synchronized (thread) {
            if (thread.isAlive()) {
                try {
                    thread.notifyAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (output != null) {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            output = null;
        }
        thread = null;
    }


    /**
     * 添加日志到写入队列中
     *
     * @param log 日志内容
     */
    private void add(String log) {
        if (!isInited) {
            return;
        }
        synchronized (queue) {
            queue.add(log);
        }
        synchronized (thread) {
            thread.notifyAll();
        }
    }

    /**
     * 获取队列中的一条日志
     *
     * @return poll() 方法在用空集合调用时不是抛出异常，只是返回 null
     */
    private String get() {
        String result = null;
        synchronized (queue) {
            if (!queue.isEmpty()) {
                result = queue.poll();
            }
        }
        return result;
    }

    private boolean isEmpty() {
        boolean result = true;
        synchronized (queue) {
            result = queue.isEmpty();
        }
        return result;
    }

    @Override
    public void run() {
        while (!isExited) {
            if (isEmpty()) {
                synchronized (thread) {
                    try {
                        thread.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            while (!isEmpty()) {
                String log = get();
                try {
                    output.write(log);
                    output.newLine();
                    output.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startThread() {
        thread = new Thread(this);
        thread.start();
    }

    /**
     * 添加日志
     *
     * @param level     日志等级
     * @param mylogtype 日志等级类型
     * @param tag       日志tag
     * @param text      日志信息
     */
    private static void addLog(int level, final String mylogtype, final String tag, final String text) {
        if (!LOG_WRITE_TO_FILE || (WRITELOGLEVEL >= level)) {
            return;
        }
        StringBuilder needWriteMessage = new StringBuilder().append(ParseUtil.timeStampToStr(System.currentTimeMillis(), 0)).append(" ").append(mylogtype).append(" ").append(tag).append(" ").append(text);
        getInstance().add(needWriteMessage.toString());
        needWriteMessage = null;
    }


    public static void v(String msg) {
        if (VERBOSE > LOGLEVEL) {
            if (LOG_LOGCAT_SWITCH) {
                Log.v(TAG, msg);
            }
            addLog(VERBOSE, "v", TAG, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (VERBOSE > LOGLEVEL) {
            if (LOG_LOGCAT_SWITCH) {
                Log.v(tag, msg);
            }
            addLog(VERBOSE, "v", tag, msg);
        }
    }

    public static void d(String msg) {
        if (DEBUG > LOGLEVEL) {
            if (LOG_LOGCAT_SWITCH) {
                Log.d(TAG, msg);
            }
            addLog(DEBUG, "d", TAG, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG > LOGLEVEL) {
            if (LOG_LOGCAT_SWITCH) {
                Log.d(tag, msg);
            }
            addLog(DEBUG, "d", tag, msg);
        }
    }

    public static void i(String msg) {
        if (INFO > LOGLEVEL) {
            if (LOG_LOGCAT_SWITCH) {
                Log.i(TAG, msg);
            }
            addLog(INFO, "i", TAG, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (INFO > LOGLEVEL) {
            if (LOG_LOGCAT_SWITCH) {
                Log.i(tag, msg);
            }
            addLog(INFO, "i", tag, msg);
        }
    }

    public static void w(String msg) {
        if (WARN > LOGLEVEL) {
            if (LOG_LOGCAT_SWITCH) {
                Log.w(TAG, msg);
            }
            addLog(WARN, "w", TAG, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (WARN > LOGLEVEL) {
            if (LOG_LOGCAT_SWITCH) {
                Log.w(tag, msg);
            }
            addLog(WARN, "w", tag, msg);
        }
    }

    public static void e(String msg) {
        if (ERROR > LOGLEVEL) {
            if (LOG_LOGCAT_SWITCH) {
                Log.e(TAG, msg);
            }
            addLog(ERROR, "e", TAG, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (ERROR > LOGLEVEL) {
            if (LOG_LOGCAT_SWITCH) {
                Log.e(tag, msg);
            }
            addLog(ERROR, "e", tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable r) {
        if (ERROR > LOGLEVEL) {
            if (LOG_LOGCAT_SWITCH) {
                Log.e(tag, msg, r);
            }
            addLog(ERROR, "e", tag, msg);
        }
    }

}
 
 
 
 