package se.kth.anderssonljung.twittbook;

import se.kth.anderssonljung.twittbook.entities.User;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class GlobalState extends Application {
	private User user;
	private SQLiteHelper db;

	@Override
	public void onCreate() {
		super.onCreate();
		this.db = new SQLiteHelper(this.getApplicationContext());

	}

	public User getUser() {
		if (user == null) {
			SharedPreferences prefs = getSharedPreferences(
					"se.kth.anderssonljung.twittbook", Context.MODE_PRIVATE);
			String userid = prefs.getString("username", null);
			if (userid == null) {
				return null;
			}

			user = db.getUser(userid);
		}
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public SQLiteHelper getDb() {
		return db;
	}

	public void setDb(SQLiteHelper db) {
		this.db = db;
	}

	public void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}

}
