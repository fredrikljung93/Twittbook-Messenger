package se.kth.anderssonljung.twittbook.gcm;

//The whole class is based on example provided by Google

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("GCMBroadcastReceiver", "onReceive called");
		String data = intent.getExtras().getString("data");
		Log.d("GCMBroadcastReceiver", "data=" + data);
		// Explicitly specify that GcmIntentService will handle the intent.
		ComponentName comp = new ComponentName(context.getPackageName(),
				GCMIntentService.class.getName());
		Log.d("GCMBroadcastReceiver", "comp done");
		// Start the service, keeping the device awake while it is launching.
		startWakefulService(context, (intent.setComponent(comp)));
		Log.d("GCMBroadcastReceiver", "Start wakefulservice done");
		setResultCode(Activity.RESULT_OK);
		Log.d("GCMBroadcastReceiver", "result ok");
	}
}