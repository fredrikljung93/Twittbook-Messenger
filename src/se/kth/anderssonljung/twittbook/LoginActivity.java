/**
 * Activity that shows lets user log in to application.
 * Also handles GCM registration (Google cloud messaging)
 */
package se.kth.anderssonljung.twittbook;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import se.kth.anderssonljung.twittbook.R;
import se.kth.anderssonljung.twittbook.entities.User;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	public static final String EXTRA_MESSAGE = "message";
	static final String PROPERTY_REG_ID = "registration_id";
	static final String PROPERTY_APP_VERSION = "appVersion";
	final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	String SENDER_ID = "186499167994";
	static final String TAG = "GCM";
	GlobalState global;
	Button button;
	EditText usernameEdit;
	EditText passwordEdit;
	GoogleCloudMessaging gcm;
	String regid;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		context = getApplicationContext();
		global = (GlobalState) getApplication();
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			gotoMenuActivityIfAlreadyLoggedIn();

		} else {
			global.showToast("No GCM support");
		}
		button = (Button) findViewById(R.id.loginButton);
		usernameEdit = (EditText) findViewById(R.id.EditTextUsername);
		passwordEdit = (EditText) findViewById(R.id.editTextPassword);
	}
/**
 * Checks whether an user is already logged in. If so, redirect user to MenuActivity
 */
	public void gotoMenuActivityIfAlreadyLoggedIn() {
		SharedPreferences prefs = getSharedPreferences(
				"se.kth.anderssonljung.twittbook", Context.MODE_PRIVATE);
		String username = prefs.getString("username", null);
		Log.d("login oncreate", "username=" + username);
		if (username != null) { // If logged in previously
			global.setUser(global.getDb().getUser(username));
			Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
			startActivity(intent);
		}
	}
/**
 * Tries to log in user with filled in credentials
 * @param view
 */
	public void onLoginClick(View view) {
		if (usernameEdit.getText().toString().isEmpty()
				|| passwordEdit.getText().toString().isEmpty()) {
			return;
		}
		LoginTask task = new LoginTask(usernameEdit.getText().toString(),
				passwordEdit.getText().toString());
		task.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
/**
 * Log in against the backend using REST interface
 *
 */
	private class LoginTask extends AsyncTask<Void, Void, ResultCode> {
		String usernameString;
		String passwordString;
		User user;

		public LoginTask(String username, String password) {
			this.passwordString = password;
			this.usernameString = username;
		}

		@Override
		protected ResultCode doInBackground(Void... params) {
			String jsonString = null;
			BufferedReader in = null;
			try {
				URL url = new URL(
						"http://a.fredrikljung.com:8080/Twittbook/webresources/rest/login?username="
								+ usernameString + "&" + "password="
								+ passwordString);
				in = new BufferedReader(new InputStreamReader(url.openStream()));

				jsonString = in.readLine();
				in.close();
			} catch (MalformedURLException ex) {
				return ResultCode.ERROR;
			} catch (FileNotFoundException ex) { // Wrong credentials
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
			user = gson.fromJson(jsonString, User.class);

			return ResultCode.SUCCESS;
		}

		@Override
		protected void onPostExecute(ResultCode result) {
			if (result == ResultCode.SUCCESS) {
				usernameEdit.setText("");
				passwordEdit.setText("");
				global.setUser(user);
				global.getDb().addUser(user);
				regid = getRegistrationId(context);

				if (regid.isEmpty()) {
					registerInBackground();
				}
				Log.d("LOGIN", "Logged in as " + user.getUsername());
				Intent intent = new Intent(LoginActivity.this,
						MenuActivity.class);
				startActivity(intent);
				SharedPreferences prefs = getSharedPreferences(
						"se.kth.anderssonljung.twittbook", Context.MODE_PRIVATE);
				prefs.edit().putString("username", user.getUsername()).apply();

			} else if (result == ResultCode.FILENOTFOUND) {
				Toast.makeText(getApplicationContext(),
						"Invalid username or password", Toast.LENGTH_SHORT)
						.show();

			} else if (result == ResultCode.NETWORKERROR) {
				Toast.makeText(getApplicationContext(), "Network error",
						Toast.LENGTH_SHORT).show();

			} else {
				Log.d("LOGIN", "Login failed, code " + result);
			}

		}

	}

	/**
	 * Method provided by Google
	 * https://developer.android.com/google/gcm/client.html
	 * 
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 * 
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i("Check play services", "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Method provided by Google
	 * https://developer.android.com/google/gcm/client.html
	 * 
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * Method provided by Google
	 * https://developer.android.com/google/gcm/client.html
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
		return getSharedPreferences("TwittbookGCM", Context.MODE_PRIVATE);
	}

	/** Method provided by Google
	 * https://developer.android.com/google/gcm/client.html
	 * 
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Method provided by Google
	 * https://developer.android.com/google/gcm/client.html
	 * 
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and the app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;

					// You should send the registration ID to your server over
					// HTTP, so it
					// can use GCM/HTTP or CCS to send messages to your app.
					sendRegistrationIdToBackend();

					// For this demo: we don't need to send it because the
					// device will send
					// upstream messages to a server that echo back the message
					// using the
					// 'from' address in the message.

					// Persist the regID - no need to register again.
					storeRegistrationId(context, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Log.d("registerinBackground() onPostExecute", msg);
			}
		}.execute(null, null, null);
	}

	/**
	 * Sends GCM registration id to backend
	 */
	private void sendRegistrationIdToBackend() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {

				try {
					HttpClient client = new DefaultHttpClient();

					HttpPost httppost = new HttpPost(
							"http://a.fredrikljung.com:8080/Twittbook/webresources/rest/registergcm");
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

					nameValuePairs.add(new BasicNameValuePair("username",
							global.getUser().getUsername()));
					nameValuePairs.add(new BasicNameValuePair("regid",
							getRegistrationId(context)));
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
							nameValuePairs, "UTF-8");
					System.out.println("Chosen conten type: "
							+ entity.getContentType());
					httppost.setEntity(entity);

					HttpResponse response = null;
					try {
						response = client.execute(httppost);
					} catch (IOException ex) {
						System.out.println("IOEXCEPTION");
						System.out.println("Exception message: "
								+ ex.getMessage());
					}
					HttpEntity resEntity = response.getEntity();
				} catch (UnsupportedEncodingException ex) {
				}
				return null;

			}
		}.execute(null, null, null);
	}

	/**
	 * Method provided by Google
	 * https://developer.android.com/google/gcm/client.html
	 * 
	 * Stores the registration ID and the app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}
}
