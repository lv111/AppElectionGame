package com.game.election.electiongame.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import static android.content.ContentResolver.SCHEME_CONTENT;


public interface Contract {

    interface State extends BaseColumns {
        String TABLE_NAME = "state";
        String STATE_NAME = "state_name";
        String POPULATION = "population";
        String NUMBER_OF_REGIONS = "number_of_regions";
        String CHANGED = "changed";

        String AUTHORITY = "com.game.election.electiongame.provider";
        Uri CONTENT_URI = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +" ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + STATE_NAME + " TEXT,"
                + POPULATION + " LONG,"
                + NUMBER_OF_REGIONS + " INTEGER,"
                + CHANGED + " LONG); ";
    }

    interface Region extends BaseColumns {
        String TABLE_NAME = "region";
        String REGION_NAME = "region_name";
        String ABBREVIATION = "abbreviation";
        String ID_STATE = "id_state";
        String POPULATION = "population";
        String CHANGED = "changed";

        int COLUMN_INDEX_ID = 0;

        String AUTHORITY = "com.game.election.electiongame.provider";
        Uri CONTENT_URI = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +" ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + REGION_NAME + " TEXT, "
                + ABBREVIATION + " TEXT, "
                + ID_STATE + " INTEGER, "
                + POPULATION + " LONG, "
                + CHANGED + " LONG, "
                + "FOREIGN KEY (" + ID_STATE + ") REFERENCES " + State.TABLE_NAME + "(" + State._ID + ") ON DELETE CASCADE);";
    }

    interface RegionNeighborhood extends BaseColumns {

        String TABLE_NAME = "region_neighborhood";
        String ID_REGION = "id_region";
        String ID_REGION_NEIGHBORHOOD = "id_region_neighborhood";
        String CHANGED = "changed";

        String AUTHORITY = "com.game.election.electiongame.provider";
        Uri CONTENT_URI = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +" ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ID_REGION + " INTEGER, "
                + ID_REGION_NEIGHBORHOOD + " INTEGER,"
                + CHANGED + " LONG, "
                + "FOREIGN KEY (" + ID_REGION + ") REFERENCES " + Region.TABLE_NAME + "(" + Region._ID + ") ON DELETE CASCADE,"
                + "FOREIGN KEY (" + ID_REGION_NEIGHBORHOOD + ") REFERENCES " + Region.TABLE_NAME + "(" + Region._ID + ") ON DELETE CASCADE);";
    }

    interface Problems extends BaseColumns {
        String TABLE_NAME = "problems";
        String PROBLEM_NAME = "problem_name";
        String ID_REGION = "id_region";
        String ACTION_TYPE = "action_type";
        String EFFECT = "effect";
        String SOLVABILITY = "solvability";
        String CHANGED = "changed";

        String AUTHORITY = "com.game.election.electiongame.provider";
        Uri CONTENT_URI = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +" ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PROBLEM_NAME + " TEXT, "
                + ID_REGION + " INTEGER, "
                + ACTION_TYPE + " TEXT, "
                + EFFECT + " REAL, "
                + SOLVABILITY + " REAL, "
                + CHANGED + " LONG, "
                + "FOREIGN KEY (" + ID_REGION + ") REFERENCES " + Region.TABLE_NAME + "(" + Region._ID + ") ON DELETE CASCADE);";
    }

    interface Candidate extends BaseColumns {

        String TABLE_NAME = "candidate";
        String NAME_SURNAME = "name_surname";
        String ID_BIRTH_REGION = "id_birth_region";
        String CHARISMA = "charisma";
        String REPUTATION = "reputation";
        String MONEY = "money";
        String CREATED_BY_USER = "created_by_user";
        String CHANGED = "changed";

