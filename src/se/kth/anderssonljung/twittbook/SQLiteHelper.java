package se.kth.anderssonljung.twittbook;

import se.kth.anderssonljung.twittbook.entities.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

	/** Database model version */
	private static final int DATABASE_VERSION = 1;

	/** Database name */
	private static final String DATABASE_NAME = "TwittbookDB";

	// Table names
	private static final String TABLE_USER = "T_USER";

	// T_USER columns
	private static final String USER_ID = "id";
	private static final String USER_NAME = "name";
	private static final String USER_DESCRIPTION = "description";

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQL statements to create tables
		String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "( "
				+ USER_ID + " TEXT PRIMARY KEY NOT NULL, " + USER_NAME
				+ " TEXT, " + USER_DESCRIPTION + " TEXT)";

		db.execSQL(CREATE_USER_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("SQLiteHelper", "Dropping tables if exists");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		this.onCreate(db);
	}

	public void addUser(User user) {
		Log.d("SQLITEHELPER", "addUser()");
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(USER_ID, user.getId());
		values.put(USER_NAME, user.getUsername());
		values.put(USER_DESCRIPTION, user.getDescription());

		if (getUser(user.getId()) == null) {
			db.insert(TABLE_USER, null, values);
		} else {
			db.update(TABLE_USER, values, USER_ID + "=" + user.getId(), null);
		}

		db.close();
	}

	public User getUser(int userid) {
		Log.d("SQLITEHELPER", "getUser()");
		SQLiteDatabase db = this.getReadableDatabase();

		String q = "SELECT * FROM " + TABLE_USER + " WHERE " + USER_ID + " = "
				+ userid;
		Log.d("SQLiteHelper", q);
		Cursor cursor = db.rawQuery(q, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}

		if (cursor.getCount() == 0) {
			return null;
		}

		User user = new User(cursor.getInt(0), cursor.getString(1),
				cursor.getString(2));
		return user;
	}

}