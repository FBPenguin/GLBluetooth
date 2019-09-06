package com.glsx.glbluetooth;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import com.glsx.glbluetooth.Activity.CallActivity;
import com.glsx.glbluetooth.Activity.IncomingActivity;
import com.glsx.glbluetooth.RemoteService.GocsdkCallbackImpl;
import com.glsx.glbluetooth.RemoteService.GocsdkServiceImpl;
import com.glsx.glbluetooth.Service.GocsdkService;
import com.glsx.glbluetooth.Service.PlayerService;
import com.glsx.glbluetooth.Util.LLog;
import com.glsx.glbluetooth.Fragment.FragmentCallPhone;
import com.glsx.glbluetooth.Fragment.FragmentCallog;
import com.glsx.glbluetooth.Fragment.FragmentMusic;
import com.glsx.glbluetooth.Fragment.FragmentPairedList;
import com.glsx.glbluetooth.Fragment.FragmentPhonebookList;
import com.glsx.glbluetooth.Fragment.FragmentSearch;
import com.glsx.glbluetooth.Fragment.FragmentSetting;
import com.glsx.glbluetooth.View.MyFragmentTabHost;
import com.goodocom.gocsdk.IGocsdkCallback;
import com.goodocom.gocsdk.IGocsdkService;

public class MainActivity extends AppCompatActivity {

    public static final int MSG_COMING = 4;
    public static final int MSG_OUTGING = 5;
    public static final int MSG_TALKING = 6;
    public static final int MSG_HANGUP = 7;
    public static final int MSG_DEVICENAME = 11;
    public static final int MSG_DEVICEPINCODE = 12;
    public static final int MSG_UPDATE_PHONEBOOK = 17;
    public static final int MSG_UPDATE_PHONEBOOK_DONE = 18;
    public static final int MSG_SET_MICPHONE_ON = 19;
    public static final int MSG_SET_MICPHONE_OFF = 20;
    public static final int MSG_UPDATE_INCOMING_CALLLOG = 25;
    public static final int MSG_UPDATE_CALLOUT_CALLLOG = 26;
    public static final int MSG_UPDATE_MISSED_CALLLOG = 27;
    public static final int MSG_UPDATE_CALLLOG_DONE = 28;
    public static final int MSG_CURRENT_CONNECT_DEVICE_NAME = 29;

    public static String mLocalName = null;
    public static String mPinCode = null;
    public static String currentDeviceName = "";

    private static MyFragmentTabHost tabhost;

    private static IGocsdkCallback gocsdkCallback;
    private static IGocsdkService gocsdkService;
    private boolean isBind = false;
    private MyConn conn;
    private String TAG = MainActivity.class.getSimpleName();

    public static final int FIREST_POSITION = 2;
    private int mImageID[] = { R.drawable.btn_calllog_selector, R.drawable.btn_contact_selector,
            /*R.drawable.btn_message_selector,8*/R.drawable.btn_jianpan_selector, R.drawable.btn_music_selector,
            R.drawable.btn_btinfo_selector, R.drawable.btn_btpairlist_selector, R.drawable.btn_setting_selector };
    private Class[] mFragment = { FragmentCallog.class, FragmentPhonebookList.class,
            /*FragmentMessage.class,*/ FragmentCallPhone.class, FragmentMusic.class,
            FragmentSearch.class, FragmentPairedList.class, FragmentSetting.class };

    private String[] mString = new String[] { "通话记录", "通讯录",/* "短信信息",*/ "拨号盘", "蓝牙音乐", "蓝牙信息", "蓝牙配对列表", "设置" };

    private static Handler hand = null;

    public static Handler getHandler() {
        return hand;
    }

    public static IGocsdkService getService() {
        return gocsdkService;
    }

    public static MyFragmentTabHost getTabHost() {
        return tabhost;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            gocsdkService = GocsdkServiceImpl.Stub.asInterface(iBinder);
            isBind = true;
            try {
                gocsdkService.registerCallback(gocsdkCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            LLog.d(TAG,"ServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LLog.d(TAG,"Service is disconnected");
            isBind = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, GocsdkService.class);
        conn = new MyConn();
        startService(intent);
        if(!isBind) {
            bindService(intent, conn, BIND_AUTO_CREATE);
        }

        if(Config.JAVA_PLAYER) {
            Intent playerService = new Intent(this, PlayerService.class);
            startService(playerService);
        }

        initView();
        tabhost.setCurrentTab(FIREST_POSITION);
        hand = handler;
        gocsdkCallback = new GocsdkCallbackImpl();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            gocsdkService.unregisterCallback(gocsdkCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(serviceConnection);
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_HANGUP:
                    break;
                case MSG_COMING: {// 来电
                    String number = (String) msg.obj;
                    IncomingActivity.start(MainActivity.this, HfpStatus.INCOMING, number);
                    break;
                }
                case MSG_TALKING: {// 通话中
                    String number = (String) msg.obj;
                    if ((!IncomingActivity.running) && (!CallActivity.running)) {
                        CallActivity.start(MainActivity.this, HfpStatus.TALKING, number);
                    }
                    break;
                }
                case MSG_OUTGING:// 拨出
                    String number = (String) msg.obj;
                    if ((!IncomingActivity.running) && (!CallActivity.running)) {
                        CallActivity.start(MainActivity.this, HfpStatus.CALLING, number);
                    }
                    break;
                case MSG_DEVICENAME:// 蓝牙设备名称
                    String name = (String) msg.obj;
                    mLocalName = name;
                    break;
                case MSG_DEVICEPINCODE:// 蓝牙设备的PIN码
                    String pincode = (String) msg.obj;
                    mPinCode = pincode;
                    break;
                case MSG_CURRENT_CONNECT_DEVICE_NAME:
                    currentDeviceName = (String) msg.obj;
                    break;
            }
        };
    };

    private void initView() {
        tabhost = (MyFragmentTabHost) findViewById(R.id.tabhost);
        tabhost.setup(MainActivity.this, getSupportFragmentManager(),R.id.fl_content_show);
        for (int i = 0; i < mImageID.length; i++) {
            TabHost.TabSpec tabSpec = tabhost.newTabSpec(mString[i]).setIndicator(getTabItemView(i));
            tabhost.addTab(tabSpec, mFragment[i], null);
        }
    }

    private View getTabItemView(int index) {
        View view = View.inflate(this, R.layout.tabhost_item_view, null);
        ImageView iv_flg = (ImageView) view.findViewById(R.id.iv_flg);
        iv_flg.setImageResource(mImageID[index]);
        return view;
    }

    private class MyConn implements ServiceConnection {
        // 绑定成功之后 会调用该方法
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("app", "onServiceConnected");
            gocsdkService = IGocsdkService.Stub.asInterface(service);
            try {
                gocsdkService.registerCallback(gocsdkCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
//                        gocsdkService.openBlueTooth();
                        gocsdkService.inqueryHfpStatus();
                        gocsdkService.inqueryA2dpStatus();
                        gocsdkService.musicUnmute();
                        gocsdkService.getLocalName();
                        gocsdkService.getPinCode();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }, 500);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
