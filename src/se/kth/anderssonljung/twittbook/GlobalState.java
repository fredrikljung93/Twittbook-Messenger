package se.kth.anderssonljung.twittbook;

import se.kth.anderssonljung.twittbook.entities.User;
import android.app.Application;

public class GlobalState extends Application {
	private User user;
	private SQLiteHelper db;

	@Override
	public void onCreate() {
		super.onCreate();
		this.db = new SQLiteHelper(this.getApplicationContext());

	}

	public User getUser() {
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

}
