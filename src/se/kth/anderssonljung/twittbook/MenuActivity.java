package se.kth.anderssonljung.twittbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

	}

	public void onDraftClick(View view) {

	}

	public void onOutboxClick(View view) {

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
				prefs.edit().remove("userid").apply();
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
}
