package se.kth.anderssonljung.twittbook.gcm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;

import se.kth.anderssonljung.twittbook.GlobalState;
import se.kth.anderssonljung.twittbook.InboxActivity;
import se.kth.anderssonljung.twittbook.R;
import se.kth.anderssonljung.twittbook.ResultCode;
import se.kth.anderssonljung.twittbook.entities.Message;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
		downloadMessagesAndNotifyUser(data, context, intent);
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
				Intent intent = new Intent(global, InboxActivity.class);
				PendingIntent pi = PendingIntent.getActivity(global, 0, intent,
						0);
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
				mBuilder.setSmallIcon(R.drawable.ic_launcher);
				mBuilder.setAutoCancel(true);
				mBuilder.setContentIntent(pi);

				mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
			}
		}

	}
}