        String AUTHORITY = "com.game.election.electiongame.provider";
        Uri CONTENT_URI = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +" ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NAME_SURNAME + " TEXT, "
                + ID_BIRTH_REGION + " INTEGER, "
                + CHARISMA + " INTEGER, "
                + REPUTATION + " INTEGER, "
                + MONEY + " INTEGER, "
                + CREATED_BY_USER + " INTEGER, "
                + CHANGED + " LONG, "
                + "FOREIGN KEY (" + ID_BIRTH_REGION + ") REFERENCES " + Region.TABLE_NAME + "(" + Region._ID + ") ON DELETE CASCADE);";
    }

    interface SavedGame extends BaseColumns {

        String TABLE_NAME = "saved_game";
        String ACTUAL_ELECTION_WEEK = "actual_election_week";
        String NUMBER_OF_ELECTIONS_WEEK = "number_of_elections_week";
        String ON_THE_STEP = "on_the_step";
        String FIRST_STEP_IN_GAME = "first_step_in_game";
        String RECOMMENDATION = "recommendation";
        String CHANGED = "changed";

        int COLUMN_INDEX_ID = 0;

        String AUTHORITY = "com.game.election.electiongame.provider";
        Uri CONTENT_URI = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +" ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ACTUAL_ELECTION_WEEK + " INTEGER,"
                + NUMBER_OF_ELECTIONS_WEEK + " INTEGER,"
                + ON_THE_STEP + " INTEGER, "
                + FIRST_STEP_IN_GAME + " INTEGER,"
                + RECOMMENDATION + " INTEGER,"
                + CHANGED + " LONG);";
    }

    interface SavedCandidate extends BaseColumns {

        String TABLE_NAME = "saved_candidate";
        String ID_CANDIDATE = "id_candidate";
        String ID_SAVED_GAME = "id_saved_game";
        String CANDIDATE = "candidate";
        String CHARISMA = "charisma";
        String REPUTATION = "reputation";
        String MONEY = "money";
        String CHANGED = "changed";

        int COLUMN_INDEX_ID = 0;
        int COLUMN_INDEX_CANDIDATE = 3;

        String AUTHORITY = "com.game.election.electiongame.provider";
        Uri CONTENT_URI = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +" ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ID_CANDIDATE + " INTEGER,"
                + ID_SAVED_GAME + " INTEGER,"
                + CANDIDATE + " INTEGER,"
                + CHARISMA + " INTEGER,"
                + REPUTATION + " INTEGER,"
                + MONEY + " INTEGER,"
                + CHANGED + " LONG, "
                + "FOREIGN KEY (" + ID_CANDIDATE + ") REFERENCES " + Candidate.TABLE_NAME + "(" + Candidate._ID + ") ON DELETE CASCADE,"
                + "FOREIGN KEY (" + ID_SAVED_GAME + ") REFERENCES " + SavedGame.TABLE_NAME + "(" + SavedGame._ID + ") ON DELETE CASCADE);";
    }

    interface SavedRegion extends BaseColumns {
        String TABLE_NAME = "saved_region";
        String ID_SAVED_GAME = "id_saved_game";
        String ID_REGION = "id_region";
        String PRO_CANDIDATE = "pro_candidate";
        String UNDECIDED = "undecided";
        String PRO_OPPONENT = "pro_opponent";
        String CHANGED = "changed";

        int COLUMN_INDEX_ID = 0;
        int COLUMN_INDEX_ID_REGION = 2;

        String AUTHORITY = "com.game.election.electiongame.provider";
        Uri CONTENT_URI = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +" ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ID_SAVED_GAME + " INTEGER,"
                + ID_REGION + " INTEGER,"
                + PRO_CANDIDATE + " REAL,"
                + UNDECIDED + " REAL,"
                + PRO_OPPONENT + " REAL,"
                + CHANGED + " LONG, "
                + "FOREIGN KEY (" + ID_SAVED_GAME + ") REFERENCES " + SavedGame.TABLE_NAME + "(" + SavedGame._ID + ") ON DELETE CASCADE,"
                + "FOREIGN KEY (" + ID_REGION + ") REFERENCES " + Region.TABLE_NAME + "(" + Region._ID + ") ON DELETE CASCADE);";
    }

