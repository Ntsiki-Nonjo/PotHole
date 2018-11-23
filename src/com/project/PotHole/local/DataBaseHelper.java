package com.project.PotHole.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {

	/*
	 * Used to create a new DB, when no database exists
	 * in Disk, will help us upgrade version of the DB
	 * if required
	 */

    public DataBaseHelper(Context context, String name, CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
		/*
		 * UNIMPLEMENTED METHOD
		 * called when no database exists in the disk an the helper class
		 * needs to create a new one
		 */
        db.execSQL(LoginDataBaseAdapter.DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*
		 * UNIMPLEMENTED METHOD
		 * called when there is a database version mismatch meaning
		 * that the version of the database on disk needs to be upgraded
		 * to the current version
		 */

        // log the version upgrade
        Log.w("TaskDBAdapter", "Upgrading From Version : " + oldVersion
                + " to " + newVersion + ", This Will Destroy All Data");

		/*
		 * upgrading existing database to new version, multiple versions can
		 * be handled by comparing oldVersion and newVersion values
		 * the simplest case is to drop the old table and create a new one
		 */
        db.execSQL("DROP TABLE IF EXISTS" + "TEMPLATE");
        onCreate(db);
    }

}
