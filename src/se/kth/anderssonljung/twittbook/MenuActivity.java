/**
 * Activity with a menu for an already signed in user
 */
package se.kth.anderssonljung.twittbook;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import se.kth.anderssonljung.twittbook.entities.Message;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MenuActivity extends Activity {
	GlobalState global;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		global = (GlobalState) getApplication();
	}

	public void onInboxClick(View view) {
		Intent intent = new Intent(MenuActivity.this, InboxActivity.class);
		startActivity(intent);
	}

	public void onNewMessageClick(View view) {
		Intent intent = new Intent(MenuActivity.this, NewMessageActivity.class);
		startActivity(intent);
	}

	public void onOutboxClick(View view) {
		Intent intent = new Intent(MenuActivity.this, OutboxActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
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
		if (id == R.id.action_updateinbox) {
			global.showToast("Sync");
			SyncInboxOutbox task = new SyncInboxOutbox();
			task.execute();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		logout();
	}

	private void logout() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Do you want to log out?");
		builder.setTitle("Log out?");
		builder.setCancelable(true);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				SharedPreferences prefs = getSharedPreferences(
						"se.kth.anderssonljung.twittbook", Context.MODE_PRIVATE);
				prefs.edit().remove("username").apply();
				global.setUser(null);
				finish();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();

	}

	private class SyncInboxOutbox extends AsyncTask<Void, Void, ResultCode> {
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
		}

	}
}