    interface SavedHistory extends BaseColumns {

        String TABLE_NAME = "saved_history";
        String ID_SAVED_GAME = "id_saved_game";
        String WEEK = "week";
        String CHANGED = "changed";

        int COLUMN_INDEX_WEEK = 2;

        String AUTHORITY = "com.game.election.electiongame.provider";
        Uri CONTENT_URI = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +" ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ID_SAVED_GAME + " INTEGER,"
                + WEEK + " INTEGER,"
                + CHANGED + " LONG, "
                + "FOREIGN KEY (" + ID_SAVED_GAME + ") REFERENCES " + SavedGame.TABLE_NAME + "(" + SavedGame._ID + ") ON DELETE CASCADE);";
    }

    interface RegionOfSavedHistory extends BaseColumns {

        String TABLE_NAME = "region_of_saved_history";
        String ID_SAVED_HISTORY = "id_saved_history";
        String CANDIDATE = "candidate";
        String ID_REGION = "id_region";
        String REGION_OF_ACTION = "region_of_action";
        String ID_PROBLEM = "id_problem";
        String BEFORE_PRO_CANDIDATE = "before_pro_candidate";
        String BEFORE_UNDECIDED = "before_undecided";
        String BEFORE_PRO_OPPONENT = "before_pro_opponent";
        String AFTER_PRO_CANDIDATE = "after_pro_candidate";
        String AFTER_UNDECIDED = "after_undecided";
        String AFTER_PRO_OPPONENT = "after_pro_opponent";
        String CHANGED = "changed";

        String AUTHORITY = "com.game.election.electiongame.provider";
        Uri CONTENT_URI = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +" ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ID_SAVED_HISTORY + " INTEGER,"
                + CANDIDATE + " INTEGER,"
                + ID_REGION + " INTEGER,"
                + REGION_OF_ACTION + " INTEGER,"
                + ID_PROBLEM + " INTEGER,"
                + BEFORE_PRO_CANDIDATE + " REAL,"
                + BEFORE_UNDECIDED + " REAL,"
                + BEFORE_PRO_OPPONENT + " REAL,"
                + AFTER_PRO_CANDIDATE + " REAL,"
                + AFTER_UNDECIDED + " REAL,"
                + AFTER_PRO_OPPONENT + " REAL,"
                + CHANGED + " LONG, "
                + "FOREIGN KEY (" + ID_SAVED_HISTORY + ") REFERENCES " + SavedHistory.TABLE_NAME + "(" + SavedHistory._ID + ") ON DELETE CASCADE,"
                + "FOREIGN KEY (" + ID_REGION + ") REFERENCES " + Region.TABLE_NAME + "(" + Region._ID + ") ON DELETE CASCADE,"
                + "FOREIGN KEY (" + ID_PROBLEM + ") REFERENCES " + Problems.TABLE_NAME + "(" + Problems._ID + ") ON DELETE CASCADE);";
    }

    interface Settings extends BaseColumns {

        String TABLE_NAME = "settings";
        String NUMBER_OF_ELECTIONS_WEEK = "number_of_elections_week";
        String FIRST_STEP_IN_GAME = "first_step_in_game";
        String RECOMMENDATION = "recommendation";
        String CHANGED = "changed";

        int COLUMN_INDEX_ID = 0;
        int COLUMN_INDEX_NUMBER_OF_ELECTIONS_WEEK = 1;
        int COLUMN_INDEX_FIRST_STEP_IN_GAME = 2;
        int COLUMN_INDEX_RECOMMENDATION = 3;

        String AUTHORITY = "com.game.election.electiongame.provider";
        Uri CONTENT_URI = new Uri.Builder()
                .scheme(SCHEME_CONTENT)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +" ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NUMBER_OF_ELECTIONS_WEEK + " INTEGER, "
                + FIRST_STEP_IN_GAME + " INTEGER, "
                + RECOMMENDATION + " INTEGER, "
                + CHANGED + " LONG); ";
    }
}
