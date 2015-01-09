package se.kth.anderssonljung.twittbook.gcm;
import android.app.IntentService;
import android.content.Intent;

// The whole class is based on example provided by Google

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GCMIntentService extends IntentService {
	public GCMIntentService() {
		super("hidden-bond-797");
	}

	protected void onHandleIntent(Intent intent) {

	}
}