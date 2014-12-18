package se.kth.anderssonljung.twittbook;

import java.util.ArrayList;

import se.kth.anderssonljung.twittbook.entities.Message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class InboxActivity extends Activity {

	ListView listview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox);
		this.listview = (ListView) findViewById(R.id.listView1);
		ArrayList<Message> messages = new ArrayList<Message>();
		for (int i = 1; i <= 20; i++) {
			messages.add(new Message(i, 1, i, "Message body with id " + i,
					"Subject " + i));
		}
		ArrayAdapter<Message> adapter = new ArrayAdapter<Message>(this, R.layout.messagelistitem, messages);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(InboxActivity.this, DisplayMessageActivity.class);
				startActivity(intent);
				
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.inbox, menu);
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
