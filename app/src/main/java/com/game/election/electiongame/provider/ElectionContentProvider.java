package com.game.election.electiongame.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

public class ElectionContentProvider extends android.content.ContentProvider {

    private DatabaseOpenHelper databaseHelper;

    @Override
    public boolean onCreate() {
        databaseHelper = new DatabaseOpenHelper(getContext());
        return true;
    }

    private Bundle returnInformationsForStateByStateName(String arg) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String select = "SELECT "
                + (Contract.State.TABLE_NAME + "." + Contract.State.POPULATION)
                + " FROM " + Contract.State.TABLE_NAME
                + " WHERE " + (Contract.State.TABLE_NAME + "." + Contract.State.STATE_NAME) + "='" + arg + "';";
        Cursor c = db.rawQuery(select, null);
        if (c != null && c.getCount() > 0) {
            Bundle obj = new Bundle();
            c.moveToFirst();
            obj.putLong("population",c.getLong(0));
            select = "SELECT "
                    + (Contract.Region.TABLE_NAME + "." + Contract.Region._ID)
                    + " FROM " + Contract.State.TABLE_NAME
                    + " INNER JOIN " + Contract.Region.TABLE_NAME
                    + " ON " + (Contract.Region.TABLE_NAME + "." + Contract.Region.ID_STATE) + "=" + (Contract.State.TABLE_NAME + "." + Contract.State._ID)
                    + " WHERE " + (Contract.State.TABLE_NAME + "." + Contract.State.STATE_NAME) + "='" + arg + "';";
            c = db.rawQuery(select, null);
            if (c != null && c.getCount() > 0) {
                obj.putInt("regionCount",c.getCount());
                return obj;
            }
            c.close();
            return null;
        }
        else
            return null;
    }

    private Bundle returnSavedCharacterByIdCharacter(String arg) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String select = "SELECT "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.NAME_SURNAME) + ", "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.CHARISMA) + ", "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.REPUTATION) + ", "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.MONEY) + ", "
                + (Contract.Region.TABLE_NAME + "." + Contract.Region.REGION_NAME)
                + " FROM " + Contract.Candidate.TABLE_NAME
                + " INNER JOIN " + Contract.Region.TABLE_NAME
                + " ON " + (Contract.Region.TABLE_NAME + "." + Contract.Region._ID) + "=" + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.ID_BIRTH_REGION)
                + " WHERE " + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate._ID) + "='" + arg + "';";
        Cursor c = db.rawQuery(select,null);
        if (c != null && c.getCount() > 0) {
            Bundle obj = new Bundle();
            c.moveToFirst();
            obj.putString("nameSurname",c.getString(0));
            obj.putInt("charisma",c.getInt(1));
            obj.putInt("reputation",c.getInt(2));
            obj.putInt("money",c.getInt(3));
            obj.putString("regionName",c.getString(4));
            c.close();
            return obj;
        }
        else
            return null;
    }

    private Bundle returnCountOfCharactersInStateByStateName(String arg) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String select = "SELECT "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate._ID)
                + " FROM " + Contract.State.TABLE_NAME
                + " INNER JOIN " + Contract.Region.TABLE_NAME
                + " ON " + (Contract.Region.TABLE_NAME + "." + Contract.Region.ID_STATE) + "=" + (Contract.State.TABLE_NAME + "." + Contract.State._ID)
                + " INNER JOIN " + Contract.Candidate.TABLE_NAME
                + " ON " + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.ID_BIRTH_REGION) + "=" + (Contract.Region.TABLE_NAME + "." + Contract.Region._ID)
                + " WHERE " + (Contract.State.TABLE_NAME + "." + Contract.State.STATE_NAME) + "='" + arg + "';";
        Cursor c = db.rawQuery(select,null);
        if (c != null && c.getCount() > 0) {
            Bundle obj = new Bundle();
            obj.putInt("count",c.getCount());
            c.close();
            return  obj;
        }
        else
            return null;
    }

    private Bundle returnCharacterByIdCharacter(String arg) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String select = "SELECT "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate._ID) + " AS character_id, "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.NAME_SURNAME) + ", "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.ID_BIRTH_REGION) + ", "
                + (Contract.Region.TABLE_NAME + "." + Contract.Region.REGION_NAME) + ", "
                + (Contract.Region.TABLE_NAME + "." + Contract.Region.ID_STATE) + ", "
                + (Contract.State.TABLE_NAME + "." + Contract.State.STATE_NAME) + ", "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.CHARISMA) + ", "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.REPUTATION) + ", "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.MONEY) + ", "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.CREATED_BY_USER) + ", "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.CHANGED)
                + " FROM " + Contract.State.TABLE_NAME
                + " INNER JOIN " + Contract.Region.TABLE_NAME
                + " ON " + (Contract.Region.TABLE_NAME + "." + Contract.Region.ID_STATE) + "=" + (Contract.State.TABLE_NAME + "." + Contract.State._ID)
                + " INNER JOIN " + Contract.Candidate.TABLE_NAME
                + " ON " + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.ID_BIRTH_REGION) + "=" + (Contract.Region.TABLE_NAME + "." + Contract.Region._ID)
                + " WHERE " + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate._ID) + "='" + arg + "';";
        Cursor c = db.rawQuery(select,null);
        if (c != null && c.getCount() > 0) {
            Bundle obj = new Bundle();
            c.moveToFirst();
            obj.putInt("id",c.getInt(0));
            obj.putString("nameSurname",c.getString(1));
            obj.putInt("idBirthRegion",c.getInt(2));
            obj.putString("birthRegionName",c.getString(3));
            obj.putInt("idBirthState",c.getInt(4));
            obj.putString("birthStateName",c.getString(5));
            obj.putInt("charisma",c.getInt(6));
            obj.putInt("reputation",c.getInt(7));
            obj.putInt("money",c.getInt(8));
            obj.putInt("createdByUser",c.getInt(9));
            obj.putLong("changed",c.getLong(10));
            c.close();
            return  obj;
        }
        else
            return null;
    }

    private Bundle returnSavedGameByIdSavedGame(String arg) {
        Bundle obj = new Bundle();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        //zakladne info ako tyzden, kto je na tahu atd
        String select = "SELECT "
                + (Contract.SavedGame.TABLE_NAME + "." + Contract.SavedGame.ACTUAL_ELECTION_WEEK) + ", "
                + (Contract.SavedGame.TABLE_NAME + "." + Contract.SavedGame.NUMBER_OF_ELECTIONS_WEEK) + ", "
                + (Contract.SavedGame.TABLE_NAME + "." + Contract.SavedGame.ON_THE_STEP) + ", "
                + (Contract.SavedGame.TABLE_NAME + "." + Contract.SavedGame.FIRST_STEP_IN_GAME) + ", "
                + (Contract.SavedGame.TABLE_NAME + "." + Contract.SavedGame.RECOMMENDATION)
                + " FROM " + Contract.SavedGame.TABLE_NAME
                + " WHERE " + Contract.SavedGame._ID + " = " + arg;
        Cursor c = db.rawQuery(select,null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            do {
                obj.putInt("actualWeek",c.getInt(0));
                obj.putInt("numberOfElectionWeek",c.getInt(1));
                obj.putInt("onStep",c.getInt(2));
                obj.putInt("firstStepInGame",c.getInt(3));
                obj.putInt("recommendation",c.getInt(4));
            } while (c.moveToNext());
        }

        //charaktery
        select = "SELECT "
                + (Contract.SavedCandidate.TABLE_NAME + "." + Contract.SavedCandidate.CANDIDATE) + ", "
                + (Contract.SavedCandidate.TABLE_NAME + "." + Contract.SavedCandidate.CHARISMA) + ", "
                + (Contract.SavedCandidate.TABLE_NAME + "." + Contract.SavedCandidate.REPUTATION) + ", "
                + (Contract.SavedCandidate.TABLE_NAME + "." + Contract.SavedCandidate.MONEY) + ", "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate._ID)
                + " FROM " + Contract.SavedCandidate.TABLE_NAME
                + " INNER JOIN " + Contract.Candidate.TABLE_NAME
                + " ON " + (Contract.SavedCandidate.TABLE_NAME + "." + Contract.SavedCandidate.ID_CANDIDATE) + "=" + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate._ID)
                + " WHERE " + (Contract.SavedCandidate.TABLE_NAME + "." + Contract.SavedCandidate.ID_SAVED_GAME) + "=" + arg;
        c = db.rawQuery(select,null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            do {
                Bundle candidate = new Bundle();
                candidate.putInt("charisma", c.getInt(1));
                candidate.putInt("reputation", c.getInt(2));
                candidate.putInt("money", c.getInt(3));
                candidate.putInt("idCharacter", c.getInt(4));

                int character = c.getInt(0);
                if (character == 1) //hrac
                    obj.putBundle("candidate", candidate);
                else if (character == 0) //protihrac
                    obj.putBundle("opponent", candidate);
            } while (c.moveToNext());
        }

        //stat hry
        select = "SELECT "
                + (Contract.State.TABLE_NAME + "." + Contract.State._ID) + ", "
                + (Contract.State.TABLE_NAME + "." + Contract.State.STATE_NAME)
                + " FROM " + Contract.SavedRegion.TABLE_NAME
                + " INNER JOIN " + Contract.Region.TABLE_NAME
                + " ON " + (Contract.SavedRegion.TABLE_NAME + "." + Contract.SavedRegion.ID_REGION) + "=" + (Contract.Region.TABLE_NAME + "." + Contract.Region._ID)
                + " INNER JOIN " + Contract.State.TABLE_NAME
                + " ON " + (Contract.State.TABLE_NAME + "." + Contract.State._ID) + "=" + (Contract.Region.TABLE_NAME + "." + Contract.Region.ID_STATE)
                + " WHERE " + (Contract.SavedRegion.TABLE_NAME + "." + Contract.SavedRegion.ID_SAVED_GAME) + "=" + arg;
        c = db.rawQuery(select,null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            obj.putInt("idState", c.getInt(0));
            obj.putString("stateName",c.getString(1));
        }

        //regiony pre aktualny stav
        select = "SELECT "
                + (Contract.SavedRegion.TABLE_NAME + "." + Contract.SavedRegion.PRO_CANDIDATE) + ", "
                + (Contract.SavedRegion.TABLE_NAME + "." + Contract.SavedRegion.UNDECIDED) + ", "
                + (Contract.SavedRegion.TABLE_NAME + "." + Contract.SavedRegion.PRO_OPPONENT) + ", "
                + (Contract.Region.TABLE_NAME + "." + Contract.Region._ID)
                + " FROM " + Contract.SavedRegion.TABLE_NAME
                + " INNER JOIN " + Contract.Region.TABLE_NAME
                + " ON " + (Contract.SavedRegion.TABLE_NAME + "." + Contract.SavedRegion.ID_REGION) + "=" + (Contract.Region.TABLE_NAME + "." + Contract.Region._ID)
                + " WHERE " + (Contract.SavedRegion.TABLE_NAME + "." + Contract.SavedRegion.ID_SAVED_GAME) + "=" + arg;
        c = db.rawQuery(select,null);
        if (c != null && c.getCount() > 0) {
            Bundle regions = new Bundle();
            regions.putInt("count",c.getCount());
            c.moveToFirst();
            int index = 0;
            do {
                Bundle region = new Bundle();
                region.putLong("proCharacter", c.getLong(0));
                region.putLong("undecided", c.getLong(1));
                region.putLong("proOpponent", c.getLong(2));
                region.putInt("idRegion", c.getInt(3));
                regions.putBundle("region" + String.valueOf(index), region);
                index++;
            } while (c.moveToNext());
            obj.putBundle("regions", regions);
        }

        //cela historia
        select = "SELECT "
                + (Contract.SavedHistory.TABLE_NAME + "." + Contract.SavedHistory._ID) + ", "
                + (Contract.SavedHistory.TABLE_NAME + "." + Contract.SavedHistory.WEEK)
                + " FROM " + Contract.SavedHistory.TABLE_NAME
                + " WHERE " + Contract.SavedHistory.ID_SAVED_GAME + "=" + arg;
        c = db.rawQuery(select,null);
        if (c != null && c.getCount() > 0) {
            Bundle history = new Bundle();
            history.putInt("count",c.getCount());
            int index = 0;
            c.moveToFirst();
            do {
                Bundle weekActions = new Bundle();
                weekActions.putInt("week",c.getInt(1));
                int idSavedHistory = c.getInt(0);
                select = "SELECT "
                        + (Contract.RegionOfSavedHistory.TABLE_NAME + "." + Contract.RegionOfSavedHistory.CANDIDATE) + ", "
                        + (Contract.RegionOfSavedHistory.TABLE_NAME + "." + Contract.RegionOfSavedHistory.REGION_OF_ACTION) + ", "
                        + (Contract.RegionOfSavedHistory.TABLE_NAME + "." + Contract.RegionOfSavedHistory.BEFORE_PRO_CANDIDATE) + ", "
                        + (Contract.RegionOfSavedHistory.TABLE_NAME + "." + Contract.RegionOfSavedHistory.BEFORE_UNDECIDED) + ", "
                        + (Contract.RegionOfSavedHistory.TABLE_NAME + "." + Contract.RegionOfSavedHistory.BEFORE_PRO_OPPONENT) + ", "
                        + (Contract.RegionOfSavedHistory.TABLE_NAME + "." + Contract.RegionOfSavedHistory.AFTER_PRO_CANDIDATE) + ", "
                        + (Contract.RegionOfSavedHistory.TABLE_NAME + "." + Contract.RegionOfSavedHistory.AFTER_UNDECIDED) + ", "
                        + (Contract.RegionOfSavedHistory.TABLE_NAME + "." + Contract.RegionOfSavedHistory.AFTER_PRO_OPPONENT) + ", "
                        + (Contract.Region.TABLE_NAME + "." + Contract.Region._ID) + ", "
                        + (Contract.Problems.TABLE_NAME + "." + Contract.Problems._ID) + ", "
                        + (Contract.Problems.TABLE_NAME + "." + Contract.Problems.PROBLEM_NAME) + ", "
                        + (Contract.Problems.TABLE_NAME + "." + Contract.Problems.ACTION_TYPE)
                        + " FROM " + Contract.RegionOfSavedHistory.TABLE_NAME
                        + " INNER JOIN " + Contract.Region.TABLE_NAME
                        + " ON " + (Contract.RegionOfSavedHistory.TABLE_NAME + "." + Contract.RegionOfSavedHistory.ID_REGION) + "=" + (Contract.Region.TABLE_NAME + "." + Contract.Region._ID)
                        + " INNER JOIN " + Contract.Problems.TABLE_NAME
                        + " ON " + (Contract.RegionOfSavedHistory.TABLE_NAME + "." + Contract.RegionOfSavedHistory.ID_PROBLEM) + "=" + (Contract.Problems.TABLE_NAME + "." + Contract.Problems._ID)
                        + " WHERE " + (Contract.RegionOfSavedHistory.TABLE_NAME + "." + Contract.RegionOfSavedHistory.ID_SAVED_HISTORY) + "=" + idSavedHistory + ";";
                Cursor c2 = db.rawQuery(select,null);
                if (c2 != null && c2.getCount() > 0) {
                    Bundle regions = new Bundle();
                    regions.putInt("count", c2.getCount());
                    int index2 = 0;
                    c2.moveToFirst();

                    weekActions.putInt("character",c2.getInt(0));
                    weekActions.putString("actionType",c2.getString(11));

                    do {
                        Bundle region = new Bundle();
                        region.putInt("regionOfAction",c2.getInt(1));
                        region.putLong("beforeProPlayer",c2.getLong(2));
                        region.putLong("beforeUndecided",c2.getLong(3));
                        region.putLong("beforeProOpponent",c2.getLong(4));
                        region.putLong("afterProPlayer",c2.getLong(5));
                        region.putLong("afterUndecided",c2.getLong(6));
                        region.putLong("afterProOpponent",c2.getLong(7));
                        region.putInt("idRegion",c2.getInt(8));
                        region.putInt("idProblem",c2.getInt(9));
                        region.putString("problemName",c2.getString(10));
                        regions.putBundle("region"+String.valueOf(index2),region);
                        index2++;
                    } while (c2.moveToNext());
                    weekActions.putBundle("regions",regions);
                }
                c2.close();
                history.putBundle("history"+String.valueOf(index),weekActions);
                index++;
            } while (c.moveToNext());
            obj.putBundle("history",history);
        }

        c.close();
        return obj;
    }

    private Bundle returnInformationsForSavedGames(String arg) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String select = "SELECT "
                + (Contract.SavedGame.TABLE_NAME + "." + Contract.SavedGame._ID) + ", "
                + (Contract.SavedGame.TABLE_NAME + "." + Contract.SavedGame.CHANGED)
                + " FROM " + Contract.SavedGame.TABLE_NAME;
        Cursor c = db.rawQuery(select,null);
        if (c != null && c.getCount() > 0) {
            Bundle obj = new Bundle();
            obj.putInt("count", c.getCount());
            int index = 0;
            c.moveToFirst();
            do {
                Bundle row = new Bundle();
                int id = c.getInt(0);
                row.putInt("id", id);
                row.putLong("changed", c.getLong(1));

                select = "SELECT "
                        + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.NAME_SURNAME) + ", "
                        + (Contract.SavedCandidate.TABLE_NAME + "." + Contract.SavedCandidate.CANDIDATE) + ", "
                        + (Contract.State.TABLE_NAME + "." + Contract.State.STATE_NAME)
                        + " FROM " + Contract.SavedGame.TABLE_NAME
                        + " INNER JOIN " + Contract.SavedCandidate.TABLE_NAME
                        + " ON " + (Contract.SavedGame.TABLE_NAME + "." + Contract.SavedGame._ID) + "="
                        + (Contract.SavedCandidate.TABLE_NAME + "." + Contract.SavedCandidate.ID_SAVED_GAME)
                        + " INNER JOIN " + Contract.Candidate.TABLE_NAME
                        + " ON " + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate._ID) + "="
                        + (Contract.SavedCandidate.TABLE_NAME + "." + Contract.SavedCandidate.ID_CANDIDATE)
                        + " INNER JOIN " + Contract.Region.TABLE_NAME
                        + " ON " + (Contract.Region.TABLE_NAME + "." + Contract.Region._ID) + "="
                        + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.ID_BIRTH_REGION)
                        + " INNER JOIN " + Contract.State.TABLE_NAME
                        + " ON " + (Contract.Region.TABLE_NAME + "." + Contract.Region.ID_STATE) + "="
                        + (Contract.State.TABLE_NAME + "." + Contract.State._ID)
                        + " WHERE " + (Contract.SavedGame.TABLE_NAME + "." + Contract.SavedGame._ID) + "=" + String.valueOf(id);
                Cursor c2 = db.rawQuery(select, null);
                if (c2 != null && c2.getCount() > 0) {
                    c2.moveToFirst();
                    row.putString("stateName",c2.getString(2));
                    do {
                        if (c2.getInt(1) == 0) //ak je tam 0, ide o oponenta
                            row.putString("opponentNameSurname", c2.getString(0));
                        else //ak je tam 1, ide o charakter
                            row.putString("characterNameSurname", c2.getString(0));
                    } while (c2.moveToNext());
                }

                select = "SELECT "
                        + (Contract.SavedRegion.TABLE_NAME + "." + Contract.SavedRegion.PRO_CANDIDATE) + ", "
                        + (Contract.SavedRegion.TABLE_NAME + "." + Contract.SavedRegion.UNDECIDED) + ", "
                        + (Contract.SavedRegion.TABLE_NAME + "." + Contract.SavedRegion.PRO_OPPONENT)
                        + " FROM " + Contract.SavedGame.TABLE_NAME
                        + " INNER JOIN " + Contract.SavedRegion.TABLE_NAME
                        + " ON " + (Contract.SavedGame.TABLE_NAME + "." + Contract.SavedGame._ID) + "="
                        + (Contract.SavedRegion.TABLE_NAME + "." + Contract.SavedRegion.ID_SAVED_GAME)
                        + " WHERE " + (Contract.SavedGame.TABLE_NAME + "." + Contract.SavedGame._ID) + "=" + String.valueOf(id);
                c2 = db.rawQuery(select, null);
                if (c2 != null && c2.getCount() > 0) {
                    c2.moveToFirst();
                    long proCharacter = 0;
                    long undecided = 0;
                    long proOpponent = 0;
                    do {
                        proCharacter += c2.getLong(0);
                        undecided += c2.getLong(1);
                        proOpponent += c2.getLong(2);
                    } while (c2.moveToNext());
                    row.putLong("proCharacter",proCharacter);
                    row.putLong("undecided",undecided);
                    row.putLong("proOpponent",proOpponent);
                }
                c2.close();
                obj.putBundle("row" + String.valueOf(index), row);
                index++;
            } while (c.moveToNext());
            c.close();
            return obj;
        }
        else
            return null;
    }

    private Bundle returnAllNeighboringRegionsByIdRegion(String arg) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String select = "SELECT "
                + (Contract.Region.TABLE_NAME + "." + Contract.Region._ID) + ", "
                + (Contract.Region.TABLE_NAME + "." + Contract.Region.REGION_NAME) + ", "
                + (Contract.Region.TABLE_NAME + "." + Contract.Region.ID_STATE) + ", "
                + (Contract.Region.TABLE_NAME + "." + Contract.Region.POPULATION) + ", "
                + (Contract.Region.TABLE_NAME + "." + Contract.Region.CHANGED)
                + " FROM " + Contract.Region.TABLE_NAME
                + " INNER JOIN " + Contract.RegionNeighborhood.TABLE_NAME
                + " ON " + (Contract.Region.TABLE_NAME + "." + Contract.Region._ID) + "=" + (Contract.RegionNeighborhood.TABLE_NAME + "." + Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD)
                + " WHERE "+ (Contract.RegionNeighborhood.TABLE_NAME + "." + Contract.RegionNeighborhood.ID_REGION) + "=" + arg;
        Cursor c = db.rawQuery(select,null);
        if (c != null && c.getCount() > 0) {
            Bundle obj = new Bundle();
            obj.putInt("count",c.getCount());
            int index = 0;
            c.moveToFirst();
            do {
                Bundle row = new Bundle();
                row.putInt("id",c.getInt(0));
                row.putString("regionName",c.getString(1));
                row.putInt("idState",c.getInt(2));
                row.putLong("population",c.getLong(3));
                row.putLong("changed",c.getLong(4));
                obj.putBundle("row"+String.valueOf(index),row);
                index++;
            } while (c.moveToNext());
            c.close();
            return  obj;
        }
        else
            return null;
    }

    private Bundle returnAllProblemsOfRegionByRegionNameAndActionType(String arg) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String select = "SELECT "
                + (Contract.Problems.TABLE_NAME + "." + Contract.Problems._ID) + ", "
                + (Contract.Problems.TABLE_NAME + "." + Contract.Problems.PROBLEM_NAME) + ", "
                + (Contract.Problems.TABLE_NAME + "." + Contract.Problems.ID_REGION) + ", "
                + (Contract.Problems.TABLE_NAME + "." + Contract.Problems.ACTION_TYPE) + ", "
                + (Contract.Problems.TABLE_NAME + "." + Contract.Problems.EFFECT) + ", "
                + (Contract.Problems.TABLE_NAME + "." + Contract.Problems.SOLVABILITY) + ", "
                + (Contract.Problems.TABLE_NAME + "." + Contract.Problems.CHANGED)
                + " FROM " + Contract.Region.TABLE_NAME
                + " INNER JOIN " + Contract.Problems.TABLE_NAME
                + " ON " + (Contract.Region.TABLE_NAME + "." + Contract.Region._ID) + "=" + (Contract.Problems.TABLE_NAME + "." + Contract.Problems.ID_REGION)
                + arg;
        Cursor c = db.rawQuery(select,null);
        if (c != null && c.getCount() > 0) {
            Bundle obj = new Bundle();
            obj.putInt("count",c.getCount());
            int index = 0;
            c.moveToFirst();
            do {
                Bundle row = new Bundle();
                row.putInt("id",c.getInt(0));
                row.putString("problemName",c.getString(1));
                row.putInt("idRegion",c.getInt(2));
                row.putString("actionType",c.getString(3));
                row.putDouble("effect",c.getDouble(4));
                row.putDouble("solvability",c.getDouble(5));
                row.putLong("changed",c.getLong(6));
                obj.putBundle("row"+String.valueOf(index),row);
                index++;
            } while (c.moveToNext());
            c.close();
            return  obj;
        }
        else
            return null;
    }

    private Bundle returnAllRegionsInStateByIdState(String arg) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String select = "SELECT "
                + (Contract.Region.TABLE_NAME + "." + Contract.Region._ID) + ", "
                + (Contract.Region.TABLE_NAME + "." + Contract.Region.REGION_NAME) + ", "
                + (Contract.Region.TABLE_NAME + "." + Contract.Region.ABBREVIATION) + ", "
                + (Contract.Region.TABLE_NAME + "." + Contract.Region.POPULATION)
                + " FROM " + Contract.State.TABLE_NAME
                + " INNER JOIN " + Contract.Region.TABLE_NAME
                + " ON " + (Contract.Region.TABLE_NAME + "." + Contract.Region.ID_STATE) + "=" + (Contract.State.TABLE_NAME + "." + Contract.State._ID)
                + " WHERE " + (Contract.State.TABLE_NAME + "." + Contract.State._ID) + "='" + arg + "';";
        Cursor c = db.rawQuery(select,null);
        if (c != null && c.getCount() > 0) {
            Bundle obj = new Bundle();
            obj.putInt("count",c.getCount());
            int index = 0;
            c.moveToFirst();
            do {
                Bundle row = new Bundle();
                row.putInt("id",c.getInt(0));
                row.putString("regionName",c.getString(1));
                row.putString("abbreviation",c.getString(2));
                row.putLong("population",c.getLong(3));
                obj.putBundle("row"+String.valueOf(index),row);
                index++;
            } while (c.moveToNext());
            c.close();
            return  obj;
        }
        else
            return null;
    }

    private Bundle returnAllRegionsInStateByStateName(String arg) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String select = "SELECT "
                + (Contract.Region.TABLE_NAME + "." + Contract.Region._ID) + ", "
                + (Contract.Region.TABLE_NAME + "." + Contract.Region.REGION_NAME) + ", "
                + (Contract.Region.TABLE_NAME + "." + Contract.Region.ABBREVIATION) + ", "
                + (Contract.Region.TABLE_NAME + "." + Contract.Region.POPULATION)
                + " FROM " + Contract.State.TABLE_NAME
                + " INNER JOIN " + Contract.Region.TABLE_NAME
                + " ON " + (Contract.Region.TABLE_NAME + "." + Contract.Region.ID_STATE) + "=" + (Contract.State.TABLE_NAME + "." + Contract.State._ID)
                + " WHERE " + (Contract.State.TABLE_NAME + "." + Contract.State.STATE_NAME) + "='" + arg + "';";
        Cursor c = db.rawQuery(select,null);
        if (c != null && c.getCount() > 0) {
            Bundle obj = new Bundle();
            obj.putInt("count",c.getCount());
            int index = 0;
            c.moveToFirst();
            do {
                Bundle row = new Bundle();
                row.putInt("id",c.getInt(0));
                row.putString("regionName",c.getString(1));
                row.putString("abbreviation",c.getString(2));
                row.putLong("population",c.getLong(3));
                obj.putBundle("row"+String.valueOf(index),row);
                index++;
            } while (c.moveToNext());
            c.close();
            return  obj;
        }
        else
            return null;
    }

    private Bundle returnAllCharactersInStateByStateName(String arg) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String select = "SELECT "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate._ID) + " AS character_id, "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.NAME_SURNAME) + ", "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.ID_BIRTH_REGION) + ", "
                + (Contract.Region.TABLE_NAME + "." + Contract.Region.REGION_NAME) + ", "
                + (Contract.Region.TABLE_NAME + "." + Contract.Region.ID_STATE) + ", "
                + (Contract.State.TABLE_NAME + "." + Contract.State.STATE_NAME) + ", "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.CHARISMA) + ", "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.REPUTATION) + ", "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.MONEY) + ", "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.CREATED_BY_USER) + ", "
                + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.CHANGED)
                + " FROM " + Contract.State.TABLE_NAME
                + " INNER JOIN " + Contract.Region.TABLE_NAME
                + " ON " + (Contract.Region.TABLE_NAME + "." + Contract.Region.ID_STATE) + "=" + (Contract.State.TABLE_NAME + "." + Contract.State._ID)
                + " INNER JOIN " + Contract.Candidate.TABLE_NAME
                + " ON " + (Contract.Candidate.TABLE_NAME + "." + Contract.Candidate.ID_BIRTH_REGION) + "=" + (Contract.Region.TABLE_NAME + "." + Contract.Region._ID)
                + " WHERE " + (Contract.State.TABLE_NAME + "." + Contract.State.STATE_NAME) + "='" + arg + "';";
        Cursor c = db.rawQuery(select,null);
        if (c != null && c.getCount() > 0) {
            Bundle obj = new Bundle();
            obj.putInt("count",c.getCount());
            int index = 0;
            c.moveToFirst();
            do {
                Bundle row = new Bundle();
                row.putInt("id",c.getInt(0));
                row.putString("nameSurname",c.getString(1));
                row.putInt("idBirthRegion",c.getInt(2));
                row.putString("birthRegionName",c.getString(3));
                row.putInt("idBirthState",c.getInt(4));
                row.putString("birthStateName",c.getString(5));
                row.putInt("charisma",c.getInt(6));
                row.putInt("reputation",c.getInt(7));
                row.putInt("money",c.getInt(8));
                row.putInt("createdByUser",c.getInt(9));
                row.putLong("changed",c.getLong(10));
                obj.putBundle("row"+String.valueOf(index),row);
                index++;
            } while (c.moveToNext());
            c.close();
            return  obj;
        }
        else
            return null;
    }

    private String returnProblemNameByIdProblem(String arg) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String select = "SELECT "
                + (Contract.Problems.TABLE_NAME + "." + Contract.Problems.PROBLEM_NAME)
                + " FROM " + Contract.Problems.TABLE_NAME
                + " WHERE " + (Contract.Problems.TABLE_NAME + "." + Contract.Problems._ID) + "='" + arg + "';";
        Cursor c = db.rawQuery(select,null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            String string = c.getString(0);
            c.close();
            return string;
        }
        else
            return null;
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        switch (method) {
            //pouzite v aktivite GameStatistics.java, kde sa vola metoda getStatistics z TogetherFunctions
            case "return problemNames by idProblems" : {
                Bundle obj = new Bundle();
                ArrayList<String> problemNames = new ArrayList<>();
                ArrayList<Integer> idProblems = extras.getIntegerArrayList("idProblems");
                for (int i = 0; i < idProblems.size(); i++) {
                    problemNames.add(returnProblemNameByIdProblem(String.valueOf(idProblems.get(i))));
                }
                obj.putStringArrayList("problemNames",problemNames);
                return obj;
            }
            //pouzite v aktivite ChooseStateActivity.java pre podrobnejsie informacie v dolnej casti okna po kliknuti na konkretny stat v listviewe statov
            case "return informations for state by stateName": {
                return returnInformationsForStateByStateName(arg);
            }
            //pouzite v aktivite CreateUpdateOwnCandidateActivity.java pre nacitanie a update charakteru, ktory uz bol vytvoreny hracom
            case "return saved character by idCharacter": {
                return returnSavedCharacterByIdCharacter(arg);
            }
            //NIKDE NEPOUZITE!!!
            case "return count of characters in state by stateName": {
                return returnCountOfCharactersInStateByStateName(arg);
            }
            //pouzite v aktivite GameActivity.java pre vratenie informacii o kandidatovi a protihracovi (pouzite 2x pre rozne id)
            case "return character by idCharacter": {
                return returnCharacterByIdCharacter(arg);
            }
            //pouzite v aktivite GameActivity.java pre nacitanie vsetkeho ulozeneho v DB, co sa tyka ulozenej hry
            case "return saved game by idSavedGame": {
                return returnSavedGameByIdSavedGame(arg);
            }
            //pouzite v aktivite SavedGameActivity.java pre nacitanie zakladnych informacii urcenych pre vypis v tejto aktivite
            //pouzite v aktivite MainMenuActivity.java pre urcenie, ci su nejake ulozene hry, ak nie su, tak sa vypise len nejaky toast, alebo dialog ze nie su ziadne a aktivita s ulozenymi hrami sa nezapne
            case "return informations for saved games": {
                return returnInformationsForSavedGames(arg);
            }
            //pouzite v aktivite GameActivity.java pre nacitanie susednych regionov daneho regiona, kde sa nasledne nastavuju preferencie volicov po vykonani tahu
            case "return all neighboring regions by idRegion": {
                return returnAllNeighboringRegionsByIdRegion(arg);
            }
            //pouzite v aktivite GameActivity.java pre nacitanie problemov po vybere regionu a typu akcie, aka sa vykona v danom kraji
            case "return all problems of region by regionName and actionType": {
                return returnAllProblemsOfRegionByRegionNameAndActionType(arg);
            }
            //pouzite v aktivite GameActivity.java pre nacitanie vsetkych regionov, kde sa mozu konat akcie v ulozenej hre
            case "return all regions in state by idState": {
                return returnAllRegionsInStateByIdState(arg);
            }
            //pouzite v aktivite CreateUpdateOwnCandidateActivity.java pre nacitanie zoznamu regionov vybraneho statu, ktory sa pouzije v spinneri
            //pouzite v aktivite GameActivity.java pre nacitanie vsetkych regionov, kde sa mozu konat akcie v novej, nie ulozenej, hre
            case "return all regions in state by stateName": {
                return returnAllRegionsInStateByStateName(arg);
            }
            //pouzite v aktivite ChooseCandidateActivity.java pre nacitanie charakterov podla vybraneho statu
            //pouzite v aktivite GameActivity.java, kde z intentu vytahujem poradie vybranych hracov a nasledne tuto poziciu pouzijem pre poziciu objektu, ktory odosiela tato metoda, aby som identifikovala kandidata a protihraca
            //pouzite v aktivite SummaryBeforeGameActivity.java, ale toto pravdepodobne este vyhodim
            case "return all characters in state by stateName": {
                return returnAllCharactersInStateByStateName(arg);
            }
            default:
                return null;
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (Contract.State.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            return db.query(Contract.State.TABLE_NAME, projection, selection, selectionArgs, null, null,sortOrder);
        } else if (Contract.Region.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            return db.query(Contract.Region.TABLE_NAME, projection, selection, selectionArgs, null, null,sortOrder);
        } else if (Contract.RegionNeighborhood.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            return db.query(Contract.RegionNeighborhood.TABLE_NAME, projection, selection, selectionArgs, null, null,sortOrder);
        } else if (Contract.Problems.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            return db.query(Contract.Problems.TABLE_NAME, projection, selection, selectionArgs, null, null,sortOrder);
        } else if (Contract.Candidate.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            return db.query(Contract.Candidate.TABLE_NAME,null,selection,selectionArgs,null,null,null);
        } else if (Contract.SavedCandidate.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            return db.query(Contract.SavedCandidate.TABLE_NAME,null,selection,selectionArgs,null,null,null);
        } else if (Contract.SavedGame.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            return db.query(Contract.SavedGame.TABLE_NAME,null,selection,selectionArgs,null,null,null);
        } else if (Contract.SavedRegion.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            return db.query(Contract.SavedRegion.TABLE_NAME,null,selection,selectionArgs,null,null,null);
        } else if (Contract.SavedHistory.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            return db.query(Contract.SavedHistory.TABLE_NAME,null,selection,selectionArgs,null,null,null);
        } else if (Contract.RegionOfSavedHistory.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            return db.query(Contract.RegionOfSavedHistory.TABLE_NAME,null,selection,selectionArgs,null,null,null);
        } else if (Contract.Settings.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            return db.query(Contract.Settings.TABLE_NAME, null, selection, selectionArgs, null, null,null);
        } else
            return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        if (Contract.State.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            long id = db.insert(Contract.State.TABLE_NAME, null, values);
            getContext().getContentResolver().notifyChange(Contract.State.CONTENT_URI, null);
            return Uri.withAppendedPath(Contract.State.CONTENT_URI, String.valueOf(id));
        } else if (Contract.Region.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            long id = db.insert(Contract.Region.TABLE_NAME, null, values);
            getContext().getContentResolver().notifyChange(Contract.Region.CONTENT_URI, null);
            return Uri.withAppendedPath(Contract.Region.CONTENT_URI, String.valueOf(id));
        } else if (Contract.RegionNeighborhood.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            long id = db.insert(Contract.RegionNeighborhood.TABLE_NAME, null, values);
            getContext().getContentResolver().notifyChange(Contract.RegionNeighborhood.CONTENT_URI, null);
            return Uri.withAppendedPath(Contract.RegionNeighborhood.CONTENT_URI, String.valueOf(id));
        } else if (Contract.Problems.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            long id = db.insert(Contract.Problems.TABLE_NAME, null, values);
            getContext().getContentResolver().notifyChange(Contract.Problems.CONTENT_URI, null);
            return Uri.withAppendedPath(Contract.Problems.CONTENT_URI, String.valueOf(id));
        } else if (Contract.Candidate.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            long id = db.insert(Contract.Candidate.TABLE_NAME, null, values);
            getContext().getContentResolver().notifyChange(Contract.Candidate.CONTENT_URI, null);
            return Uri.withAppendedPath(Contract.Candidate.CONTENT_URI, String.valueOf(id));
        } else if (Contract.SavedCandidate.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            long id = db.insert(Contract.SavedCandidate.TABLE_NAME, null, values);
            getContext().getContentResolver().notifyChange(Contract.SavedCandidate.CONTENT_URI, null);
            return Uri.withAppendedPath(Contract.SavedCandidate.CONTENT_URI, String.valueOf(id));
        } else if (Contract.SavedGame.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            long id = db.insert(Contract.SavedGame.TABLE_NAME, null, values);
            getContext().getContentResolver().notifyChange(Contract.SavedGame.CONTENT_URI, null);
            return Uri.withAppendedPath(Contract.SavedGame.CONTENT_URI, String.valueOf(id));
        } else if (Contract.SavedRegion.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            long id = db.insert(Contract.SavedRegion.TABLE_NAME, null, values);
            getContext().getContentResolver().notifyChange(Contract.SavedRegion.CONTENT_URI, null);
            return Uri.withAppendedPath(Contract.SavedRegion.CONTENT_URI, String.valueOf(id));
        } else if (Contract.SavedHistory.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            long id = db.insert(Contract.SavedHistory.TABLE_NAME, null, values);
            getContext().getContentResolver().notifyChange(Contract.SavedHistory.CONTENT_URI, null);
            return Uri.withAppendedPath(Contract.SavedHistory.CONTENT_URI, String.valueOf(id));
        } else if (Contract.RegionOfSavedHistory.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            long id = db.insert(Contract.RegionOfSavedHistory.TABLE_NAME, null, values);
            getContext().getContentResolver().notifyChange(Contract.RegionOfSavedHistory.CONTENT_URI, null);
            return Uri.withAppendedPath(Contract.RegionOfSavedHistory.CONTENT_URI, String.valueOf(id));
        } else if (Contract.Settings.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            long id = db.insert(Contract.Settings.TABLE_NAME, null, values);
            getContext().getContentResolver().notifyChange(Contract.Settings.CONTENT_URI, null);
            return Uri.withAppendedPath(Contract.Settings.CONTENT_URI, String.valueOf(id));
        } else
            return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int insertCount = 0;
        if (Contract.State.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                for (ContentValues value : values) {
                    long id = db.insert(Contract.State.TABLE_NAME, null, value);
                    if (id > 0)
                        insertCount++;
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
            } finally {
                db.endTransaction();
            }
            return insertCount;
        } else if (Contract.Region.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                for (ContentValues value : values) {
                    long id = db.insert(Contract.Region.TABLE_NAME, null, value);
                    if (id > 0)
                        insertCount++;
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
            } finally {
                db.endTransaction();
            }
            return insertCount;
        } else if (Contract.RegionNeighborhood.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                for (ContentValues value : values) {
                    long id = db.insert(Contract.RegionNeighborhood.TABLE_NAME, null, value);
                    if (id > 0)
                        insertCount++;
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
            } finally {
                db.endTransaction();
            }
            return insertCount;
        } else if (Contract.Problems.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                for (ContentValues value : values) {
                    long id = db.insert(Contract.Problems.TABLE_NAME, null, value);
                    if (id > 0)
                        insertCount++;
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
            } finally {
                db.endTransaction();
            }
            return insertCount;
        } else if (Contract.Candidate.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                for (ContentValues value : values) {
                    long id = db.insert(Contract.Candidate.TABLE_NAME, null, value);
                    if (id > 0)
                        insertCount++;
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
            } finally {
                db.endTransaction();
            }
            return insertCount;
        } else if (Contract.SavedCandidate.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                for (ContentValues value : values) {
                    long id = db.insert(Contract.SavedCandidate.TABLE_NAME, null, value);
                    if (id > 0)
                        insertCount++;
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
            } finally {
                db.endTransaction();
            }
            return insertCount;
        } else if (Contract.SavedGame.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                for (ContentValues value : values) {
                    long id = db.insert(Contract.SavedGame.TABLE_NAME, null, value);
                    if (id > 0)
                        insertCount++;
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
            } finally {
                db.endTransaction();
            }
            return insertCount;
        } else if (Contract.SavedRegion.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                for (ContentValues value : values) {
                    long id = db.insert(Contract.SavedRegion.TABLE_NAME, null, value);
                    if (id > 0)
                        insertCount++;
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
            } finally {
                db.endTransaction();
            }
            return insertCount;
        } else if (Contract.SavedHistory.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                for (ContentValues value : values) {
                    long id = db.insert(Contract.SavedHistory.TABLE_NAME, null, value);
                    if (id > 0)
                        insertCount++;
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
            } finally {
                db.endTransaction();
            }
            return insertCount;
        }else if (Contract.RegionOfSavedHistory.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                for (ContentValues value : values) {
                    long id = db.insert(Contract.RegionOfSavedHistory.TABLE_NAME, null, value);
                    if (id > 0)
                        insertCount++;
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
            } finally {
                db.endTransaction();
            }
            return insertCount;
        } else if (Contract.Settings.CONTENT_URI.toString().equals(uri.toString())) {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                for (ContentValues value : values) {
                    long id = db.insert(Contract.Settings.TABLE_NAME, null, value);
                    if (id > 0)
                        insertCount++;
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
            } finally {
                db.endTransaction();
            }
            return insertCount;
        } else
            return 0;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        if (uri.toString().contains((Contract.State.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.delete(Contract.State.TABLE_NAME, Contract.State._ID + "=?", whereArgs);
            getContext().getContentResolver().notifyChange(Contract.State.CONTENT_URI, null);
            return affectedRows;
        } else if (uri.toString().contains((Contract.Region.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.delete(Contract.Region.TABLE_NAME, Contract.Region._ID + "=?", whereArgs);
            getContext().getContentResolver().notifyChange(Contract.Region.CONTENT_URI, null);
            return affectedRows;
        } else if (uri.toString().contains((Contract.RegionNeighborhood.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.delete(Contract.RegionNeighborhood.TABLE_NAME, Contract.RegionNeighborhood._ID + "=?", whereArgs);
            getContext().getContentResolver().notifyChange(Contract.RegionNeighborhood.CONTENT_URI, null);
            return affectedRows;
        } else if (uri.toString().contains((Contract.Problems.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.delete(Contract.Problems.TABLE_NAME, Contract.Problems._ID + "=?", whereArgs);
            getContext().getContentResolver().notifyChange(Contract.Problems.CONTENT_URI, null);
            return affectedRows;
        } else if (uri.toString().contains((Contract.Candidate.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.delete(Contract.Candidate.TABLE_NAME, Contract.Candidate._ID + "=?", whereArgs);
            getContext().getContentResolver().notifyChange(Contract.Candidate.CONTENT_URI, null);
            return affectedRows;
        } else if (uri.toString().contains((Contract.SavedCandidate.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.delete(Contract.SavedCandidate.TABLE_NAME, Contract.SavedCandidate._ID + "=?", whereArgs);
            getContext().getContentResolver().notifyChange(Contract.SavedCandidate.CONTENT_URI, null);
            return affectedRows;
        } else if (uri.toString().contains((Contract.SavedGame.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.delete(Contract.SavedGame.TABLE_NAME, Contract.SavedGame._ID + "=?", whereArgs);
            getContext().getContentResolver().notifyChange(Contract.SavedGame.CONTENT_URI, null);
            return affectedRows;
        } else if (uri.toString().contains((Contract.SavedRegion.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.delete(Contract.SavedRegion.TABLE_NAME, Contract.SavedRegion._ID + "=?", whereArgs);
            getContext().getContentResolver().notifyChange(Contract.SavedRegion.CONTENT_URI, null);
            return affectedRows;
        } else if (uri.toString().contains((Contract.SavedHistory.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.delete(Contract.SavedHistory.TABLE_NAME, Contract.SavedHistory._ID + "=?", whereArgs);
            getContext().getContentResolver().notifyChange(Contract.SavedHistory.CONTENT_URI, null);
            return affectedRows;
        } else if (uri.toString().contains((Contract.RegionOfSavedHistory.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.delete(Contract.RegionOfSavedHistory.TABLE_NAME, Contract.RegionOfSavedHistory._ID + "=?", whereArgs);
            getContext().getContentResolver().notifyChange(Contract.RegionOfSavedHistory.CONTENT_URI, null);
            return affectedRows;
        } else if (uri.toString().contains((Contract.Settings.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.delete(Contract.Settings.TABLE_NAME, Contract.Settings._ID + "=?", whereArgs);
            getContext().getContentResolver().notifyChange(Contract.Settings.CONTENT_URI, null);
            return affectedRows;
        } else
            return 0;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (uri.toString().contains((Contract.State.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.update(Contract.State.TABLE_NAME,values, Contract.State._ID + "=?",whereArgs);
            getContext().getContentResolver().notifyChange(Contract.State.CONTENT_URI, null);
            return affectedRows;
        } else if (uri.toString().contains((Contract.Region.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.update(Contract.Region.TABLE_NAME,values, Contract.Region._ID + "=?",whereArgs);
            getContext().getContentResolver().notifyChange(Contract.Region.CONTENT_URI, null);
            return affectedRows;
        } else if (uri.toString().contains((Contract.RegionNeighborhood.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.update(Contract.RegionNeighborhood.TABLE_NAME,values, Contract.RegionNeighborhood._ID + "=?",whereArgs);
            getContext().getContentResolver().notifyChange(Contract.RegionNeighborhood.CONTENT_URI, null);
            return affectedRows;
        } else if (uri.toString().contains((Contract.Problems.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.update(Contract.Problems.TABLE_NAME,values, Contract.Problems._ID + "=?",whereArgs);
            getContext().getContentResolver().notifyChange(Contract.Problems.CONTENT_URI, null);
            return affectedRows;
        } else if (uri.toString().contains((Contract.Candidate.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.update(Contract.Candidate.TABLE_NAME,values, Contract.Candidate._ID + "=?",whereArgs);
            getContext().getContentResolver().notifyChange(Contract.Candidate.CONTENT_URI, null);
            return affectedRows;
        } else if (uri.toString().contains((Contract.SavedCandidate.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.update(Contract.SavedCandidate.TABLE_NAME,values, Contract.SavedCandidate._ID + "=?",whereArgs);
            getContext().getContentResolver().notifyChange(Contract.SavedCandidate.CONTENT_URI, null);
            return affectedRows;
        } else if (uri.toString().contains((Contract.SavedGame.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.update(Contract.SavedGame.TABLE_NAME,values, Contract.SavedGame._ID + "=?",whereArgs);
            getContext().getContentResolver().notifyChange(Contract.SavedGame.CONTENT_URI, null);
            return affectedRows;
        } else if (uri.toString().contains((Contract.SavedRegion.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.update(Contract.SavedRegion.TABLE_NAME,values, Contract.SavedRegion._ID + "=?",whereArgs);
            getContext().getContentResolver().notifyChange(Contract.SavedRegion.CONTENT_URI, null);
            return affectedRows;
        } else if (uri.toString().contains((Contract.SavedHistory.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.update(Contract.SavedHistory.TABLE_NAME,values, Contract.SavedHistory._ID + "=?",whereArgs);
            getContext().getContentResolver().notifyChange(Contract.SavedHistory.CONTENT_URI, null);
            return affectedRows;
        } else if (uri.toString().contains((Contract.RegionOfSavedHistory.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.update(Contract.RegionOfSavedHistory.TABLE_NAME,values, Contract.RegionOfSavedHistory._ID + "=?",whereArgs);
            getContext().getContentResolver().notifyChange(Contract.RegionOfSavedHistory.CONTENT_URI, null);
            return affectedRows;
        } else if (uri.toString().contains((Contract.Settings.CONTENT_URI.toString()))) {
            String id = uri.getLastPathSegment();
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] whereArgs = {id};
            int affectedRows = db.update(Contract.Settings.TABLE_NAME,values, Contract.Settings._ID + "=?",whereArgs);
            getContext().getContentResolver().notifyChange(Contract.Settings.CONTENT_URI, null);
            return affectedRows;
        }
        else
            return 0;
    }
}
