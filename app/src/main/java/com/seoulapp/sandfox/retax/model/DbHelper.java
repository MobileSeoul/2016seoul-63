package com.seoulapp.sandfox.retax.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 2016-10-12.
 */

public class DbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "retaxdb";
    public static final int DB_VERSION = 1;


    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Market.getSql());
        db.execSQL(Refund.getSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

}
