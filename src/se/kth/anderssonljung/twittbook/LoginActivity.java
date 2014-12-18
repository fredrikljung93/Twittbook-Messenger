package se.kth.anderssonljung.twittbook;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;

import se.kth.anderssonljung.twittbook.R;
import se.kth.anderssonljung.twittbook.entities.User;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {

	Button button;
	EditText username;
	EditText password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		button = (Button) findViewById(R.id.loginButton);
		username = (EditText) findViewById(R.id.EditTextUsername);
		password = (EditText) findViewById(R.id.editTextPassword);
	}

	public void onLoginClick(View view) {
		LoginTask task = new LoginTask(username.getText().toString(), password
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

	private class LoginTask extends AsyncTask<Void, Void, String> {
		String username;
		String password;
		User user;

		public LoginTask(String username, String password) {
			this.password = password;
			this.username = username;
		}

		@Override
		protected String doInBackground(Void... params) {
			String jsonString = null;
			BufferedReader in = null;
			try {
				URL url = new URL(
						"http://a.fredrikljung.com:8080/Twittbook/webresources/rest/login?username="
								+ username + "&" + "password=" + password);
				in = new BufferedReader(new InputStreamReader(url.openStream()));

				jsonString = in.readLine();
				in.close();
			} catch (MalformedURLException ex) {
				return "error1";
			} catch (FileNotFoundException ex) { // Wrong credentials
				return "error2";
			}
			catch (IOException ex) { // Network problems
				return "error3";
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

			return "success";
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equalsIgnoreCase("success")) {
				if (user != null) {
					Log.d("LOGIN", "Logged in as " + user.getUsername());
					Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
					startActivity(intent);
				} else {
					Log.d("LOGIN", "code succes, but no user");
				}
			} else {
				Log.d("LOGIN", "Login failed, code " + result);
			}

		}

	}
}
