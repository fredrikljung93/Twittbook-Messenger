package se.kth.anderssonljung.twittbook.gcm;

//The whole class is based on example provided by Google

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import se.kth.anderssonljung.twittbook.GlobalState;
import se.kth.anderssonljung.twittbook.ResultCode;
import se.kth.anderssonljung.twittbook.entities.Message;
import android.R;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
		downloadMessagesAndNotifyUser(data, context, intent);
	}

	private void sendNotification(String msg, Context context, Intent intent) {
		GlobalState global = (GlobalState) context.getApplicationContext();
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

	private void downloadMessagesAndNotifyUser(String msg, Context context,
			Intent intent) {
		SyncInboxOutbox task = new SyncInboxOutbox(
				(GlobalState) context.getApplicationContext(), msg);
		task.execute();

	}

	private class SyncInboxOutbox extends AsyncTask<Void, Void, ResultCode> {
		GlobalState global;
		String msg;

		public SyncInboxOutbox(GlobalState global, String msg) {
			this.global = global;
			this.msg = msg;
		}

		@Override
		protected ResultCode doInBackground(Void... params) {
			String jsonString = null;
			BufferedReader in = null;
			try {
				int minid = global.getDb().getBiggestMessageId();
				URL url = new URL(
						"http://a.fredrikljung.com:8080/Twittbook/webresources/rest/allmessages?userId="
								+ global.getUser().getUsername()
								+ "&minId="
								+ minid);
				Log.d("Sync url", url.toString());
				in = new BufferedReader(new InputStreamReader(url.openStream()));

				jsonString = in.readLine();
				in.close();
			} catch (MalformedURLException ex) {
				return ResultCode.ERROR;
			} catch (FileNotFoundException ex) {
				return ResultCode.FILENOTFOUND;
			} catch (IOException ex) { // Network problems
				return ResultCode.NETWORKERROR;
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			Gson gson = new Gson();
			Message[] messageArray = gson.fromJson(jsonString, Message[].class);
			for (Message m : messageArray) {
				global.getDb().addMessage(m);
			}

			return ResultCode.SUCCESS;
		}

		@Override
		protected void onPostExecute(ResultCode result) {
			if (result == ResultCode.SUCCESS) {
				Vibrator v = (Vibrator) global.getApplicationContext()
						.getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(new long[] { 100, 100, 100, 100 }, -1);
				Log.d("GCMService", "sendNotification");
				mNotificationManager = (NotificationManager) global
						.getApplicationContext().getSystemService(
								Context.NOTIFICATION_SERVICE);
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						global.getApplicationContext()).setSmallIcon(0)
						.setContentTitle("Twittbook").setContentText(msg);
				mBuilder.setDefaults(Notification.DEFAULT_SOUND);
				mBuilder.setSmallIcon(R.drawable.ic_media_ff);
				mBuilder.setAutoCancel(true);
				mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
			}
		}

	}
}