package com.glsx.glbluetooth.Receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.glsx.glbluetooth.Config;
import com.glsx.glbluetooth.Service.GocsdkService;
import com.glsx.glbluetooth.Service.PlayerService;


public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(Config.JAVA_SDK) {
			Intent service = new Intent(context, GocsdkService.class);
			context.startService(service);
		}

		if(Config.JAVA_PLAYER) {
			Intent playService = new Intent(context, PlayerService.class);
			context.startService(playService);
		}
	}
}
