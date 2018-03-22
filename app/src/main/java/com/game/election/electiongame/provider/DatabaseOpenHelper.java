package com.game.election.electiongame.provider;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "election_game";
    private static final int DATABASE_VERSION = 1;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Contract.State.CREATE_TABLE);
        db.execSQL(Contract.Region.CREATE_TABLE);
        db.execSQL(Contract.RegionNeighborhood.CREATE_TABLE);
        db.execSQL(Contract.Problems.CREATE_TABLE);
        db.execSQL(Contract.Candidate.CREATE_TABLE);
        db.execSQL(Contract.SavedGame.CREATE_TABLE);
        db.execSQL(Contract.SavedCandidate.CREATE_TABLE);
        db.execSQL(Contract.SavedRegion.CREATE_TABLE);
        db.execSQL(Contract.SavedHistory.CREATE_TABLE);
        db.execSQL(Contract.RegionOfSavedHistory.CREATE_TABLE);
        db.execSQL(Contract.Settings.CREATE_TABLE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("Pragma foreign_keys='on'");

    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Contract.Settings.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.SavedHistory.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.SavedRegion.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.SavedGame.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.SavedCandidate.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.Candidate.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.Problems.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.RegionNeighborhood.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.Region.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.State.TABLE_NAME);
        onCreate(db);
    }

}
