package com.game.election.electiongame;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.game.election.electiongame.adapter.SavedGameAdapter;
import com.game.election.electiongame.classes.ChangeVariable;
import com.game.election.electiongame.classes.SavedGame;
import com.game.election.electiongame.classes.TogetherFunctions;
import com.game.election.electiongame.provider.Contract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;


public class MainMenuActivity extends AppCompatActivity {

    TogetherFunctions tf;
    ChangeVariable numberOfElectionWeek, firstStep, recommendation;
    private float startX, startY;//premenne, ktore oznacuju bod pri dotyku pouzivatela s obrazovkou

    /*
    *
    * funkcia onCreate sa zapne ako prva v tejto aktivite
    * nastavuje sa tu hlavne to, aby bola aktivita v mode "fullscreen"
    * potom sa zapne funkcia setLayout
    *
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_menu);
        tf = new TogetherFunctions();
        setLayout();
    }
    /*
        *
        * kontroluje kazdy dotyk pouzivatela s obrazovkou, ak ide o kliknutie, ozve sa zvucka, ak nie, tak sa neozve
        *
        * */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                startX = ev.getX();
                startY = ev.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                float endX = ev.getX();
                float endY = ev.getY();
                tf.checkIfIsClick(getApplicationContext(), startX, endX, startY, endY);
                break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void setLayout() {
        numberOfElectionWeek = new ChangeVariable(0);
        firstStep = new ChangeVariable(0);
        insertDataToDatabase();
        setButtonOnClickListeners();

    }

