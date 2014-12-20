package se.kth.anderssonljung.twittbook;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;

import se.kth.anderssonljung.twittbook.R;
import se.kth.anderssonljung.twittbook.entities.User;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

	GlobalState global;
	Button button;
	EditText usernameEdit;
	EditText passwordEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		global = (GlobalState) getApplication();
		SharedPreferences prefs = getSharedPreferences(
				"se.kth.anderssonljung.twittbook", Context.MODE_PRIVATE);
		int userid = prefs.getInt("userid", -1);
		Log.d("login oncreate", "userid=" + userid);
		if (userid != -1) { // If logged in previously
			global.setUser(global.getDb().getUser(userid));
			Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
			startActivity(intent);
		}

		setContentView(R.layout.activity_login);
		button = (Button) findViewById(R.id.loginButton);
		usernameEdit = (EditText) findViewById(R.id.EditTextUsername);
		passwordEdit = (EditText) findViewById(R.id.editTextPassword);
	}

	public void onLoginClick(View view) {
		if (usernameEdit.getText().toString().isEmpty()
				|| passwordEdit.getText().toString().isEmpty()) {
			return;
		}
		LoginTask task = new LoginTask(usernameEdit.getText().toString(), passwordEdit
				.getText().toString());
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

	public enum resultCode {
		SUCCESS, FILENOTFOUND, NETWORKERROR, ERROR
	}

	private class LoginTask extends AsyncTask<Void, Void, resultCode> {
		String usernameString;
		String passwordString;
		User user;

		public LoginTask(String username, String password) {
			this.passwordString = password;
			this.usernameString = username;
		}

		@Override
		protected resultCode doInBackground(Void... params) {
			String jsonString = null;
			BufferedReader in = null;
			try {
				URL url = new URL(
						"http://a.fredrikljung.com:8080/Twittbook/webresources/rest/login?username="
								+ usernameString + "&" + "password=" + passwordString);
				in = new BufferedReader(new InputStreamReader(url.openStream()));

				jsonString = in.readLine();
				in.close();
			} catch (MalformedURLException ex) {
				return resultCode.ERROR;
			} catch (FileNotFoundException ex) { // Wrong credentials
				return resultCode.FILENOTFOUND;
			} catch (IOException ex) { // Network problems
				return resultCode.NETWORKERROR;
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

			return resultCode.SUCCESS;
		}

		@Override
		protected void onPostExecute(resultCode result) {
			if (result == resultCode.SUCCESS) {
				usernameEdit.setText("");
				passwordEdit.setText("");
				Log.d("LOGIN", "Logged in as " + user.getUsername());
				global.getDb().addUser(user);
				Intent intent = new Intent(LoginActivity.this,
						MenuActivity.class);
				startActivity(intent);
				SharedPreferences prefs = getSharedPreferences(
						"se.kth.anderssonljung.twittbook", Context.MODE_PRIVATE);
				prefs.edit().putInt("userid", user.getId()).apply();

			} else if (result == resultCode.FILENOTFOUND) {
				Toast.makeText(getApplicationContext(),
						"Invalid username or password", Toast.LENGTH_SHORT)
						.show();

			} else if (result == resultCode.NETWORKERROR) {
				Toast.makeText(getApplicationContext(), "Network error",
						Toast.LENGTH_SHORT).show();

			} else {
				Log.d("LOGIN", "Login failed, code " + result);
			}

		}

	}
}
