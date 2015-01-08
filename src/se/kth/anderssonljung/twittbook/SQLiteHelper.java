package se.kth.anderssonljung.twittbook;

import java.util.ArrayList;

import se.kth.anderssonljung.twittbook.entities.Message;
import se.kth.anderssonljung.twittbook.entities.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

	/** Database model version */
	private static final int DATABASE_VERSION = 3;

	/** Database name */
	private static final String DATABASE_NAME = "TwittbookDB";

	// Table names
	private static final String TABLE_USER = "T_USER";
	private static final String TABLE_MESSAGE = "T_MESSAGE";

	// T_USER columns
	private static final String USER_NAME = "name";
	private static final String USER_DESCRIPTION = "description";

	// T_MESSAGE columns
	private static final String MESSAGE_ID = "id";
	private static final String MESSAGE_RECEIVER = "receiver";
	private static final String MESSAGE_SENDER = "sender";
	private static final String MESSAGE_SUBJECT = "subject";
	private static final String MESSAGE_BODY = "body";

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQL statements to create tables
		String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "( "
				+ USER_NAME + " TEXT PRIMARY KEY NOT NULL," + USER_DESCRIPTION
				+ " TEXT)";

		String CREATE_MESSAGE_TABLE = "CREATE TABLE " + TABLE_MESSAGE + "( "
				+ MESSAGE_ID + " TEXT PRIMARY KEY NOT NULL, "
				+ MESSAGE_RECEIVER + " TEXT, " + MESSAGE_SENDER + " TEXT, "
				+ MESSAGE_SUBJECT + " TEXT, " + MESSAGE_BODY + " TEXT)";

		db.execSQL(CREATE_USER_TABLE);
		db.execSQL(CREATE_MESSAGE_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("SQLiteHelper", "Dropping tables if exists");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER + ";");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE + ";");
		this.onCreate(db);
	}

	public void addUser(User user) {
		Log.d("SQLITEHELPER", "addUser()");
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(USER_NAME, user.getUsername());
		values.put(USER_DESCRIPTION, user.getDescription());

		if (getUser(user.getUsername()) == null) {
			db.insert(TABLE_USER, null, values);
		} else {
			db.update(TABLE_USER, values, USER_NAME + "=" + user.getUsername(),
					null);
		}

		db.close();
	}

	public void addMessage(Message m) {
		Log.d("SQLITEHELPER", "addMessage()");
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values;

		values = new ContentValues();
		values.put(MESSAGE_ID, m.getId());
		values.put(MESSAGE_RECEIVER, m.getReceiver());
		values.put(MESSAGE_SENDER, m.getSender());
		values.put(MESSAGE_SUBJECT, m.getSubject());
		values.put(MESSAGE_BODY, m.getMessage());
		db.insert(TABLE_MESSAGE, null, values);

		db.close();
	}

	public User getUser(String username) {
		Log.d("SQLITEHELPER", "getUser()");
		SQLiteDatabase db = this.getReadableDatabase();

		String q = "SELECT * FROM " + TABLE_USER + " WHERE UPPER(" + USER_NAME
				+ ") = UPPER('" + username + "')";
		Log.d("SQLiteHelper", q);
		Cursor cursor = db.rawQuery(q, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}

		if (cursor.getCount() == 0) {
			return null;
		}

		User user = new User(cursor.getString(0), cursor.getString(1));
		return user;
	}

	public Message getMessage(int id) {
		Log.d("SQLITEHELPER", "getMessage() with id " + id);
		SQLiteDatabase db = this.getReadableDatabase();

		String q = "SELECT * FROM " + TABLE_MESSAGE + " WHERE " + MESSAGE_ID
				+ " = " + id;
		Log.d("SQLiteHelper", q);
		Cursor cursor = db.rawQuery(q, null);

		if (cursor != null) {
			cursor.moveToFirst();
			Message message = new Message();
			message.setId(cursor.getInt(0));
			message.setReceiver(cursor.getString(1));
			message.setSender(cursor.getString(2));
			message.setSubject(cursor.getString(3));
			message.setMessage(cursor.getString(4));
			return message;
		} else {
			Log.d("getMessage()", "IS RETURNING NULL!");
			return null;
		}

	}

	public ArrayList<Message> getInbox(String receiver) {
		Log.d("SQLITEHELPER", "getMessage()");
		SQLiteDatabase db = this.getReadableDatabase();

		String q = "SELECT * FROM " + TABLE_MESSAGE + " WHERE UPPER("
				+ MESSAGE_RECEIVER + ") = UPPER('" + receiver + "')";
		Log.d("SQLiteHelper", q);
		Cursor cursor = db.rawQuery(q, null);
		ArrayList<Message> messages = new ArrayList<Message>();

		if (cursor.moveToFirst()) {
			do {
				Message message = new Message();
				message.setId(cursor.getInt(0));
				message.setReceiver(cursor.getString(1));
				message.setSender(cursor.getString(2));
				message.setSubject(cursor.getString(3));
				message.setMessage(cursor.getString(4));
				messages.add(message);
			} while (cursor.moveToNext());
		}

		return messages;
	}

	public ArrayList<Message> getOutbox(String sender) {
		Log.d("SQLITEHELPER", "getMessage()");
		SQLiteDatabase db = this.getReadableDatabase();

		String q = "SELECT * FROM " + TABLE_MESSAGE + " WHERE UPPER("
				+ MESSAGE_SENDER + ") = UPPER('" + sender + "')";
		Log.d("SQLiteHelper", q);
		Cursor cursor = db.rawQuery(q, null);
		ArrayList<Message> messages = new ArrayList<Message>();

		if (cursor.moveToFirst()) {
			do {
				Message message = new Message();
				message.setId(cursor.getInt(0));
				message.setReceiver(cursor.getString(1));
				message.setSender(cursor.getString(2));
				message.setSubject(cursor.getString(3));
				message.setMessage(cursor.getString(4));
				messages.add(message);
			} while (cursor.moveToNext());
		}

		return messages;
	}

	public int getBiggestMessageId() {
		Log.d("SQLITEHELPER", "getBiggestMessageId()");
		SQLiteDatabase db = this.getReadableDatabase();

		String q = "SELECT MAX(" + MESSAGE_ID + ") FROM " + TABLE_MESSAGE;
		Log.d("SQLiteHelper", q);
		Cursor cursor = db.rawQuery(q, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			return cursor.getInt(0);
		} else {
			return -1;
		}

	}

}