    private void setButtonOnClickListeners() {
        LinearLayout linearLayoutNewGame = findViewById(R.id.linearLayoutNewGame);
        linearLayoutNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, ChooseStateActivity.class);
                startActivity(intent);
            }
        });

        Button buttonSavedGame = findViewById(R.id.buttonSavedGame);
        buttonSavedGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSavedGameDialog();
            }
        });

        Button buttonHowToPlay = findViewById(R.id.buttonHowToPlay);
        buttonHowToPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, HowToPlayActivity.class);
                startActivity(intent);
            }
        });

        Button buttonSettings = findViewById(R.id.buttonSettings);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSettingsDialog();
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private void insertDataToDatabase() {
        AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
                if (cursor.getCount() == 0) {
                    JSONObject obj = loadFile();
                    String language = null;
                    if (Locale.getDefault().getDisplayLanguage().toLowerCase().equals("slovenčina") ||
                            Locale.getDefault().getDisplayLanguage().toLowerCase().equals("čeština")
//TODO: anglictinu dat prec, pre testovanie je to zatial takto
                            || Locale.getDefault().getDisplayLanguage().toLowerCase().equals("english")) {
                        language = "sk";
                    }
                    else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.application_not_support_language), Toast.LENGTH_LONG).show();
                        finish();
                    }
                    try {
                        insertToTableState(obj,language);
                        insertToTableRegion(obj,language);
                        insertToTableProblems(obj,language);
                        insertToTableRegionNeighborhood(obj,language);
                        insertToTableCandidate(obj,language);
                        insertToTableSettings(obj);
                    }
                    catch (Exception e) {
                        Log.e("something wrong",e.toString());
                    }
                }
            }
        };
        asyncQueryHandler.startQuery(0,null, Contract.Settings.CONTENT_URI,null,null,null,null);
    }

    public JSONObject loadFile() {
        try {
            InputStream ins = getResources().openRawResource(getResources().getIdentifier("data", "raw", getPackageName()));
            InputStreamReader inputStreamReader = new InputStreamReader(ins, "windows-1250");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ( (receiveString = bufferedReader.readLine()) != null ) {
                stringBuilder.append(receiveString);
            }

            return new JSONObject(stringBuilder.toString());
        }
        catch (Exception e) {
            Log.e("something wrong",e.toString());
        }

        return null;
    }

    private void insertToTableState(JSONObject obj, String language) throws JSONException {
        JSONArray states = obj.getJSONArray(language);
        ContentValues[] contentValues = new ContentValues[states.length()];
        for (int i = 0; i < states.length(); i++) {
            JSONObject state = states.getJSONObject(i);

            contentValues[i] = new ContentValues();
            contentValues[i].put(Contract.State._ID,i+1);
            contentValues[i].put(Contract.State.STATE_NAME,state.getString("state_name"));
            contentValues[i].put(Contract.State.POPULATION,state.getLong("population"));
            contentValues[i].put(Contract.State.NUMBER_OF_REGIONS,state.getJSONArray("regions").length());
            contentValues[i].put(Contract.State.CHANGED,System.currentTimeMillis());
        }

         new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                getContentResolver().bulkInsert(Contract.State.CONTENT_URI,(ContentValues[]) params);
                return null;
            }
        }.execute(contentValues);
    }

    private void insertToTableRegion(JSONObject obj, String language) throws JSONException {
        JSONArray states = obj.getJSONArray(language);
        int regionCount = 0;

        for (int i = 0; i < states.length(); i++) {
            JSONObject state = states.getJSONObject(i);
            regionCount += state.getJSONArray("regions").length();
        }

        ContentValues[] contentValues = new ContentValues[regionCount];
        int idRegion = 1;
        for (int i = 0; i < states.length(); i++) {
            JSONObject state = states.getJSONObject(i);
            JSONArray regions = state.getJSONArray("regions");

            for (int j = 0; j < regions.length(); j++) {
                JSONObject region = regions.getJSONObject(j);

                contentValues[idRegion-1] = new ContentValues();
                contentValues[idRegion-1].put(Contract.Region._ID,idRegion);
                contentValues[idRegion-1].put(Contract.Region.REGION_NAME,region.getString("region_name"));
                contentValues[idRegion-1].put(Contract.Region.ABBREVIATION,region.getString("abbreviation"));
                contentValues[idRegion-1].put(Contract.Region.ID_STATE,i+1);
                contentValues[idRegion-1].put(Contract.Region.POPULATION,region.getLong("population"));
                contentValues[idRegion-1].put(Contract.Region.CHANGED,System.currentTimeMillis());

                idRegion++;
            }
        }

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                getContentResolver().bulkInsert(Contract.Region.CONTENT_URI,(ContentValues[]) params);
                return null;
            }
        }.execute(contentValues);
    }

    private void insertToTableRegionNeighborhood(JSONObject obj, String language) throws JSONException {
        JSONArray states = obj.getJSONArray(language);
        ArrayList<String> regionNames = new ArrayList<>();
        int regionNeighborhoodCount = 0;

        for (int i = 0; i < states.length(); i++) {
            JSONObject state = states.getJSONObject(i);
            JSONArray regions = state.getJSONArray("regions");

            for (int j = 0; j < regions.length(); j++) {
                JSONObject region = regions.getJSONObject(j);
                regionNames.add(region.getString("region_name"));
                regionNeighborhoodCount += region.getJSONArray("neighborhood").length();
            }
        }

        int regionId = 1,
                regionNeighborhoodId = 0;
        ContentValues[] contentValues = new ContentValues[regionNeighborhoodCount];
        for (int i = 0; i < states.length(); i++) {
            JSONObject state = states.getJSONObject(i);
            JSONArray regions = state.getJSONArray("regions");

            for (int j = 0; j < regions.length(); j++) {
                JSONObject region = regions.getJSONObject(j);
                JSONArray neighborhood = region.getJSONArray("neighborhood");

                for (int k = 0; k < neighborhood.length(); k++) {
                    String regionNeighborhoodName = neighborhood.getString(k);

                    contentValues[regionNeighborhoodId] = new ContentValues();
                    contentValues[regionNeighborhoodId].put(Contract.RegionNeighborhood.ID_REGION, regionId);
                    contentValues[regionNeighborhoodId].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, regionNames.indexOf(regionNeighborhoodName)+1);
                    contentValues[regionNeighborhoodId].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

                    regionNeighborhoodId++;
                }
                regionId++;
            }
        }

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                getContentResolver().bulkInsert(Contract.RegionNeighborhood.CONTENT_URI,(ContentValues[]) params);
                return null;
            }
        }.execute(contentValues);
    }

    private void insertToTableProblems(JSONObject obj, String language) throws JSONException {
        JSONArray states = obj.getJSONArray(language);
        int problemCount = 0;

        for (int i = 0; i < states.length(); i++) {
            JSONObject state = states.getJSONObject(i);
            JSONArray regions = state.getJSONArray("regions");

            for (int j = 0; j < regions.length(); j++) {
                JSONObject region = regions.getJSONObject(j);
                problemCount += region.getJSONArray("problems").length();
            }
        }

        ContentValues[] contentValues = new ContentValues[problemCount];
        int idRegion = 1,
                idProblem = 0;
        for (int i = 0; i < states.length(); i++) {
            JSONObject state = states.getJSONObject(i);
            JSONArray regions = state.getJSONArray("regions");

            for (int j = 0; j < regions.length(); j++) {
                JSONObject region = regions.getJSONObject(j);
                JSONArray problems = region.getJSONArray("problems");

                for (int k = 0; k < problems.length(); k++) {
                    JSONObject problem = problems.getJSONObject(k);

                    contentValues[idProblem] = new ContentValues();
                    contentValues[idProblem].put(Contract.Problems.PROBLEM_NAME,problem.getString("problem_name"));
                    contentValues[idProblem].put(Contract.Problems.ID_REGION,idRegion);
                    contentValues[idProblem].put(Contract.Problems.ACTION_TYPE,problem.getString("action_type"));
                    contentValues[idProblem].put(Contract.Problems.EFFECT,problem.getDouble("effect"));
                    contentValues[idProblem].put(Contract.Problems.SOLVABILITY,problem.getDouble("solvability"));
                    contentValues[idProblem].put(Contract.Problems.CHANGED,System.currentTimeMillis());

                    idProblem++;
                }

                idRegion++;
            }
        }

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                getContentResolver().bulkInsert(Contract.Problems.CONTENT_URI,(ContentValues[]) params);
                return null;
            }
        }.execute(contentValues);
    }

    private void insertToTableCandidate(JSONObject obj, String language) throws JSONException {
        JSONArray states = obj.getJSONArray(language);
        ArrayList<String> regionNames = new ArrayList<>();
        int candidateCount = 0;

        for (int i = 0; i < states.length(); i++) {
            JSONObject state = states.getJSONObject(i);
            JSONArray regions = state.getJSONArray("regions");

            for (int j = 0; j < regions.length(); j++) {
                JSONObject region = regions.getJSONObject(j);
                regionNames.add(region.getString("region_name"));
            }
            candidateCount += state.getJSONArray("candidates").length();
        }

        ContentValues[] contentValues = new ContentValues[candidateCount];
        int indexCandidate = 0;
        for (int i = 0; i < states.length(); i++) {
            JSONObject state = states.getJSONObject(i);
            JSONArray candidates = state.getJSONArray("candidates");
            for (int j = 0; j < candidates.length(); j++) {
                JSONObject candidate = candidates.getJSONObject(j);

                contentValues[indexCandidate] = new ContentValues();
                contentValues[indexCandidate].put(Contract.Candidate.NAME_SURNAME, candidate.getString("name_surname"));
                contentValues[indexCandidate].put(Contract.Candidate.ID_BIRTH_REGION, regionNames.indexOf(candidate.getString("birth_region")) + 1);
                contentValues[indexCandidate].put(Contract.Candidate.CHARISMA, candidate.getInt("charisma"));
                contentValues[indexCandidate].put(Contract.Candidate.REPUTATION, candidate.getInt("reputation"));
                contentValues[indexCandidate].put(Contract.Candidate.MONEY, candidate.getInt("money"));
                contentValues[indexCandidate].put(Contract.Candidate.CREATED_BY_USER, 0);
                contentValues[indexCandidate].put(Contract.Candidate.CHANGED, System.currentTimeMillis());

                indexCandidate++;
            }
        }

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                getContentResolver().bulkInsert(Contract.Candidate.CONTENT_URI,(ContentValues[]) params);
                return null;
            }
        }.execute(contentValues);
    }

    private void insertToTableSettings(JSONObject obj) throws JSONException {
        JSONObject settings = obj.getJSONObject("settings");

        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Settings.NUMBER_OF_ELECTIONS_WEEK,settings.getInt("count_of_election_week"));
        contentValues.put(Contract.Settings.FIRST_STEP_IN_GAME,settings.getInt("first_step_in_game"));
        contentValues.put(Contract.Settings.RECOMMENDATION,settings.getInt("recommendation"));
        contentValues.put(Contract.Settings.CHANGED,System.currentTimeMillis());

        @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {};
        asyncQueryHandler.startInsert(0,null, Contract.Settings.CONTENT_URI,contentValues);
    }

    private void setSettingsDialog() {
        final Dialog dialog = tf.getDialog(MainMenuActivity.this,true,R.layout.dialog_settings);
        (dialog.findViewById(R.id.constraintLayout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tf.playMusic(getApplicationContext());
            }
        });

        numberOfElectionWeek = new ChangeVariable(0);
        firstStep = new ChangeVariable(0);
        recommendation = new ChangeVariable(0);
        final TextView textViewNumberOfElectionWeek = dialog.findViewById(R.id.textViewNumberOfElectionWeek);
        final TextView textViewFirstStep = dialog.findViewById(R.id.textViewFirstStep);
        final TextView textViewRecommendation = dialog.findViewById(R.id.textViewRecommendation);
        final Switch switchFirstStep = dialog.findViewById(R.id.switchFirstStep);
        final Switch switchRecommendation = dialog.findViewById(R.id.switchRecommendation);
        @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
                cursor.moveToFirst();
                final int id = cursor.getInt(Contract.Settings.COLUMN_INDEX_ID);
                numberOfElectionWeek = new ChangeVariable(cursor.getInt(Contract.Settings.COLUMN_INDEX_NUMBER_OF_ELECTIONS_WEEK));
                firstStep = new ChangeVariable(cursor.getInt(Contract.Settings.COLUMN_INDEX_FIRST_STEP_IN_GAME));
                recommendation = new ChangeVariable(cursor.getInt(Contract.Settings.COLUMN_INDEX_RECOMMENDATION));


                numberOfElectionWeek.setListener(new ChangeVariable.ChangeListener() {
                    @Override
                    public void onChange() {
                        textViewNumberOfElectionWeek.setText(String.valueOf(numberOfElectionWeek.getIntValue()));
                    }
                });
                numberOfElectionWeek.callListener();

                firstStep.setListener(new ChangeVariable.ChangeListener() {
                    @Override
                    public void onChange() {
                        if (firstStep.getIntValue() == 0)
                            textViewFirstStep.setText(getResources().getString(R.string.first_step_opponent));
                        else if (firstStep.getIntValue() == 1)
                            textViewFirstStep.setText(getResources().getString(R.string.first_step_candidate));
                    }
                });
                firstStep.callListener();
                if (firstStep.getIntValue() == 0)
                    switchFirstStep.setChecked(true);
                else if (firstStep.getIntValue() == 1)
                    switchFirstStep.setChecked(false);

                recommendation.setListener(new ChangeVariable.ChangeListener() {
                    @Override
                    public void onChange() {
                        if (recommendation.getIntValue() == 1)
                            textViewRecommendation.setText(getResources().getString(R.string.helps_show));
                        else
                            textViewRecommendation.setText(getResources().getString(R.string.helps_not_show));
                    }
                });
                recommendation.callListener();
                if (recommendation.getIntValue() == 1)
                    switchRecommendation.setChecked(true);
                else
                    switchRecommendation.setChecked(false);


                (dialog.findViewById(R.id.imageButtonAdd)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tf.playMusic(getApplicationContext());
                        int newNumber = numberOfElectionWeek.getIntValue() + 1;
                        if (newNumber <= 30) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(Contract.Settings.NUMBER_OF_ELECTIONS_WEEK, newNumber);
                            Uri settingsUri = ContentUris.withAppendedId(Contract.Settings.CONTENT_URI, id);

                            @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
                            };
                            asyncQueryHandler.startUpdate(0, null, settingsUri, contentValues, null, null);

                            numberOfElectionWeek.setValue(newNumber);
                        }
                    }
                });
                (dialog.findViewById(R.id.imageButtonRemove)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tf.playMusic(getApplicationContext());
                        int newNumber = numberOfElectionWeek.getIntValue() - 1;
                        if (newNumber >= 2) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(Contract.Settings.NUMBER_OF_ELECTIONS_WEEK, newNumber);
                            Uri settingsUri = ContentUris.withAppendedId(Contract.Settings.CONTENT_URI, id);

                            @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {};
                            asyncQueryHandler.startUpdate(0, null, settingsUri, contentValues, null, null);

                            numberOfElectionWeek.setValue(newNumber);
                        }
                    }
                });
                switchFirstStep.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tf.playMusic(getApplicationContext());
                        Switch s = (Switch)v;
                        if (s.isChecked()) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(Contract.Settings.FIRST_STEP_IN_GAME,0);
                            Uri settingsUri = ContentUris.withAppendedId(Contract.Settings.CONTENT_URI,id);

                            @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {};
                            asyncQueryHandler.startUpdate(0,null,settingsUri,contentValues,null,null);

                            firstStep.setValue(0);
                        }
                        else {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(Contract.Settings.FIRST_STEP_IN_GAME,1);
                            Uri settingsUri = ContentUris.withAppendedId(Contract.Settings.CONTENT_URI,id);

                            @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {};
                            asyncQueryHandler.startUpdate(0,null,settingsUri,contentValues,null,null);

                            firstStep.setValue(1);
                        }
                    }
                });
                switchRecommendation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tf.playMusic(getApplicationContext());
                        Switch s = (Switch)v;
                        if (s.isChecked()) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(Contract.Settings.RECOMMENDATION,1);
                            Uri settingsUri = ContentUris.withAppendedId(Contract.Settings.CONTENT_URI,id);

                            @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {};
                            asyncQueryHandler.startUpdate(0,null,settingsUri,contentValues,null,null);

                            recommendation.setValue(1);
                        }
                        else {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(Contract.Settings.RECOMMENDATION,0);
                            Uri settingsUri = ContentUris.withAppendedId(Contract.Settings.CONTENT_URI,id);

                            @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {};
                            asyncQueryHandler.startUpdate(0,null,settingsUri,contentValues,null,null);

                            recommendation.setValue(0);
                        }
                    }
                });
                dialog.show();
            }
        };
        asyncQueryHandler.startQuery(0,null, Contract.Settings.CONTENT_URI,null,null,null,null);
    }

    private ArrayList<SavedGame> setArrayListInSavedGameDialog(Bundle obj) {
        ArrayList<SavedGame> games = new ArrayList<>();
        int count = obj.getInt("count");
        for (int i = 0; i < count; i++) {
            Bundle row = obj.getBundle("row" + String.valueOf(i));
            games.add(new SavedGame(row.getInt("id"), row.getString("stateName"),
                    row.getString("characterNameSurname"),
                    row.getString("opponentNameSurname"),
                    row.getLong("proCharacter"), row.getLong("undecided"),
                    row.getLong("proOpponent"), row.getLong("changed")));
        }
        return games;
    }

    private void setSavedGameDialog() {
        Bundle obj = getContentResolver().call(Contract.State.CONTENT_URI,"return informations for saved games",null,null);

        if (obj != null) {
            final Dialog dialog = tf.getDialog(MainMenuActivity.this,true,R.layout.dialog_saved_game);
            dialog.show();
            (dialog.findViewById(R.id.constraintLayout)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tf.playMusic(getApplicationContext());
                }
            });

            ListView listview = dialog.findViewById(R.id.listviewSavedGames);
            ArrayList<SavedGame> games = setArrayListInSavedGameDialog(obj);

            final SavedGameAdapter adapter = new SavedGameAdapter(games, getApplicationContext());
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, final View itemView, int i, long l) {
                    final Dialog dialog1 = tf.getDialogSimpleQuestion(MainMenuActivity.this,true,R.layout.dialog_simple_question,
                            getResources().getString(R.string.saved_game),getResources().getString(R.string.sure_you_want_play_this_saved_game),
                            getResources().getString(R.string.yes),getResources().getString(R.string.no));
                    dialog1.show();

                    (dialog1.findViewById(R.id.buttonPositiveAnswer)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog1.dismiss();
                            dialog.dismiss();
                            Intent intent = new Intent(MainMenuActivity.this,GameActivity.class);
                            intent.putExtra("idSavedGame",Integer.parseInt(itemView.getTag().toString()));
                            startActivity(intent);
                        }
                    });
                    (dialog1.findViewById(R.id.buttonNegativeButton)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog1.dismiss();
                        }
                    });
                }
            });
            listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, final View itemView, int i, long l) {
                    final Dialog dialog1 = tf.getDialogSimpleQuestion(MainMenuActivity.this,true,R.layout.dialog_simple_question,
                            getResources().getString(R.string.delete_saved_game),getResources().getString(R.string.sure_you_want_delete_this_saved_game),
                            getResources().getString(R.string.yes),getResources().getString(R.string.no));
                    dialog1.show();

                    (dialog1.findViewById(R.id.buttonPositiveAnswer)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int idSavedGame = Integer.parseInt(itemView.getTag().toString());
                            @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
                                @Override
                                protected void onDeleteComplete(int token, Object cookie, int result) {
                                    super.onDeleteComplete(token, cookie, result);

                                    Bundle obj = getContentResolver().call(Contract.State.CONTENT_URI,"return informations for saved games",null,null);
                                    if (obj != null) {
                                        adapter.updateData(setArrayListInSavedGameDialog(obj));
                                        dialog1.dismiss();
                                    }
                                    else {
                                        dialog1.dismiss();
                                        dialog.dismiss();
                                    }
                                }
                            };
                            Uri uri = ContentUris.withAppendedId(Contract.SavedGame.CONTENT_URI, idSavedGame);
                            asyncQueryHandler.startDelete(0,null,uri,null,null);

                        }
                    });
                    (dialog1.findViewById(R.id.buttonNegativeButton)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog1.dismiss();
                        }
                    });

                    return true;
                }
            });
        }
        else {
            Dialog dialog = tf.getDialogSimpleNotice(MainMenuActivity.this,true,R.layout.dialog_simple_notice,
                    getResources().getString(R.string.save_game),getResources().getString(R.string.any_saved_game));
            dialog.show();
        }
    }

}
