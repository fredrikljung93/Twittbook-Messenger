/**
 * Activity to compose a new message
 */
package se.kth.anderssonljung.twittbook;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class NewMessageActivity extends Activity {
	EditText to;
	EditText subject;
	EditText messageBody;
	GlobalState global;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_message);
		global = (GlobalState) getApplication();
		to = (EditText) findViewById(R.id.editTextTo);
		subject = (EditText) findViewById(R.id.editTextSubject);
		messageBody = (EditText) findViewById(R.id.editTextMessageBody);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_message, menu);
		return true;
	}

	public void onSendClick(View view) {
		SendMessageTask task = new SendMessageTask(to.getText().toString(),
				global.getUser().getUsername(), subject.getText().toString(),
				messageBody.getText().toString());
		task.execute();
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
 * Sends message to backend
 *
 */
	private class SendMessageTask extends AsyncTask<Void, Void, ResultCode> {
		String to, from, subject, messagebody;

		public SendMessageTask(String to, String from, String subject,
				String messagebody) {
			this.to = to;
			this.from = from;
			this.subject = subject;
			this.messagebody = messagebody;
		}

		@Override
		protected ResultCode doInBackground(Void... params) {
			try {
				HttpClient client = new DefaultHttpClient();

				HttpPost httppost = new HttpPost(
						"http://a.fredrikljung.com:8080/Twittbook/webresources/rest/mobilesendpm");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				nameValuePairs.add(new BasicNameValuePair("receiver", to));
				nameValuePairs.add(new BasicNameValuePair("message",
						messagebody));
				nameValuePairs.add(new BasicNameValuePair("subject", subject));
				nameValuePairs.add(new BasicNameValuePair("sender", from));
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
					System.out.println("Exception message: " + ex.getMessage());
				}
				HttpEntity resEntity = response.getEntity();
				return ResultCode.SUCCESS;
			} catch (UnsupportedEncodingException ex) {
				return ResultCode.ERROR;
			}
		}

		@Override
		protected void onPostExecute(ResultCode result) {
			global.showToast("Message sent");
		}

	}
}
