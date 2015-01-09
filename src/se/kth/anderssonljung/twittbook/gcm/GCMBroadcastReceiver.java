package se.kth.anderssonljung.twittbook.gcm;

//The whole class is based on example provided by Google

import android.R;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

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
		sendNotification(data, context, intent);
	}

	private void sendNotification(String msg, Context context, Intent intent) {
		Vibrator v = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(new long[] { 100, 100, 100, 100 }, -1);
		Log.d("GCMService", "sendNotification");
		mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(0).setContentTitle("Twittbook")
				.setContentText(msg);
		mBuilder.setDefaults(Notification.DEFAULT_SOUND);
		mBuilder.setSmallIcon(R.drawable.ic_media_ff);
		mBuilder.setAutoCancel(true);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
}