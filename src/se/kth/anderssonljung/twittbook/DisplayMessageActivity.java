package se.kth.anderssonljung.twittbook;

import se.kth.anderssonljung.twittbook.entities.Message;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayMessageActivity extends Activity {
	Message message;
	TextView from;
	TextView subject;
	TextView messagebody;
	GlobalState global;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		global = (GlobalState) getApplication();
		int messageid = getIntent().getExtras().getInt("messageid");
		message = global.getDb().getMessage(messageid);
		setContentView(R.layout.activity_display_message);

		from = (TextView) findViewById(R.id.textViewFrom);
		subject = (TextView) findViewById(R.id.TextViewSubject);
		messagebody = (TextView) findViewById(R.id.textViewMessageBody);

		from.setText(message.getSender() + "");
		subject.setText(message.getSubject());
		messagebody.setText(message.getMessage());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_message, menu);
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
}