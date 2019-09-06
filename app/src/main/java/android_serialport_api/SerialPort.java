package android_serialport_api;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class SerialPort {
    private static final String TAG = "SerialPort";
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;
    private BufferedReader mBufferedReader;
    static {
        System.loadLibrary("serial_port");
    }

    public native void close();

    private  native static FileDescriptor open(String path, int a, int b);

    public SerialPort(File device, int baudrate, int flags)
            throws SecurityException, IOException {
        if (!device.canRead() || !device.canWrite()) {
            try {
                Process su;
                su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
                        + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead()
                        || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }
        mFd = open(device.getAbsolutePath(), baudrate, flags);
        if (mFd == null) {
            throw new IOException();
        }
        //mBufferedReader = getBufferedReader(mFd);
        //FileInputStream fis=new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
        mFileInputStream=new FileInputStream(mFd);
        mBufferedReader=new BufferedReader(new InputStreamReader(mFileInputStream));

    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }
    public InputStream getInputStream(){
        return mFileInputStream;
    }
    public BufferedReader getBufferedReader() {
        return mBufferedReader;
    }

    public void closePort() {
        try {
            mBufferedReader.close();
            mFileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        close();
    }

    public BufferedReader getBufferedReader(FileDescriptor file) {
        BufferedReader reader = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream in = new BufferedInputStream(fis);
            in.mark(4);
            byte[] first3bytes = new byte[3];
            in.read(first3bytes);
            in.reset();
            if (first3bytes[0] == (byte) 0xEF && first3bytes[1] == (byte) 0xBB
                    && first3bytes[2] == (byte) 0xBF) {// utf-8
                reader = new BufferedReader(new InputStreamReader(in, "utf-8"));

            } else if (first3bytes[0] == (byte) 0xFF
                    && first3bytes[1] == (byte) 0xFE) {
                reader = new BufferedReader(
                        new InputStreamReader(in, "unicode"));
            } else if (first3bytes[0] == (byte) 0xFE
                    && first3bytes[1] == (byte) 0xFF) {
                reader = new BufferedReader(new InputStreamReader(in,
                        "utf-16be"));
            } else if (first3bytes[0] == (byte) 0xFF
                    && first3bytes[1] == (byte) 0xFF) {
                reader = new BufferedReader(new InputStreamReader(in,
                        "utf-16le"));
            } else {
                reader = new BufferedReader(new InputStreamReader(in, "GBK"));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reader;
    }

}
