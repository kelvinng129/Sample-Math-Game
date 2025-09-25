package com.mobile.game;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database name and version
    private static final String DATABASE_NAME = "games.db";
    private static final int DATABASE_VERSION = 1;

    // Table name and columns
    public static final String TABLE_GAMES_LOG = "GamesLog";
    public static final String COLUMN_GAME_ID = "gameID";
    public static final String COLUMN_PLAY_DATE = "playDate";
    public static final String COLUMN_PLAY_TIME = "playTime";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_CORRECT_COUNT = "correctCount";

    // SQL to create the table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_GAMES_LOG + " (" +
                    COLUMN_GAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PLAY_DATE + " TEXT, " +
                    COLUMN_PLAY_TIME + " TEXT, " +
                    COLUMN_DURATION + " INTEGER, " +
                    COLUMN_CORRECT_COUNT + " INTEGER" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES_LOG);
        onCreate(db);
    }

    // Insert a new game log
    public void insertGameLog(String playDate, String playTime, int duration, int correctCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLAY_DATE, playDate);
        values.put(COLUMN_PLAY_TIME, playTime);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_CORRECT_COUNT, correctCount);
        db.insert(TABLE_GAMES_LOG, null, values);
        db.close();
    }

    // Retrieve all game logs
    public Cursor getAllGameLogs() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_GAMES_LOG, null, null, null, null, null, null);
    }
}