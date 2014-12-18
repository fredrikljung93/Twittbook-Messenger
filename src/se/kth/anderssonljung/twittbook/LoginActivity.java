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
import android.content.Intent;
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
	EditText username;
	EditText password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		button = (Button) findViewById(R.id.loginButton);
		username = (EditText) findViewById(R.id.EditTextUsername);
		password = (EditText) findViewById(R.id.editTextPassword);
		global=(GlobalState) getApplication();
	}

	public void onLoginClick(View view) {
		if (username.getText().toString().isEmpty()
				|| password.getText().toString().isEmpty()) {
			return;
		}
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

	public enum resultCode {
		SUCCESS, FILENOTFOUND, NETWORKERROR, ERROR
	}

	private class LoginTask extends AsyncTask<Void, Void, resultCode> {
		String username;
		String password;
		User user;

		public LoginTask(String username, String password) {
			this.password = password;
			this.username = username;
		}

		@Override
		protected resultCode doInBackground(Void... params) {
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
				Log.d("LOGIN", "Logged in as " + user.getUsername());
				if(global.getDb().getUser(user.getId())==null){
					global.getDb().addUser(user); // Add user to local db if first login
				}
				Intent intent = new Intent(LoginActivity.this,
						MenuActivity.class);
				startActivity(intent);
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
