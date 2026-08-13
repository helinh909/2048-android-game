package com.example.a2048_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "highscores.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_SCORES = "scores";
    private static final String TABLE_STATS = "statistics";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SCORE = "score";

    private static final String COLUMN_PLAYED_GAME = "games_played";
    private static final String COLUMN_GAME_WON = "games_won";

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    //create the table and the column in our database
    public void onCreate(SQLiteDatabase db) {
        String createScoreTable = "CREATE TABLE " + TABLE_SCORES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SCORE + " INTEGER)";
        db.execSQL(createScoreTable);

        String createStatTable = "CREATE TABLE " + TABLE_STATS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PLAYED_GAME + " INTEGER, " +
                COLUMN_GAME_WON + " INTEGER)";
        db.execSQL(createStatTable);

        //For insuring that there is always a values in this table
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, 1);
        db.insert(TABLE_STATS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_STATS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_PLAYED_GAME + " INTEGER DEFAULT 0, " +
                    COLUMN_GAME_WON + " INTEGER DEFAULT 0)");

            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, 1);
            db.insertWithOnConflict(TABLE_STATS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        }
    }


    public void incrementGamesPlayed() {
        SQLiteDatabase db = this.getWritableDatabase();

        //Verify if the line exist
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_STATS + " WHERE " + COLUMN_ID + " = 1", null);
        if (cursor.moveToFirst() && cursor.getInt(0) == 0) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, 1);
            values.put(COLUMN_PLAYED_GAME, 0);
            values.put(COLUMN_GAME_WON, 0);
            db.insert(TABLE_STATS, null, values);
        }
        cursor.close();
        //we add one to the game played
        db.execSQL("UPDATE " + TABLE_STATS + " SET " + COLUMN_PLAYED_GAME + " = " + COLUMN_PLAYED_GAME + " + 1 WHERE " + COLUMN_ID + " = 1");

        db.close();
    }

    public void decreaseGamesPlayed() {
        SQLiteDatabase db = this.getWritableDatabase();

        //Verify if the line exist
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_STATS + " WHERE " + COLUMN_ID + " = 1", null);
        if (cursor.moveToFirst() && cursor.getInt(0) == 0) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, 1);
            values.put(COLUMN_PLAYED_GAME, 0);
            values.put(COLUMN_GAME_WON, 0);
            db.insert(TABLE_STATS, null, values);
        }
        cursor.close();

        //decrease one to the game played column
        db.execSQL("UPDATE " + TABLE_STATS + " SET " + COLUMN_PLAYED_GAME + " = " + COLUMN_PLAYED_GAME + " - 1 WHERE " + COLUMN_ID + " = 1");

        db.close();
    }


    public void incrementGamesWon() {
        SQLiteDatabase db = this.getWritableDatabase();

        //Verify if the line exist
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_STATS + " WHERE " + COLUMN_ID + " = 1", null);
        if (cursor.moveToFirst() && cursor.getInt(0) == 0) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, 1);
            values.put(COLUMN_PLAYED_GAME, 0);
            values.put(COLUMN_GAME_WON, 0);
            db.insert(TABLE_STATS, null, values);
        }
        cursor.close();

        //add one to the game won column
        db.execSQL("UPDATE " + TABLE_STATS + " SET " + COLUMN_GAME_WON + " = " + COLUMN_GAME_WON + " + 1");

        db.close();
    }

    //select the number of played gamed
    public int getPlayedGame() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PLAYED_GAME + " FROM " + TABLE_STATS + " WHERE " + COLUMN_ID + "=1", null);
       int gamesPlayed = 0;

        if (cursor.moveToFirst()) {
            gamesPlayed = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return gamesPlayed;
    }

    //select the number of game won
    public int getGameWon() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_GAME_WON + " FROM " + TABLE_STATS + " WHERE " + COLUMN_ID + "=1", null);
        int gamesWon = 0;

        if (cursor.moveToFirst()) {
            gamesWon = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return gamesWon;
    }

    //Add a new score in the column of score
    public void addScore(int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCORE, score);
        db.insert(TABLE_SCORES, null, values);
        db.close();
    }

    //gey the top 10 score
    public List<Integer> getTopScores() {
        List<Integer> scores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_SCORE + " FROM " + TABLE_SCORES +
                " ORDER BY " + COLUMN_SCORE + " DESC LIMIT 10", null);

        if (cursor.moveToFirst()) {
            do {
                scores.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return scores;
    }

    //get the number one score
    public int getTheTopScore() {
        int bestScore = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_SCORE + " FROM " + TABLE_SCORES +
                " ORDER BY " + COLUMN_SCORE + " DESC LIMIT 1", null);

        if (cursor.moveToFirst()) {
            bestScore = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return bestScore;
    }

    //reset all the score
    public void resetScores() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_SCORES);
        db.close();
    }


    //if you don't have the first version of the database execute this line for exampke in Game2048
    public void resetAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_STATS);
        db.execSQL("DELETE FROM " + TABLE_SCORES);

        // Réinsérer la ligne par défaut dans statistics
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, 1);
        values.put(COLUMN_PLAYED_GAME, 0);
        values.put(COLUMN_GAME_WON, 0);
        db.insert(TABLE_STATS, null, values);

        db.close();
    }
}
