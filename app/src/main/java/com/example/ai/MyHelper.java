package com.example.ai;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyHelper extends SQLiteOpenHelper {
    public MyHelper(Context context) {
        super(context, "user.db", null, 2);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users(username VARCHAR(100) PRIMARY KEY, password VARCHAR(100))");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists information");
        onCreate(db);
    }
}

