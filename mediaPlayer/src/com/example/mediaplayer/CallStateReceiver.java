package com.example.mediaplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CallStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	    // TODO Auto-generated method stub
		Log.d("Debug", "I'm Here!");
		Log.d("Debug", android.telephony.TelephonyManager.EXTRA_STATE);
		Log.d("Debug", intent.getStringExtra(android.telephony.TelephonyManager.EXTRA_STATE));
	}

}
