package com.project.PotHole.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class LoginDataBaseAdapter {
    /*
	 * Handles all Database operations like creation, insertion, recording
	 * updates, delete, close DB and Cursor related stuff
	 */

    static final String DATABASE_NAME = "login.db";
    static final int DATABASE_VERSION = 1;

    // public field for each column in your table
    public static final int NAME_COLUMN = 1;

    // statement to create a new database
    public static final String DATABASE_CREATE = "create table " + "LOGIN"
            + "( " + "ID" + " integer primary key autoincrement," + "USERNAME"
            + " text unique, PASSWORD text);";
    // variable to database instance
    public SQLiteDatabase db;
    // context of the application using the database
    private final Context context;
    // database open or upgrade helper
    private DataBaseHelper dbHelper;

    public LoginDataBaseAdapter(Context context) {

        this.context = context;
        dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public LoginDataBaseAdapter open() throws SQLException {
        // open database
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        // close database
        db.close();
    }

    public SQLiteDatabase getDatabaseInstance() {
        return db;
    }

    public void insertEntry(String userName, String password) {

        // insert entity
        ContentValues newValues = new ContentValues();

        // assign values for each row
        newValues.put("USERNAME", userName);
        newValues.put("PASSWORD", password);

        // insert the row into your table
        db.insert("LOGIN", null, newValues);
        // Toast.makeText(context, "reminder is successfully saved",
        // Toast.LENGTH_LONG).show();
    }

    public int deleteEntry(String username) {

        // string id = string.valueOf(ID);
        String where = "USERNAME=?";
        int numberOfEntriesDeleted = db.delete("LOGIN", where, new String[] {username});
        // Toast.makeText(context, "number for entry deleted successfully : " + numberOfEntriesDeleted,
        // Toast.LENGTH_LONG).show();

        return numberOfEntriesDeleted;
    }

    public String getSingleEntry(String username) {
        Cursor cursor = db.query("LOGIN", null, "USERNAME=?", new String[] {username}, null, null, null);

        // check if name exists
        if(cursor.getCount() < 1) {
            // user name doesn't exist
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String password = cursor.getString(cursor.getColumnIndex("PASSWORD"));
        cursor.close();

        return password;
    }

    public String[] getUsers() {
        Cursor c = db.rawQuery("SELECT USERNAME,PASSWORD FROM LOGIN ", null);
        String[] str = new String[2];
        if(c.moveToFirst()){
            do{
                //assing values
                String column1 = c.getString(0);
                String column2 = c.getString(1);
                str[0] = column1;
                str[1] = column2;
                return str;

            }while(c.moveToNext());
        }
        c.close();
        return null;
    }

    public void updateEntry(String username, String password) {

        // define the updated row content
        ContentValues updatedValues = new ContentValues();

        // assign values for each row
        updatedValues.put("USERNAME", username);
        updatedValues.put("PASSWORD", password);

        String where = "USERNAME = ?";
        db.update("LOGIN", updatedValues, where, new String[] {username});
    }
}
