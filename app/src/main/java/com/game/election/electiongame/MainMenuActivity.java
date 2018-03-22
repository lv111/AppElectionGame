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

import com.game.election.electiongame.adapter.SavedGameAdapter;
import com.game.election.electiongame.classes.ChangeVariable;
import com.game.election.electiongame.classes.SavedGame;
import com.game.election.electiongame.classes.TogetherFunctions;
import com.game.election.electiongame.provider.Contract;

import java.util.ArrayList;


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
                    insertToTableState();
                    insertToTableRegion();
                    insertToTableRegionNeighborhood();
                    insertToTableProblems();
                    insertToTableCharacter();
                }
            }
        };
        asyncQueryHandler.startQuery(0,null,Contract.State.CONTENT_URI,null,null,null,null);

        asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
                if (cursor.getCount() == 0) {
                    insertToTableSettings();
                }
            }
        };
        asyncQueryHandler.startQuery(0,null, Contract.Settings.CONTENT_URI,null,null,null,null);

    }

    private void insertToTableState() {
        ContentValues[] contentValues = new ContentValues[2];
        contentValues[0] = new ContentValues();
        contentValues[0].put(Contract.State._ID,1);
        contentValues[0].put(Contract.State.STATE_NAME,"Slovenská republika");
        contentValues[0].put(Contract.State.POPULATION,5415949);
        contentValues[0].put(Contract.State.NUMBER_OF_REGIONS,8);
        contentValues[0].put(Contract.State.CHANGED,System.currentTimeMillis());

        contentValues[1] = new ContentValues();
        contentValues[1].put(Contract.State._ID,2);
        contentValues[1].put(Contract.State.STATE_NAME,"Česká republika");
        contentValues[1].put(Contract.State.POPULATION,10513800);
        contentValues[1].put(Contract.State.NUMBER_OF_REGIONS,14);
        contentValues[1].put(Contract.State.CHANGED,System.currentTimeMillis());

         new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                getContentResolver().bulkInsert(Contract.State.CONTENT_URI,(ContentValues[]) params);
                return null;
            }
        }.execute(contentValues);
    }

    private void insertToTableRegion() {
        ContentValues[] contentValues = new ContentValues[22];
        contentValues[0] = new ContentValues();
        contentValues[0].put(Contract.Region._ID,1);
        contentValues[0].put(Contract.Region.REGION_NAME,"Bratislavský kraj");
        contentValues[0].put(Contract.Region.ABBREVIATION,"BA");
        contentValues[0].put(Contract.Region.ID_STATE,1);
        contentValues[0].put(Contract.Region.POPULATION,641892);
        contentValues[0].put(Contract.Region.CHANGED,System.currentTimeMillis());

        contentValues[1] = new ContentValues();
        contentValues[1].put(Contract.Region._ID,2);
        contentValues[1].put(Contract.Region.REGION_NAME,"Trnavský kraj");
        contentValues[1].put(Contract.Region.ABBREVIATION,"TT");
        contentValues[1].put(Contract.Region.ID_STATE,1);
        contentValues[1].put(Contract.Region.POPULATION,561156);
        contentValues[1].put(Contract.Region.CHANGED,System.currentTimeMillis());

        contentValues[2] = new ContentValues();
        contentValues[2].put(Contract.Region._ID,3);
        contentValues[2].put(Contract.Region.REGION_NAME,"Trenčiansky kraj");
        contentValues[2].put(Contract.Region.ABBREVIATION,"TN");
        contentValues[2].put(Contract.Region.ID_STATE,1);
        contentValues[2].put(Contract.Region.POPULATION,588816);
        contentValues[2].put(Contract.Region.CHANGED,System.currentTimeMillis());

        contentValues[3] = new ContentValues();
        contentValues[3].put(Contract.Region._ID,4);
        contentValues[3].put(Contract.Region.REGION_NAME,"Nitriansky kraj");
        contentValues[3].put(Contract.Region.ABBREVIATION,"NT");
        contentValues[3].put(Contract.Region.ID_STATE,1);
        contentValues[3].put(Contract.Region.POPULATION,680779);
        contentValues[3].put(Contract.Region.CHANGED,System.currentTimeMillis());

        contentValues[4] = new ContentValues();
        contentValues[4].put(Contract.Region._ID,5);
        contentValues[4].put(Contract.Region.REGION_NAME,"Žilinský kraj");
        contentValues[4].put(Contract.Region.ABBREVIATION,"ZA");
        contentValues[4].put(Contract.Region.ID_STATE,1);
        contentValues[4].put(Contract.Region.POPULATION,690778);
        contentValues[4].put(Contract.Region.CHANGED,System.currentTimeMillis());

        contentValues[5] = new ContentValues();
        contentValues[5].put(Contract.Region._ID,6);
        contentValues[5].put(Contract.Region.REGION_NAME,"Banskobystrický kraj");
        contentValues[5].put(Contract.Region.ABBREVIATION,"BB");
        contentValues[5].put(Contract.Region.ID_STATE,1);
        contentValues[5].put(Contract.Region.POPULATION,651509);
        contentValues[5].put(Contract.Region.CHANGED,System.currentTimeMillis());

        contentValues[6] = new ContentValues();
        contentValues[6].put(Contract.Region._ID,7);
        contentValues[6].put(Contract.Region.REGION_NAME,"Prešovský kraj");
        contentValues[6].put(Contract.Region.ABBREVIATION,"PO");
        contentValues[6].put(Contract.Region.ID_STATE,1);
        contentValues[6].put(Contract.Region.POPULATION,822310);
        contentValues[6].put(Contract.Region.CHANGED,System.currentTimeMillis());

        contentValues[7] = new ContentValues();
        contentValues[7].put(Contract.Region._ID,8);
        contentValues[7].put(Contract.Region.REGION_NAME,"Košický kraj");
        contentValues[7].put(Contract.Region.ABBREVIATION,"KE");
        contentValues[7].put(Contract.Region.ID_STATE,1);
        contentValues[7].put(Contract.Region.POPULATION,798103);
        contentValues[7].put(Contract.Region.CHANGED,System.currentTimeMillis());

        contentValues[8] = new ContentValues();
        contentValues[8].put(Contract.Region._ID,9);
        contentValues[8].put(Contract.Region.REGION_NAME,"Jihočeský kraj");
        contentValues[8].put(Contract.Region.ABBREVIATION,"JHČ");
        contentValues[8].put(Contract.Region.ID_STATE,2);
        contentValues[8].put(Contract.Region.POPULATION,637723);
        contentValues[8].put(Contract.Region.CHANGED,System.currentTimeMillis());

        contentValues[9] = new ContentValues();
        contentValues[9].put(Contract.Region._ID,10);
        contentValues[9].put(Contract.Region.REGION_NAME,"Jihomoravský kraj");
        contentValues[9].put(Contract.Region.ABBREVIATION,"JHM");
        contentValues[9].put(Contract.Region.ID_STATE,2);
        contentValues[9].put(Contract.Region.POPULATION,1152819);
        contentValues[9].put(Contract.Region.CHANGED,System.currentTimeMillis());

        contentValues[10] = new ContentValues();
        contentValues[10].put(Contract.Region._ID,11);
        contentValues[10].put(Contract.Region.REGION_NAME,"Karlovarský kraj");
        contentValues[10].put(Contract.Region.ABBREVIATION,"KVK");
        contentValues[10].put(Contract.Region.ID_STATE,2);
        contentValues[10].put(Contract.Region.POPULATION,304602);
        contentValues[10].put(Contract.Region.CHANGED,System.currentTimeMillis());

        contentValues[11] = new ContentValues();
        contentValues[11].put(Contract.Region._ID,12);
        contentValues[11].put(Contract.Region.REGION_NAME,"Královéhradecký kraj");
        contentValues[11].put(Contract.Region.ABBREVIATION,"HKK");
        contentValues[11].put(Contract.Region.ID_STATE,2);
        contentValues[11].put(Contract.Region.POPULATION,547903);
        contentValues[11].put(Contract.Region.CHANGED,System.currentTimeMillis());

        contentValues[12] = new ContentValues();
        contentValues[12].put(Contract.Region._ID,13);
        contentValues[12].put(Contract.Region.REGION_NAME,"Liberecký kraj");
        contentValues[12].put(Contract.Region.ABBREVIATION,"LBK");
        contentValues[12].put(Contract.Region.ID_STATE,2);
        contentValues[12].put(Contract.Region.POPULATION,433948);
        contentValues[12].put(Contract.Region.CHANGED,System.currentTimeMillis());

        contentValues[13] = new ContentValues();
        contentValues[13].put(Contract.Region._ID,14);
        contentValues[13].put(Contract.Region.REGION_NAME,"Moravskoslezský kraj");
        contentValues[13].put(Contract.Region.ABBREVIATION,"MSK");
        contentValues[13].put(Contract.Region.ID_STATE,2);
        contentValues[13].put(Contract.Region.POPULATION,1244837);
        contentValues[13].put(Contract.Region.CHANGED,System.currentTimeMillis());

        contentValues[14] = new ContentValues();
        contentValues[14].put(Contract.Region._ID,15);
        contentValues[14].put(Contract.Region.REGION_NAME,"Olomoucký kraj");
        contentValues[14].put(Contract.Region.ABBREVIATION,"OLK");
        contentValues[14].put(Contract.Region.ID_STATE,2);
        contentValues[14].put(Contract.Region.POPULATION,640410);
        contentValues[14].put(Contract.Region.CHANGED,System.currentTimeMillis());

        contentValues[15] = new ContentValues();
        contentValues[15].put(Contract.Region._ID,16);
        contentValues[15].put(Contract.Region.REGION_NAME,"Pardubický kraj");
        contentValues[15].put(Contract.Region.ABBREVIATION,"PAK");
        contentValues[15].put(Contract.Region.ID_STATE,2);
        contentValues[15].put(Contract.Region.POPULATION,520584);
        contentValues[15].put(Contract.Region.CHANGED,System.currentTimeMillis());

        contentValues[16] = new ContentValues();
        contentValues[16].put(Contract.Region._ID,17);
        contentValues[16].put(Contract.Region.REGION_NAME,"Plzeňský kraj");
        contentValues[16].put(Contract.Region.ABBREVIATION,"PLK");
        contentValues[16].put(Contract.Region.ID_STATE,2);
        contentValues[16].put(Contract.Region.POPULATION,571831);
        contentValues[16].put(Contract.Region.CHANGED,System.currentTimeMillis());

        contentValues[17] = new ContentValues();
        contentValues[17].put(Contract.Region._ID,18);
        contentValues[17].put(Contract.Region.REGION_NAME,"Středočeský kraj");
        contentValues[17].put(Contract.Region.ABBREVIATION,"STČ");
        contentValues[17].put(Contract.Region.ID_STATE,2);
        contentValues[17].put(Contract.Region.POPULATION,1256850);
        contentValues[17].put(Contract.Region.CHANGED,System.currentTimeMillis());

        contentValues[18] = new ContentValues();
        contentValues[18].put(Contract.Region._ID,19);
        contentValues[18].put(Contract.Region.REGION_NAME,"Praha");
        contentValues[18].put(Contract.Region.ABBREVIATION,"PHA");
        contentValues[18].put(Contract.Region.ID_STATE,2);
        contentValues[18].put(Contract.Region.POPULATION,1251072);
        contentValues[18].put(Contract.Region.CHANGED,System.currentTimeMillis());

        contentValues[19] = new ContentValues();
        contentValues[19].put(Contract.Region._ID,20);
        contentValues[19].put(Contract.Region.REGION_NAME,"Ústecký kraj");
        contentValues[19].put(Contract.Region.ABBREVIATION,"ULK");
        contentValues[19].put(Contract.Region.ID_STATE,2);
        contentValues[19].put(Contract.Region.POPULATION,835814);
        contentValues[19].put(Contract.Region.CHANGED,System.currentTimeMillis());

        contentValues[20] = new ContentValues();
        contentValues[20].put(Contract.Region._ID,21);
        contentValues[20].put(Contract.Region.REGION_NAME,"Kraj Vysošina");
        contentValues[20].put(Contract.Region.ABBREVIATION,"VYS");
        contentValues[20].put(Contract.Region.ID_STATE,2);
        contentValues[20].put(Contract.Region.POPULATION,513677);
        contentValues[20].put(Contract.Region.CHANGED,System.currentTimeMillis());

        contentValues[21] = new ContentValues();
        contentValues[21].put(Contract.Region._ID,22);
        contentValues[21].put(Contract.Region.REGION_NAME,"Zlínsky kraj");
        contentValues[21].put(Contract.Region.ABBREVIATION,"ZLK");
        contentValues[21].put(Contract.Region.ID_STATE,2);
        contentValues[21].put(Contract.Region.POPULATION,590527);
        contentValues[21].put(Contract.Region.CHANGED,System.currentTimeMillis());

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                getContentResolver().bulkInsert(Contract.Region.CONTENT_URI,(ContentValues[]) params);
                return null;
            }
        }.execute(contentValues);
    }

    private void insertToTableRegionNeighborhood() {
        ContentValues[] contentValues = new ContentValues[76];

        contentValues[0] = new ContentValues();
        contentValues[0].put(Contract.RegionNeighborhood._ID, 1);
        contentValues[0].put(Contract.RegionNeighborhood.ID_REGION, 1);
        contentValues[0].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 2);
        contentValues[0].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[1] = new ContentValues();
        contentValues[1].put(Contract.RegionNeighborhood._ID, 2);
        contentValues[1].put(Contract.RegionNeighborhood.ID_REGION, 2);
        contentValues[1].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 1);
        contentValues[1].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[2] = new ContentValues();
        contentValues[2].put(Contract.RegionNeighborhood._ID, 3);
        contentValues[2].put(Contract.RegionNeighborhood.ID_REGION, 2);
        contentValues[2].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 3);
        contentValues[2].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[3] = new ContentValues();
        contentValues[3].put(Contract.RegionNeighborhood._ID, 4);
        contentValues[3].put(Contract.RegionNeighborhood.ID_REGION, 2);
        contentValues[3].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 4);
        contentValues[3].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[4] = new ContentValues();
        contentValues[4].put(Contract.RegionNeighborhood._ID, 5);
        contentValues[4].put(Contract.RegionNeighborhood.ID_REGION, 3);
        contentValues[4].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 2);
        contentValues[4].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[5] = new ContentValues();
        contentValues[5].put(Contract.RegionNeighborhood._ID, 6);
        contentValues[5].put(Contract.RegionNeighborhood.ID_REGION, 3);
        contentValues[5].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 4);
        contentValues[5].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[6] = new ContentValues();
        contentValues[6].put(Contract.RegionNeighborhood._ID, 7);
        contentValues[6].put(Contract.RegionNeighborhood.ID_REGION, 3);
        contentValues[6].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 5);
        contentValues[6].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[7] = new ContentValues();
        contentValues[7].put(Contract.RegionNeighborhood._ID, 8);
        contentValues[7].put(Contract.RegionNeighborhood.ID_REGION, 3);
        contentValues[7].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 6);
        contentValues[7].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[8] = new ContentValues();
        contentValues[8].put(Contract.RegionNeighborhood._ID, 9);
        contentValues[8].put(Contract.RegionNeighborhood.ID_REGION, 4);
        contentValues[8].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 2);
        contentValues[8].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[9] = new ContentValues();
        contentValues[9].put(Contract.RegionNeighborhood._ID, 10);
        contentValues[9].put(Contract.RegionNeighborhood.ID_REGION, 4);
        contentValues[9].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 3);
        contentValues[9].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[10] = new ContentValues();
        contentValues[10].put(Contract.RegionNeighborhood._ID, 11);
        contentValues[10].put(Contract.RegionNeighborhood.ID_REGION, 4);
        contentValues[10].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 6);
        contentValues[10].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[11] = new ContentValues();
        contentValues[11].put(Contract.RegionNeighborhood._ID, 12);
        contentValues[11].put(Contract.RegionNeighborhood.ID_REGION, 5);
        contentValues[11].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 3);
        contentValues[11].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[12] = new ContentValues();
        contentValues[12].put(Contract.RegionNeighborhood._ID, 13);
        contentValues[12].put(Contract.RegionNeighborhood.ID_REGION, 5);
        contentValues[12].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 6);
        contentValues[12].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[13] = new ContentValues();
        contentValues[13].put(Contract.RegionNeighborhood._ID, 14);
        contentValues[13].put(Contract.RegionNeighborhood.ID_REGION, 5);
        contentValues[13].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 7);
        contentValues[13].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[14] = new ContentValues();
        contentValues[14].put(Contract.RegionNeighborhood._ID, 15);
        contentValues[14].put(Contract.RegionNeighborhood.ID_REGION, 6);
        contentValues[14].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 3);
        contentValues[14].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[15] = new ContentValues();
        contentValues[15].put(Contract.RegionNeighborhood._ID, 16);
        contentValues[15].put(Contract.RegionNeighborhood.ID_REGION, 6);
        contentValues[15].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 4);
        contentValues[15].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[16] = new ContentValues();
        contentValues[16].put(Contract.RegionNeighborhood._ID, 17);
        contentValues[16].put(Contract.RegionNeighborhood.ID_REGION, 6);
        contentValues[16].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 5);
        contentValues[16].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[17] = new ContentValues();
        contentValues[17].put(Contract.RegionNeighborhood._ID, 18);
        contentValues[17].put(Contract.RegionNeighborhood.ID_REGION, 6);
        contentValues[17].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 7);
        contentValues[17].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[18] = new ContentValues();
        contentValues[18].put(Contract.RegionNeighborhood._ID, 19);
        contentValues[18].put(Contract.RegionNeighborhood.ID_REGION, 6);
        contentValues[18].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 8);
        contentValues[18].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[19] = new ContentValues();
        contentValues[19].put(Contract.RegionNeighborhood._ID, 20);
        contentValues[19].put(Contract.RegionNeighborhood.ID_REGION, 7);
        contentValues[19].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 5);
        contentValues[19].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[20] = new ContentValues();
        contentValues[20].put(Contract.RegionNeighborhood._ID, 21);
        contentValues[20].put(Contract.RegionNeighborhood.ID_REGION, 7);
        contentValues[20].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 6);
        contentValues[20].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[21] = new ContentValues();
        contentValues[21].put(Contract.RegionNeighborhood._ID, 22);
        contentValues[21].put(Contract.RegionNeighborhood.ID_REGION, 7);
        contentValues[21].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 8);
        contentValues[21].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[22] = new ContentValues();
        contentValues[22].put(Contract.RegionNeighborhood._ID, 23);
        contentValues[22].put(Contract.RegionNeighborhood.ID_REGION, 8);
        contentValues[22].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 6);
        contentValues[22].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[23] = new ContentValues();
        contentValues[23].put(Contract.RegionNeighborhood._ID, 24);
        contentValues[23].put(Contract.RegionNeighborhood.ID_REGION, 8);
        contentValues[23].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 7);
        contentValues[23].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[24] = new ContentValues();
        contentValues[24].put(Contract.RegionNeighborhood._ID, 25);
        contentValues[24].put(Contract.RegionNeighborhood.ID_REGION, 9);
        contentValues[24].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 10);
        contentValues[24].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[25] = new ContentValues();
        contentValues[25].put(Contract.RegionNeighborhood._ID, 26);
        contentValues[25].put(Contract.RegionNeighborhood.ID_REGION, 9);
        contentValues[25].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 17);
        contentValues[25].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[26] = new ContentValues();
        contentValues[26].put(Contract.RegionNeighborhood._ID, 27);
        contentValues[26].put(Contract.RegionNeighborhood.ID_REGION, 9);
        contentValues[26].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 19);
        contentValues[26].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[27] = new ContentValues();
        contentValues[27].put(Contract.RegionNeighborhood._ID, 28);
        contentValues[27].put(Contract.RegionNeighborhood.ID_REGION, 9);
        contentValues[27].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 21);
        contentValues[27].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[28] = new ContentValues();
        contentValues[28].put(Contract.RegionNeighborhood._ID, 29);
        contentValues[28].put(Contract.RegionNeighborhood.ID_REGION, 10);
        contentValues[28].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 9);
        contentValues[28].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[29] = new ContentValues();
        contentValues[29].put(Contract.RegionNeighborhood._ID, 30);
        contentValues[29].put(Contract.RegionNeighborhood.ID_REGION, 10);
        contentValues[29].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 15);
        contentValues[29].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[30] = new ContentValues();
        contentValues[30].put(Contract.RegionNeighborhood._ID, 31);
        contentValues[30].put(Contract.RegionNeighborhood.ID_REGION, 10);
        contentValues[30].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 16);
        contentValues[30].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[31] = new ContentValues();
        contentValues[31].put(Contract.RegionNeighborhood._ID, 32);
        contentValues[31].put(Contract.RegionNeighborhood.ID_REGION, 10);
        contentValues[31].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 21);
        contentValues[31].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[32] = new ContentValues();
        contentValues[32].put(Contract.RegionNeighborhood._ID, 33);
        contentValues[32].put(Contract.RegionNeighborhood.ID_REGION, 10);
        contentValues[32].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 22);
        contentValues[32].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[33] = new ContentValues();
        contentValues[33].put(Contract.RegionNeighborhood._ID, 34);
        contentValues[33].put(Contract.RegionNeighborhood.ID_REGION, 11);
        contentValues[33].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 17);
        contentValues[33].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[34] = new ContentValues();
        contentValues[34].put(Contract.RegionNeighborhood._ID, 35);
        contentValues[34].put(Contract.RegionNeighborhood.ID_REGION, 11);
        contentValues[34].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 20);
        contentValues[34].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[35] = new ContentValues();
        contentValues[35].put(Contract.RegionNeighborhood._ID, 36);
        contentValues[35].put(Contract.RegionNeighborhood.ID_REGION, 12);
        contentValues[35].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 13);
        contentValues[35].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[36] = new ContentValues();
        contentValues[36].put(Contract.RegionNeighborhood._ID, 37);
        contentValues[36].put(Contract.RegionNeighborhood.ID_REGION, 12);
        contentValues[36].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 16);
        contentValues[36].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[37] = new ContentValues();
        contentValues[37].put(Contract.RegionNeighborhood._ID, 38);
        contentValues[37].put(Contract.RegionNeighborhood.ID_REGION, 12);
        contentValues[37].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 19);
        contentValues[37].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[38] = new ContentValues();
        contentValues[38].put(Contract.RegionNeighborhood._ID, 39);
        contentValues[38].put(Contract.RegionNeighborhood.ID_REGION, 13);
        contentValues[38].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 12);
        contentValues[38].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[39] = new ContentValues();
        contentValues[39].put(Contract.RegionNeighborhood._ID, 40);
        contentValues[39].put(Contract.RegionNeighborhood.ID_REGION, 13);
        contentValues[39].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 19);
        contentValues[39].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[40] = new ContentValues();
        contentValues[40].put(Contract.RegionNeighborhood._ID, 41);
        contentValues[40].put(Contract.RegionNeighborhood.ID_REGION, 13);
        contentValues[40].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 20);
        contentValues[40].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[41] = new ContentValues();
        contentValues[41].put(Contract.RegionNeighborhood._ID, 422);
        contentValues[41].put(Contract.RegionNeighborhood.ID_REGION, 14);
        contentValues[41].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 15);
        contentValues[41].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[42] = new ContentValues();
        contentValues[42].put(Contract.RegionNeighborhood._ID, 43);
        contentValues[42].put(Contract.RegionNeighborhood.ID_REGION, 14);
        contentValues[42].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 22);
        contentValues[42].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[43] = new ContentValues();
        contentValues[43].put(Contract.RegionNeighborhood._ID, 44);
        contentValues[43].put(Contract.RegionNeighborhood.ID_REGION, 15);
        contentValues[43].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 10);
        contentValues[43].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[44] = new ContentValues();
        contentValues[44].put(Contract.RegionNeighborhood._ID, 45);
        contentValues[44].put(Contract.RegionNeighborhood.ID_REGION, 15);
        contentValues[44].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 14);
        contentValues[44].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[45] = new ContentValues();
        contentValues[45].put(Contract.RegionNeighborhood._ID, 46);
        contentValues[45].put(Contract.RegionNeighborhood.ID_REGION, 15);
        contentValues[45].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 16);
        contentValues[45].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[46] = new ContentValues();
        contentValues[46].put(Contract.RegionNeighborhood._ID, 47);
        contentValues[46].put(Contract.RegionNeighborhood.ID_REGION, 15);
        contentValues[46].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 22);
        contentValues[46].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[47] = new ContentValues();
        contentValues[47].put(Contract.RegionNeighborhood._ID, 48);
        contentValues[47].put(Contract.RegionNeighborhood.ID_REGION, 16);
        contentValues[47].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 10);
        contentValues[47].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[48] = new ContentValues();
        contentValues[48].put(Contract.RegionNeighborhood._ID, 49);
        contentValues[48].put(Contract.RegionNeighborhood.ID_REGION, 16);
        contentValues[48].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 12);
        contentValues[48].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[49] = new ContentValues();
        contentValues[49].put(Contract.RegionNeighborhood._ID, 50);
        contentValues[49].put(Contract.RegionNeighborhood.ID_REGION, 16);
        contentValues[49].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 15);
        contentValues[49].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[50] = new ContentValues();
        contentValues[50].put(Contract.RegionNeighborhood._ID, 51);
        contentValues[50].put(Contract.RegionNeighborhood.ID_REGION, 16);
        contentValues[50].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 19);
        contentValues[50].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[51] = new ContentValues();
        contentValues[51].put(Contract.RegionNeighborhood._ID, 52);
        contentValues[51].put(Contract.RegionNeighborhood.ID_REGION, 16);
        contentValues[51].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 21);
        contentValues[51].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[52] = new ContentValues();
        contentValues[52].put(Contract.RegionNeighborhood._ID, 53);
        contentValues[52].put(Contract.RegionNeighborhood.ID_REGION, 17);
        contentValues[52].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 9);
        contentValues[52].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[53] = new ContentValues();
        contentValues[53].put(Contract.RegionNeighborhood._ID, 54);
        contentValues[53].put(Contract.RegionNeighborhood.ID_REGION, 17);
        contentValues[53].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 11);
        contentValues[53].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[54] = new ContentValues();
        contentValues[54].put(Contract.RegionNeighborhood._ID, 55);
        contentValues[54].put(Contract.RegionNeighborhood.ID_REGION, 17);
        contentValues[54].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 19);
        contentValues[54].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[55] = new ContentValues();
        contentValues[55].put(Contract.RegionNeighborhood._ID, 56);
        contentValues[55].put(Contract.RegionNeighborhood.ID_REGION, 17);
        contentValues[55].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 20);
        contentValues[55].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[56] = new ContentValues();
        contentValues[56].put(Contract.RegionNeighborhood._ID, 57);
        contentValues[56].put(Contract.RegionNeighborhood.ID_REGION, 18);
        contentValues[56].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 19);
        contentValues[56].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[57] = new ContentValues();
        contentValues[57].put(Contract.RegionNeighborhood._ID, 58);
        contentValues[57].put(Contract.RegionNeighborhood.ID_REGION, 19);
        contentValues[57].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 9);
        contentValues[57].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[58] = new ContentValues();
        contentValues[58].put(Contract.RegionNeighborhood._ID, 59);
        contentValues[58].put(Contract.RegionNeighborhood.ID_REGION, 19);
        contentValues[58].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 12);
        contentValues[58].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[59] = new ContentValues();
        contentValues[59].put(Contract.RegionNeighborhood._ID, 60);
        contentValues[59].put(Contract.RegionNeighborhood.ID_REGION, 19);
        contentValues[59].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 13);
        contentValues[59].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[60] = new ContentValues();
        contentValues[60].put(Contract.RegionNeighborhood._ID, 61);
        contentValues[60].put(Contract.RegionNeighborhood.ID_REGION, 19);
        contentValues[60].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 16);
        contentValues[60].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[61] = new ContentValues();
        contentValues[61].put(Contract.RegionNeighborhood._ID, 62);
        contentValues[61].put(Contract.RegionNeighborhood.ID_REGION, 19);
        contentValues[61].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 17);
        contentValues[61].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[62] = new ContentValues();
        contentValues[62].put(Contract.RegionNeighborhood._ID, 63);
        contentValues[62].put(Contract.RegionNeighborhood.ID_REGION, 19);
        contentValues[62].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 18);
        contentValues[62].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[63] = new ContentValues();
        contentValues[63].put(Contract.RegionNeighborhood._ID, 64);
        contentValues[63].put(Contract.RegionNeighborhood.ID_REGION, 19);
        contentValues[63].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 20);
        contentValues[63].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[64] = new ContentValues();
        contentValues[64].put(Contract.RegionNeighborhood._ID, 65);
        contentValues[64].put(Contract.RegionNeighborhood.ID_REGION, 19);
        contentValues[64].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 21);
        contentValues[64].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[65] = new ContentValues();
        contentValues[65].put(Contract.RegionNeighborhood._ID, 66);
        contentValues[65].put(Contract.RegionNeighborhood.ID_REGION, 20);
        contentValues[65].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 11);
        contentValues[65].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[66] = new ContentValues();
        contentValues[66].put(Contract.RegionNeighborhood._ID, 67);
        contentValues[66].put(Contract.RegionNeighborhood.ID_REGION, 20);
        contentValues[66].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 13);
        contentValues[66].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[67] = new ContentValues();
        contentValues[67].put(Contract.RegionNeighborhood._ID, 68);
        contentValues[67].put(Contract.RegionNeighborhood.ID_REGION, 20);
        contentValues[67].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 17);
        contentValues[67].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[68] = new ContentValues();
        contentValues[68].put(Contract.RegionNeighborhood._ID, 69);
        contentValues[68].put(Contract.RegionNeighborhood.ID_REGION, 20);
        contentValues[68].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 19);
        contentValues[68].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[69] = new ContentValues();
        contentValues[69].put(Contract.RegionNeighborhood._ID, 70);
        contentValues[69].put(Contract.RegionNeighborhood.ID_REGION, 21);
        contentValues[69].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 9);
        contentValues[69].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[70] = new ContentValues();
        contentValues[70].put(Contract.RegionNeighborhood._ID, 71);
        contentValues[70].put(Contract.RegionNeighborhood.ID_REGION, 21);
        contentValues[70].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 10);
        contentValues[70].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[71] = new ContentValues();
        contentValues[71].put(Contract.RegionNeighborhood._ID, 72);
        contentValues[71].put(Contract.RegionNeighborhood.ID_REGION, 21);
        contentValues[71].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 16);
        contentValues[71].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[72] = new ContentValues();
        contentValues[72].put(Contract.RegionNeighborhood._ID, 73);
        contentValues[72].put(Contract.RegionNeighborhood.ID_REGION, 21);
        contentValues[72].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 19);
        contentValues[72].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[73] = new ContentValues();
        contentValues[73].put(Contract.RegionNeighborhood._ID, 74);
        contentValues[73].put(Contract.RegionNeighborhood.ID_REGION, 22);
        contentValues[73].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 10);
        contentValues[73].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[74] = new ContentValues();
        contentValues[74].put(Contract.RegionNeighborhood._ID, 75);
        contentValues[74].put(Contract.RegionNeighborhood.ID_REGION, 22);
        contentValues[74].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 14);
        contentValues[74].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        contentValues[75] = new ContentValues();
        contentValues[75].put(Contract.RegionNeighborhood._ID, 76);
        contentValues[75].put(Contract.RegionNeighborhood.ID_REGION, 22);
        contentValues[75].put(Contract.RegionNeighborhood.ID_REGION_NEIGHBORHOOD, 15);
        contentValues[75].put(Contract.RegionNeighborhood.CHANGED,System.currentTimeMillis());

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                getContentResolver().bulkInsert(Contract.RegionNeighborhood.CONTENT_URI,(ContentValues[]) params);
                return null;
            }
        }.execute(contentValues);
    }

    private void insertToTableProblems() {
        ContentValues[] contentValues = new ContentValues[792];
        contentValues[0] = new ContentValues(problemToContentValue("Zdravotníctvo",1,"investment",2,3));
        contentValues[1] = new ContentValues(problemToContentValue("Zdravotníctvo",2,"investment",2,3));
        contentValues[2] = new ContentValues(problemToContentValue("Zdravotníctvo",3,"investment",1,4));
        contentValues[3] = new ContentValues(problemToContentValue("Zdravotníctvo",4,"investment",2,4));
        contentValues[4] = new ContentValues(problemToContentValue("Zdravotníctvo",5,"investment",1,2));
        contentValues[5] = new ContentValues(problemToContentValue("Zdravotníctvo",6,"investment",2,3));
        contentValues[6] = new ContentValues(problemToContentValue("Zdravotníctvo",7,"investment",1,3));
        contentValues[7] = new ContentValues(problemToContentValue("Zdravotníctvo",8,"investment",1,3));
        contentValues[8] = new ContentValues(problemToContentValue("Zdravotníctvo",9,"investment",1,4));
        contentValues[9] = new ContentValues(problemToContentValue("Zdravotníctvo",10,"investment",1.5,3));
        contentValues[10] = new ContentValues(problemToContentValue("Zdravotníctvo",11,"investment",1,4));
        contentValues[11] = new ContentValues(problemToContentValue("Zdravotníctvo",12,"investment",0,4));
        contentValues[12] = new ContentValues(problemToContentValue("Zdravotníctvo",13,"investment",1,3.5));
        contentValues[13] = new ContentValues(problemToContentValue("Zdravotníctvo",14,"investment",1,4));
        contentValues[14] = new ContentValues(problemToContentValue("Zdravotníctvo",15,"investment",1,4));
        contentValues[15] = new ContentValues(problemToContentValue("Zdravotníctvo",16,"investment",1,4));
        contentValues[16] = new ContentValues(problemToContentValue("Zdravotníctvo",17,"investment",1,4));
        contentValues[17] = new ContentValues(problemToContentValue("Zdravotníctvo",18,"investment",1,4));
        contentValues[18] = new ContentValues(problemToContentValue("Zdravotníctvo",19,"investment",2,3));
        contentValues[19] = new ContentValues(problemToContentValue("Zdravotníctvo",20,"investment",1,4));
        contentValues[20] = new ContentValues(problemToContentValue("Zdravotníctvo",21,"investment",1,4));
        contentValues[21] = new ContentValues(problemToContentValue("Zdravotníctvo",22,"investment",1,4));

        contentValues[22] = new ContentValues(problemToContentValue("Školstvo",1,"investment",2,3));
        contentValues[23] = new ContentValues(problemToContentValue("Školstvo",2,"investment",1,3));
        contentValues[24] = new ContentValues(problemToContentValue("Školstvo",3,"investment",2,2));
        contentValues[25] = new ContentValues(problemToContentValue("Školstvo",4,"investment",2,3));
        contentValues[26] = new ContentValues(problemToContentValue("Školstvo",5,"investment",1,3));
        contentValues[27] = new ContentValues(problemToContentValue("Školstvo",6,"investment",1.5,3));
        contentValues[28] = new ContentValues(problemToContentValue("Školstvo",7,"investment",1,3));
        contentValues[29] = new ContentValues(problemToContentValue("Školstvo",8,"investment",1,3));
        contentValues[30] = new ContentValues(problemToContentValue("Školstvo",9,"investment",2,2));
        contentValues[31] = new ContentValues(problemToContentValue("Školstvo",10,"investment",2,2));
        contentValues[32] = new ContentValues(problemToContentValue("Školstvo",11,"investment",2,2));
        contentValues[33] = new ContentValues(problemToContentValue("Školstvo",12,"investment",0.5,3));
        contentValues[34] = new ContentValues(problemToContentValue("Školstvo",13,"investment",2,2));
        contentValues[35] = new ContentValues(problemToContentValue("Školstvo",14,"investment",2,2));
        contentValues[36] = new ContentValues(problemToContentValue("Školstvo",15,"investment",2,2));
        contentValues[37] = new ContentValues(problemToContentValue("Školstvo",16,"investment",2,2));
        contentValues[38] = new ContentValues(problemToContentValue("Školstvo",17,"investment",2,2));
        contentValues[39] = new ContentValues(problemToContentValue("Školstvo",18,"investment",2,2));
        contentValues[40] = new ContentValues(problemToContentValue("Školstvo",19,"investment",2,2));
        contentValues[41] = new ContentValues(problemToContentValue("Školstvo",20,"investment",2,2));
        contentValues[42] = new ContentValues(problemToContentValue("Školstvo",21,"investment",2,2));
        contentValues[43] = new ContentValues(problemToContentValue("Školstvo",22,"investment",2,2));

        contentValues[44] = new ContentValues(problemToContentValue("Doprava",1,"investment",2,3));
        contentValues[45] = new ContentValues(problemToContentValue("Doprava",2,"investment",1,3));
        contentValues[46] = new ContentValues(problemToContentValue("Doprava",3,"investment",0,3));
        contentValues[47] = new ContentValues(problemToContentValue("Doprava",4,"investment",1,4));
        contentValues[48] = new ContentValues(problemToContentValue("Doprava",5,"investment",1,3));
        contentValues[49] = new ContentValues(problemToContentValue("Doprava",6,"investment",1,3));
        contentValues[50] = new ContentValues(problemToContentValue("Doprava",7,"investment",1,3));
        contentValues[51] = new ContentValues(problemToContentValue("Doprava",8,"investment",1,3));
        contentValues[52] = new ContentValues(problemToContentValue("Doprava",9,"investment",1,2));
        contentValues[53] = new ContentValues(problemToContentValue("Doprava",10,"investment",1,2));
        contentValues[54] = new ContentValues(problemToContentValue("Doprava",11,"investment",1,2));
        contentValues[55] = new ContentValues(problemToContentValue("Doprava",12,"investment",-0.5,2.5));
        contentValues[56] = new ContentValues(problemToContentValue("Doprava",13,"investment",1,2.5));
        contentValues[57] = new ContentValues(problemToContentValue("Doprava",14,"investment",1,2));
        contentValues[58] = new ContentValues(problemToContentValue("Doprava",15,"investment",1,2));
        contentValues[59] = new ContentValues(problemToContentValue("Doprava",16,"investment",1,2));
        contentValues[60] = new ContentValues(problemToContentValue("Doprava",17,"investment",1,2));
        contentValues[61] = new ContentValues(problemToContentValue("Doprava",18,"investment",1,2));
        contentValues[62] = new ContentValues(problemToContentValue("Doprava",19,"investment",1.5,2.5));
        contentValues[63] = new ContentValues(problemToContentValue("Doprava",20,"investment",1,2));
        contentValues[64] = new ContentValues(problemToContentValue("Doprava",21,"investment",1,2));
        contentValues[65] = new ContentValues(problemToContentValue("Doprava",22,"investment",1,2));

        contentValues[66] = new ContentValues(problemToContentValue("Kultúra",1,"investment",0,3));
        contentValues[67] = new ContentValues(problemToContentValue("Kultúra",2,"investment",0,3));
        contentValues[68] = new ContentValues(problemToContentValue("Kultúra",3,"investment",0,2));
        contentValues[69] = new ContentValues(problemToContentValue("Kultúra",4,"investment",1,3));
        contentValues[70] = new ContentValues(problemToContentValue("Kultúra",5,"investment",0,2));
        contentValues[71] = new ContentValues(problemToContentValue("Kultúra",6,"investment",1,3));
        contentValues[72] = new ContentValues(problemToContentValue("Kultúra",7,"investment",0,2));
        contentValues[73] = new ContentValues(problemToContentValue("Kultúra",8,"investment",0,2));
        contentValues[74] = new ContentValues(problemToContentValue("Kultúra",9,"investment",0,2));
        contentValues[75] = new ContentValues(problemToContentValue("Kultúra",10,"investment",0.5,2));
        contentValues[76] = new ContentValues(problemToContentValue("Kultúra",11,"investment",0,2));
        contentValues[77] = new ContentValues(problemToContentValue("Kultúra",12,"investment",-1,2.5));
        contentValues[78] = new ContentValues(problemToContentValue("Kultúra",13,"investment",0.5,2.5));
        contentValues[79] = new ContentValues(problemToContentValue("Kultúra",14,"investment",0,2));
        contentValues[80] = new ContentValues(problemToContentValue("Kultúra",15,"investment",0,2));
        contentValues[81] = new ContentValues(problemToContentValue("Kultúra",16,"investment",0,2));
        contentValues[82] = new ContentValues(problemToContentValue("Kultúra",17,"investment",0,2));
        contentValues[83] = new ContentValues(problemToContentValue("Kultúra",18,"investment",0,2));
        contentValues[84] = new ContentValues(problemToContentValue("Kultúra",19,"investment",1,2.5));
        contentValues[85] = new ContentValues(problemToContentValue("Kultúra",20,"investment",0,2));
        contentValues[86] = new ContentValues(problemToContentValue("Kultúra",21,"investment",0,2));
        contentValues[87] = new ContentValues(problemToContentValue("Kultúra",22,"investment",0,2));

        contentValues[88] = new ContentValues(problemToContentValue("Veda a výskum",1,"investment",1,2));
        contentValues[89] = new ContentValues(problemToContentValue("Veda a výskum",2,"investment",1,3));
        contentValues[90] = new ContentValues(problemToContentValue("Veda a výskum",3,"investment",1,3));
        contentValues[91] = new ContentValues(problemToContentValue("Veda a výskum",4,"investment",1,3));
        contentValues[92] = new ContentValues(problemToContentValue("Veda a výskum",5,"investment",0,3));
        contentValues[93] = new ContentValues(problemToContentValue("Veda a výskum",6,"investment",1,3));
        contentValues[94] = new ContentValues(problemToContentValue("Veda a výskum",7,"investment",1,3));
        contentValues[95] = new ContentValues(problemToContentValue("Veda a výskum",8,"investment",1,3));
        contentValues[96] = new ContentValues(problemToContentValue("Veda a výskum",9,"investment",1,3));
        contentValues[97] = new ContentValues(problemToContentValue("Veda a výskum",10,"investment",1.5,2.5));
        contentValues[98] = new ContentValues(problemToContentValue("Veda a výskum",11,"investment",1,3));
        contentValues[99] = new ContentValues(problemToContentValue("Veda a výskum",12,"investment",0,3));
        contentValues[100] = new ContentValues(problemToContentValue("Veda a výskum",13,"investment",1.5,3));
        contentValues[101] = new ContentValues(problemToContentValue("Veda a výskum",14,"investment",1,3));
        contentValues[102] = new ContentValues(problemToContentValue("Veda a výskum",15,"investment",1,3));
        contentValues[103] = new ContentValues(problemToContentValue("Veda a výskum",16,"investment",1,3));
        contentValues[104] = new ContentValues(problemToContentValue("Veda a výskum",17,"investment",1,3));
        contentValues[105] = new ContentValues(problemToContentValue("Veda a výskum",18,"investment",1,3));
        contentValues[106] = new ContentValues(problemToContentValue("Veda a výskum",19,"investment",1.5,2.5));
        contentValues[107] = new ContentValues(problemToContentValue("Veda a výskum",20,"investment",1,3));
        contentValues[108] = new ContentValues(problemToContentValue("Veda a výskum",21,"investment",1,3));
        contentValues[109] = new ContentValues(problemToContentValue("Veda a výskum",22,"investment",1,3));

        contentValues[110] = new ContentValues(problemToContentValue("Podnikanie",1,"investment",1,3));
        contentValues[111] = new ContentValues(problemToContentValue("Podnikanie",2,"investment",0,2));
        contentValues[112] = new ContentValues(problemToContentValue("Podnikanie",3,"investment",0,2));
        contentValues[113] = new ContentValues(problemToContentValue("Podnikanie",4,"investment",0,3));
        contentValues[114] = new ContentValues(problemToContentValue("Podnikanie",5,"investment",1,3));
        contentValues[115] = new ContentValues(problemToContentValue("Podnikanie",6,"investment",1,3));
        contentValues[116] = new ContentValues(problemToContentValue("Podnikanie",7,"investment",1,3));
        contentValues[117] = new ContentValues(problemToContentValue("Podnikanie",8,"investment",1,3));
        contentValues[118] = new ContentValues(problemToContentValue("Podnikanie",9,"investment",1,2));
        contentValues[119] = new ContentValues(problemToContentValue("Podnikanie",10,"investment",1.5,2));
        contentValues[120] = new ContentValues(problemToContentValue("Podnikanie",11,"investment",1,2));
        contentValues[121] = new ContentValues(problemToContentValue("Podnikanie",12,"investment",-0.5,2.5));
        contentValues[122] = new ContentValues(problemToContentValue("Podnikanie",13,"investment",1,2.5));
        contentValues[123] = new ContentValues(problemToContentValue("Podnikanie",14,"investment",1,2));
        contentValues[124] = new ContentValues(problemToContentValue("Podnikanie",15,"investment",1,2));
        contentValues[125] = new ContentValues(problemToContentValue("Podnikanie",16,"investment",1,2));
        contentValues[126] = new ContentValues(problemToContentValue("Podnikanie",17,"investment",1,2));
        contentValues[127] = new ContentValues(problemToContentValue("Podnikanie",18,"investment",1,2));
        contentValues[128] = new ContentValues(problemToContentValue("Podnikanie",19,"investment",1,3));
        contentValues[129] = new ContentValues(problemToContentValue("Podnikanie",20,"investment",1,2));
        contentValues[130] = new ContentValues(problemToContentValue("Podnikanie",21,"investment",1,2));
        contentValues[131] = new ContentValues(problemToContentValue("Podnikanie",22,"investment",1,2));

        contentValues[132] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",1,"investment",1,3));
        contentValues[133] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",2,"investment",1,3));
        contentValues[134] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",3,"investment",1,3));
        contentValues[135] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",4,"investment",1,3));
        contentValues[136] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",5,"investment",1,2));
        contentValues[137] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",6,"investment",1,3));
        contentValues[138] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",7,"investment",1,2));
        contentValues[139] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",8,"investment",1,3));
        contentValues[140] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",9,"investment",1,3));
        contentValues[141] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",10,"investment",1,2.5));
        contentValues[142] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",11,"investment",1,3));
        contentValues[143] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",12,"investment",0.5,2.5));
        contentValues[144] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",13,"investment",1,3));
        contentValues[145] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",14,"investment",1,3));
        contentValues[146] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",15,"investment",1,3));
        contentValues[147] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",16,"investment",1,3));
        contentValues[148] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",17,"investment",1,3));
        contentValues[149] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",18,"investment",1,3));
        contentValues[150] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",19,"investment",2,2.5));
        contentValues[151] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",20,"investment",1,3));
        contentValues[152] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",21,"investment",1,3));
        contentValues[153] = new ContentValues(problemToContentValue("Rozvoj miest a obcí",22,"investment",1,3));

        contentValues[154] = new ContentValues(problemToContentValue("Šport",1,"investment",0,3));
        contentValues[155] = new ContentValues(problemToContentValue("Šport",2,"investment",0,3));
        contentValues[156] = new ContentValues(problemToContentValue("Šport",3,"investment",0.5,2));
        contentValues[157] = new ContentValues(problemToContentValue("Šport",4,"investment",0,3));
        contentValues[158] = new ContentValues(problemToContentValue("Šport",5,"investment",0.5,2));
        contentValues[159] = new ContentValues(problemToContentValue("Šport",6,"investment",1,2));
        contentValues[160] = new ContentValues(problemToContentValue("Šport",7,"investment",1,2));
        contentValues[161] = new ContentValues(problemToContentValue("Šport",8,"investment",1,2));
        contentValues[162] = new ContentValues(problemToContentValue("Šport",9,"investment",2,2));
        contentValues[163] = new ContentValues(problemToContentValue("Šport",10,"investment",1,3));
        contentValues[164] = new ContentValues(problemToContentValue("Šport",11,"investment",2,2));
        contentValues[165] = new ContentValues(problemToContentValue("Šport",12,"investment",1,2.5));
        contentValues[166] = new ContentValues(problemToContentValue("Šport",13,"investment",1.5,2.5));
        contentValues[167] = new ContentValues(problemToContentValue("Šport",14,"investment",2,2));
        contentValues[168] = new ContentValues(problemToContentValue("Šport",15,"investment",2,2));
        contentValues[169] = new ContentValues(problemToContentValue("Šport",16,"investment",2,2));
        contentValues[170] = new ContentValues(problemToContentValue("Šport",17,"investment",2,2));
        contentValues[171] = new ContentValues(problemToContentValue("Šport",18,"investment",2,2));
        contentValues[172] = new ContentValues(problemToContentValue("Šport",19,"investment",2,2));
        contentValues[173] = new ContentValues(problemToContentValue("Šport",20,"investment",2,2));
        contentValues[174] = new ContentValues(problemToContentValue("Šport",21,"investment",2,2));
        contentValues[175] = new ContentValues(problemToContentValue("Šport",22,"investment",2,2));

        contentValues[176] = new ContentValues(problemToContentValue("Cestovný ruch",1,"investment",0,3));
        contentValues[177] = new ContentValues(problemToContentValue("Cestovný ruch",2,"investment",0.5,3));
        contentValues[178] = new ContentValues(problemToContentValue("Cestovný ruch",3,"investment",1,2));
        contentValues[179] = new ContentValues(problemToContentValue("Cestovný ruch",4,"investment",0.5,2.5));
        contentValues[180] = new ContentValues(problemToContentValue("Cestovný ruch",5,"investment",0.5,2.5));
        contentValues[181] = new ContentValues(problemToContentValue("Cestovný ruch",6,"investment",1,2.5));
        contentValues[182] = new ContentValues(problemToContentValue("Cestovný ruch",7,"investment",1,3));
        contentValues[183] = new ContentValues(problemToContentValue("Cestovný ruch",8,"investment",0.5,3));
        contentValues[184] = new ContentValues(problemToContentValue("Cestovný ruch",9,"investment",1,5));
        contentValues[185] = new ContentValues(problemToContentValue("Cestovný ruch",10,"investment",1,3.5));
        contentValues[186] = new ContentValues(problemToContentValue("Cestovný ruch",11,"investment",1,5));
        contentValues[187] = new ContentValues(problemToContentValue("Cestovný ruch",12,"investment",0,3.5));
        contentValues[188] = new ContentValues(problemToContentValue("Cestovný ruch",13,"investment",1.5,4));
        contentValues[189] = new ContentValues(problemToContentValue("Cestovný ruch",14,"investment",1,5));
        contentValues[190] = new ContentValues(problemToContentValue("Cestovný ruch",15,"investment",1,5));
        contentValues[191] = new ContentValues(problemToContentValue("Cestovný ruch",16,"investment",1,5));
        contentValues[192] = new ContentValues(problemToContentValue("Cestovný ruch",17,"investment",1,5));
        contentValues[193] = new ContentValues(problemToContentValue("Cestovný ruch",18,"investment",1,5));
        contentValues[194] = new ContentValues(problemToContentValue("Cestovný ruch",19,"investment",1,5));
        contentValues[195] = new ContentValues(problemToContentValue("Cestovný ruch",20,"investment",1,5));
        contentValues[196] = new ContentValues(problemToContentValue("Cestovný ruch",21,"investment",1,5));
        contentValues[197] = new ContentValues(problemToContentValue("Cestovný ruch",22,"investment",1,5));

        contentValues[198] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",1,"interview",1.5,3));
        contentValues[199] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",2,"interview",2,3));
        contentValues[200] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",3,"interview",2,3));
        contentValues[201] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",4,"interview",1.5,4));
        contentValues[202] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",5,"interview",1,2));
        contentValues[203] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",6,"interview",0.5,3));
        contentValues[204] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",7,"interview",1,3));
        contentValues[205] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",8,"interview",1,3));
        contentValues[206] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",9,"interview",1,3));
        contentValues[207] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",10,"interview",0.5,2.5));
        contentValues[208] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",11,"interview",1,3));
        contentValues[209] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",12,"interview",-0.5,2.5));
        contentValues[210] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",13,"interview",1,3.5));
        contentValues[211] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",14,"interview",1,3));
        contentValues[212] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",15,"interview",1,3));
        contentValues[213] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",16,"interview",1,3));
        contentValues[214] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",17,"interview",1,3));
        contentValues[215] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",18,"interview",1,3));
        contentValues[216] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",19,"interview",1.5,5));
        contentValues[217] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",20,"interview",1,3));
        contentValues[218] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",21,"interview",1,3));
        contentValues[219] = new ContentValues(problemToContentValue("Ochrana lesov a zákaz výrubu",22,"interview",1,3));

        contentValues[220] = new ContentValues(problemToContentValue("Prijímanie imigrantov",1,"interview",-1,3));
        contentValues[221] = new ContentValues(problemToContentValue("Prijímanie imigrantov",2,"interview",-2,3));
        contentValues[222] = new ContentValues(problemToContentValue("Prijímanie imigrantov",3,"interview",-1,4));
        contentValues[223] = new ContentValues(problemToContentValue("Prijímanie imigrantov",4,"interview",-1,4));
        contentValues[224] = new ContentValues(problemToContentValue("Prijímanie imigrantov",5,"interview",-2,4));
        contentValues[225] = new ContentValues(problemToContentValue("Prijímanie imigrantov",6,"interview",-2,4));
        contentValues[226] = new ContentValues(problemToContentValue("Prijímanie imigrantov",7,"interview",-1,3));
        contentValues[227] = new ContentValues(problemToContentValue("Prijímanie imigrantov",8,"interview",-1,3));
        contentValues[228] = new ContentValues(problemToContentValue("Prijímanie imigrantov",9,"interview",-2,3));
        contentValues[229] = new ContentValues(problemToContentValue("Prijímanie imigrantov",10,"interview",-1,3.5));
        contentValues[230] = new ContentValues(problemToContentValue("Prijímanie imigrantov",11,"interview",-2,3));
        contentValues[231] = new ContentValues(problemToContentValue("Prijímanie imigrantov",12,"interview",-2,2));
        contentValues[232] = new ContentValues(problemToContentValue("Prijímanie imigrantov",13,"interview",-1,3.5));
        contentValues[233] = new ContentValues(problemToContentValue("Prijímanie imigrantov",14,"interview",-2,3));
        contentValues[234] = new ContentValues(problemToContentValue("Prijímanie imigrantov",15,"interview",-2,3));
        contentValues[235] = new ContentValues(problemToContentValue("Prijímanie imigrantov",16,"interview",-2,3));
        contentValues[236] = new ContentValues(problemToContentValue("Prijímanie imigrantov",17,"interview",-2,3));
        contentValues[237] = new ContentValues(problemToContentValue("Prijímanie imigrantov",18,"interview",-2,3));
        contentValues[238] = new ContentValues(problemToContentValue("Prijímanie imigrantov",19,"interview",0,5));
        contentValues[239] = new ContentValues(problemToContentValue("Prijímanie imigrantov",20,"interview",-2,3));
        contentValues[240] = new ContentValues(problemToContentValue("Prijímanie imigrantov",21,"interview",-2,3));
        contentValues[241] = new ContentValues(problemToContentValue("Prijímanie imigrantov",22,"interview",-2,3));

        contentValues[242] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",1,"interview",1,3));
        contentValues[243] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",2,"interview",0,3));
        contentValues[244] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",3,"interview",0,4));
        contentValues[245] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",4,"interview",0,3));
        contentValues[246] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",5,"interview",0,3));
        contentValues[247] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",6,"interview",1,3));
        contentValues[248] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",7,"interview",1,3));
        contentValues[249] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",8,"interview",1,3));
        contentValues[250] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",9,"interview",1,3));
        contentValues[251] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",10,"interview",0.5,4));
        contentValues[252] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",11,"interview",1,4));
        contentValues[253] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",12,"interview",0.5,4));
        contentValues[254] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",13,"interview",1,4.5));
        contentValues[255] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",14,"interview",1,4));
        contentValues[256] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",15,"interview",1,4));
        contentValues[257] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",16,"interview",1,4));
        contentValues[258] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",17,"interview",1,4));
        contentValues[259] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",18,"interview",1,4));
        contentValues[260] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",19,"interview",-0.5,5));
        contentValues[261] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",20,"interview",1,4));
        contentValues[262] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",21,"interview",1,4));
        contentValues[263] = new ContentValues(problemToContentValue("Zníženie daní a poskytnutie daňových úľav",22,"interview",1,4));

        contentValues[264] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",1,"interview",0,3));
        contentValues[265] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",2,"interview",0,3));
        contentValues[266] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",3,"interview",-1,4));
        contentValues[267] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",4,"interview",1,4));
        contentValues[268] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",5,"interview",0,3));
        contentValues[269] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",6,"interview",0,3.5));
        contentValues[270] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",7,"interview",0,3));
        contentValues[271] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",8,"interview",0,3));
        contentValues[272] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",9,"interview",0,4));
        contentValues[273] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",10,"interview",0,4));
        contentValues[274] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",11,"interview",0,4));
        contentValues[275] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",12,"interview",-1,4.5));
        contentValues[276] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",13,"interview",1,4));
        contentValues[277] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",14,"interview",0,4));
        contentValues[278] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",15,"interview",0,4));
        contentValues[279] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",16,"interview",0,4));
        contentValues[280] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",17,"interview",0,4));
        contentValues[281] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",18,"interview",0,4));
        contentValues[282] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",19,"interview",0,4));
        contentValues[283] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",20,"interview",0,4));
        contentValues[284] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",21,"interview",0,4));
        contentValues[285] = new ContentValues(problemToContentValue("Zvýšenie sociálnych dávok",22,"interview",0,4));

        contentValues[286] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",1,"interview",-1,3));
        contentValues[287] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",2,"interview",-1,3));
        contentValues[288] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",3,"interview",0,3));
        contentValues[289] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",4,"interview",0,2));
        contentValues[290] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",5,"interview",0,3));
        contentValues[291] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",6,"interview",0,2.5));
        contentValues[292] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",7,"interview",0,3));
        contentValues[293] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",8,"interview",0,3));
        contentValues[294] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",9,"interview",0,3));
        contentValues[295] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",10,"interview",0,2.5));
        contentValues[296] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",11,"interview",0,3));
        contentValues[297] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",12,"interview",-1,2.5));
        contentValues[298] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",13,"interview",-1,3.5));
        contentValues[299] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",14,"interview",0,3));
        contentValues[300] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",15,"interview",0,3));
        contentValues[301] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",16,"interview",0,3));
        contentValues[302] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",17,"interview",0,3));
        contentValues[303] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",18,"interview",0,3));
        contentValues[304] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",19,"interview",-0.5,4));
        contentValues[305] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",20,"interview",0,3));
        contentValues[306] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",21,"interview",0,3));
        contentValues[307] = new ContentValues(problemToContentValue("Zníženie sociálnych dávok",22,"interview",0,3));

        contentValues[308] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",1,"interview",1,3));
        contentValues[309] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",2,"interview",2,4));
        contentValues[310] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",3,"interview",0,3));
        contentValues[311] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",4,"interview",2,4));
        contentValues[312] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",5,"interview",1,3));
        contentValues[313] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",6,"interview",1,3));
        contentValues[314] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",7,"interview",1,3));
        contentValues[315] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",8,"interview",1,3));
        contentValues[316] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",9,"interview",1,4));
        contentValues[317] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",10,"interview",0.5,4));
        contentValues[318] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",11,"interview",1,4));
        contentValues[319] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",12,"interview",1,4));
        contentValues[320] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",13,"interview",1.5,4));
        contentValues[321] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",14,"interview",1,4));
        contentValues[322] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",15,"interview",1,4));
        contentValues[323] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",16,"interview",1,4));
        contentValues[324] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",17,"interview",1,4));
        contentValues[325] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",18,"interview",1,4));
        contentValues[326] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",19,"interview",1.5,3));
        contentValues[327] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",20,"interview",1,4));
        contentValues[328] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",21,"interview",1,4));
        contentValues[329] = new ContentValues(problemToContentValue("Zvýšenie minimálnej mzdy",22,"interview",1,4));

        contentValues[330] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",1,"interview",-2,3));
        contentValues[331] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",2,"interview",-2,3));
        contentValues[332] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",3,"interview",-2,2));
        contentValues[333] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",4,"interview",-2,2));
        contentValues[334] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",5,"interview",-2,3));
        contentValues[335] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",6,"interview",-2,3));
        contentValues[336] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",7,"interview",-2,3));
        contentValues[337] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",8,"interview",-2,3));
        contentValues[338] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",9,"interview",-1,3));
        contentValues[339] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",10,"interview",-1,2.5));
        contentValues[340] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",11,"interview",-1,3));
        contentValues[341] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",12,"interview",-2,2));
        contentValues[342] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",13,"interview",-2,3.5));
        contentValues[343] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",14,"interview",-1,3));
        contentValues[344] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",15,"interview",-1,3));
        contentValues[345] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",16,"interview",-1,3));
        contentValues[346] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",17,"interview",-1,3));
        contentValues[347] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",18,"interview",-1,3));
        contentValues[348] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",19,"interview",-2,4.5));
        contentValues[349] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",20,"interview",-1,3));
        contentValues[350] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",21,"interview",-1,3));
        contentValues[351] = new ContentValues(problemToContentValue("Zníženie minimálnej mzdy",22,"interview",-1,3));

        contentValues[352] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",1,"interview",0,3));
        contentValues[353] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",2,"interview",1,3));
        contentValues[354] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",3,"interview",0,3));
        contentValues[355] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",4,"interview",1,3));
        contentValues[356] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",5,"interview",0,3));
        contentValues[357] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",6,"interview",1,3));
        contentValues[358] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",7,"interview",0,3));
        contentValues[359] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",8,"interview",0,3));
        contentValues[360] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",9,"interview",0,3));
        contentValues[361] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",10,"interview",0.5,3));
        contentValues[362] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",11,"interview",0,3));
        contentValues[363] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",12,"interview",-1,2.5));
        contentValues[364] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",13,"interview",0.5,3));
        contentValues[365] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",14,"interview",0,3));
        contentValues[366] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",15,"interview",0,3));
        contentValues[367] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",16,"interview",0,3));
        contentValues[368] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",17,"interview",0,3));
        contentValues[369] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",18,"interview",0,3));
        contentValues[370] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",19,"interview",0.5,2));
        contentValues[371] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",20,"interview",0,3));
        contentValues[372] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",21,"interview",0,3));
        contentValues[373] = new ContentValues(problemToContentValue("Poskytnutie dotácií poľnohospodárom",22,"interview",0,3));

        contentValues[374] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",1,"interview",-1,3));
        contentValues[375] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",2,"interview",0,4));
        contentValues[376] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",3,"interview",-2,2));
        contentValues[377] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",4,"interview",0,4));
        contentValues[378] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",5,"interview",0,4));
        contentValues[379] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",6,"interview",-1,3));
        contentValues[380] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",7,"interview",0,3));
        contentValues[381] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",8,"interview",0,3));
        contentValues[382] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",9,"interview",0,5));
        contentValues[383] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",10,"interview",-1,3));
        contentValues[384] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",11,"interview",0,5));
        contentValues[385] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",12,"interview",-1,3.5));
        contentValues[386] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",13,"interview",1,3.5));
        contentValues[387] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",14,"interview",0,5));
        contentValues[388] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",15,"interview",0,5));
        contentValues[389] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",16,"interview",0,5));
        contentValues[390] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",17,"interview",0,5));
        contentValues[391] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",18,"interview",0,5));
        contentValues[392] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",19,"interview",0,5));
        contentValues[393] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",20,"interview",-2,5));
        contentValues[394] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",21,"interview",0,5));
        contentValues[395] = new ContentValues(problemToContentValue("Zrušenie vykonávania potratov",22,"interview",0,5));

        contentValues[396] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",1,"interview",-1,3));
        contentValues[397] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",2,"interview",0,4));
        contentValues[398] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",3,"interview",-1,2));
        contentValues[399] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",4,"interview",0,4));
        contentValues[400] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",5,"interview",-2,4));
        contentValues[401] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",6,"interview",-1,3));
        contentValues[402] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",7,"interview",-1.5,3));
        contentValues[403] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",8,"interview",0,3));
        contentValues[404] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",9,"interview",0,5));
        contentValues[405] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",10,"interview",0.5,3.5));
        contentValues[406] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",11,"interview",0,5));
        contentValues[407] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",12,"interview",-1,3.5));
        contentValues[408] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",13,"interview",-0.5,4));
        contentValues[409] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",14,"interview",0,5));
        contentValues[410] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",15,"interview",0,5));
        contentValues[411] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",16,"interview",0,5));
        contentValues[412] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",17,"interview",0,5));
        contentValues[413] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",18,"interview",0,5));
        contentValues[414] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",19,"interview",-1.5,4.5));
        contentValues[415] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",20,"interview",0,5));
        contentValues[416] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",21,"interview",0,5));
        contentValues[417] = new ContentValues(problemToContentValue("Dovolené vykonávanie potratov len ženám v nepriaznivých sociálnych situáciach",22,"interview",0,5));

        contentValues[418] = new ContentValues(problemToContentValue("Podpora menšín",1,"interview",0,3));
        contentValues[419] = new ContentValues(problemToContentValue("Podpora menšín",2,"interview",0,3));
        contentValues[420] = new ContentValues(problemToContentValue("Podpora menšín",3,"interview",0,4));
        contentValues[421] = new ContentValues(problemToContentValue("Podpora menšín",4,"interview",-1,2));
        contentValues[422] = new ContentValues(problemToContentValue("Podpora menšín",5,"interview",0,3));
        contentValues[423] = new ContentValues(problemToContentValue("Podpora menšín",6,"interview",-1,3));
        contentValues[424] = new ContentValues(problemToContentValue("Podpora menšín",7,"interview",0,3));
        contentValues[425] = new ContentValues(problemToContentValue("Podpora menšín",8,"interview",0,3));
        contentValues[426] = new ContentValues(problemToContentValue("Podpora menšín",9,"interview",0,3));
        contentValues[427] = new ContentValues(problemToContentValue("Podpora menšín",10,"interview",0.5,3.5));
        contentValues[428] = new ContentValues(problemToContentValue("Podpora menšín",11,"interview",0,3));
        contentValues[429] = new ContentValues(problemToContentValue("Podpora menšín",12,"interview",-1,2.5));
        contentValues[430] = new ContentValues(problemToContentValue("Podpora menšín",13,"interview",0,3.5));
        contentValues[431] = new ContentValues(problemToContentValue("Podpora menšín",14,"interview",0,3));
        contentValues[432] = new ContentValues(problemToContentValue("Podpora menšín",15,"interview",0,3));
        contentValues[433] = new ContentValues(problemToContentValue("Podpora menšín",16,"interview",0,3));
        contentValues[434] = new ContentValues(problemToContentValue("Podpora menšín",17,"interview",0,3));
        contentValues[435] = new ContentValues(problemToContentValue("Podpora menšín",18,"interview",0,3));
        contentValues[436] = new ContentValues(problemToContentValue("Podpora menšín",19,"interview",1,3.5));
        contentValues[437] = new ContentValues(problemToContentValue("Podpora menšín",20,"interview",0,3));
        contentValues[438] = new ContentValues(problemToContentValue("Podpora menšín",21,"interview",0,3));
        contentValues[439] = new ContentValues(problemToContentValue("Podpora menšín",22,"interview",0,3));

        contentValues[440] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",1,"interview",-1,3));
        contentValues[441] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",2,"interview",0,3));
        contentValues[442] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",3,"interview",-1,3));
        contentValues[443] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",4,"interview",1,3));
        contentValues[444] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",5,"interview",0,3));
        contentValues[445] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",6,"interview",0,3));
        contentValues[446] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",7,"interview",0,3));
        contentValues[447] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",8,"interview",0,3));
        contentValues[448] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",9,"interview",0,3));
        contentValues[449] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",10,"interview",0,3));
        contentValues[450] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",11,"interview",0,3));
        contentValues[451] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",12,"interview",-1,2.5));
        contentValues[452] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",13,"interview",0,3.5));
        contentValues[453] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",14,"interview",0,3));
        contentValues[454] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",15,"interview",0,3));
        contentValues[455] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",16,"interview",0,3));
        contentValues[456] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",17,"interview",0,3));
        contentValues[457] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",18,"interview",0,3));
        contentValues[458] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",19,"interview",0,4));
        contentValues[459] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",20,"interview",0,3));
        contentValues[460] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",21,"interview",0,3));
        contentValues[461] = new ContentValues(problemToContentValue("Zvýšená ochrana hraníc štátu",22,"interview",0,3));

        contentValues[462] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",1,"interview",1,3));
        contentValues[463] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",2,"interview",1,3));
        contentValues[464] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",3,"interview",1,3));
        contentValues[465] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",4,"interview",1,3));
        contentValues[466] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",5,"interview",1,2));
        contentValues[467] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",6,"interview",1,3));
        contentValues[468] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",7,"interview",1,3));
        contentValues[469] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",8,"interview",1,3));
        contentValues[470] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",9,"interview",1,3));
        contentValues[471] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",10,"interview",1,2.5));
        contentValues[472] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",11,"interview",1,3));
        contentValues[473] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",12,"interview",0,2.5));
        contentValues[474] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",13,"interview",1,3));
        contentValues[475] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",14,"interview",1,2.5));
        contentValues[476] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",15,"interview",1,3.5));
        contentValues[477] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",16,"interview",1,3));
        contentValues[478] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",17,"interview",1,3));
        contentValues[479] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",18,"interview",1,3));
        contentValues[480] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",19,"interview",1,2.5));
        contentValues[481] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",20,"interview",1,3));
        contentValues[482] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",21,"interview",1,3));
        contentValues[483] = new ContentValues(problemToContentValue("Podpora inovácií a nových technológií",22,"interview",1,3));

        contentValues[484] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",1,"interview",2,2));
        contentValues[485] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",2,"interview",1,3));
        contentValues[486] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",3,"interview",2,2));
        contentValues[487] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",4,"interview",1,1));
        contentValues[488] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",5,"interview",2,2));
        contentValues[489] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",6,"interview",1,3));
        contentValues[490] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",7,"interview",1,3));
        contentValues[491] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",8,"interview",1,2));
        contentValues[492] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",9,"interview",2,3));
        contentValues[493] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",10,"interview",2,3));
        contentValues[494] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",11,"interview",2,3));
        contentValues[495] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",12,"interview",0,3));
        contentValues[496] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",13,"interview",1.5,3));
        contentValues[497] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",14,"interview",2,3));
        contentValues[498] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",15,"interview",2,3));
        contentValues[499] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",16,"interview",2,3));
        contentValues[500] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",17,"interview",2,3));
        contentValues[501] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",18,"interview",2,3));
        contentValues[502] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",19,"interview",2,3.5));
        contentValues[503] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",20,"interview",2,3));
        contentValues[504] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",21,"interview",2,3));
        contentValues[505] = new ContentValues(problemToContentValue("Zvýšený dôraz na ekologickú recykláciu a triedenie odpadov",22,"interview",2,3));

        contentValues[506] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",1,"interview",0,3));
        contentValues[507] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",2,"interview",0,3));
        contentValues[508] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",3,"interview",0,3));
        contentValues[509] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",4,"interview",0,3));
        contentValues[510] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",5,"interview",-1,3));
        contentValues[511] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",6,"interview",-1,4));
        contentValues[512] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",7,"interview",-2,3));
        contentValues[513] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",8,"interview",0,3));
        contentValues[514] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",9,"interview",-2,4));
        contentValues[515] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",10,"interview",-0.5,2.5));
        contentValues[516] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",11,"interview",-2,4));
        contentValues[517] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",12,"interview",-2,2.5));
        contentValues[518] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",13,"interview",-2,4));
        contentValues[519] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",14,"interview",-2,2.5));
        contentValues[520] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",15,"interview",-2,3));
        contentValues[521] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",16,"interview",-2,4));
        contentValues[522] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",17,"interview",-2,4));
        contentValues[523] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",18,"interview",-2,4));
        contentValues[524] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",19,"interview",0.5,3.5));
        contentValues[525] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",20,"interview",-2,4));
        contentValues[526] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",21,"interview",-2,4));
        contentValues[527] = new ContentValues(problemToContentValue("Legalizácia používania marihuany",22,"interview",-2,4));

        contentValues[528] = new ContentValues(problemToContentValue("Povinná vojenská služba",1,"interview",-2,3));
        contentValues[529] = new ContentValues(problemToContentValue("Povinná vojenská služba",2,"interview",-1,3));
        contentValues[530] = new ContentValues(problemToContentValue("Povinná vojenská služba",3,"interview",-2,3));
        contentValues[531] = new ContentValues(problemToContentValue("Povinná vojenská služba",4,"interview",-1,3));
        contentValues[532] = new ContentValues(problemToContentValue("Povinná vojenská služba",5,"interview",-2,4));
        contentValues[533] = new ContentValues(problemToContentValue("Povinná vojenská služba",6,"interview",-1,3));
        contentValues[534] = new ContentValues(problemToContentValue("Povinná vojenská služba",7,"interview",-1,3));
        contentValues[535] = new ContentValues(problemToContentValue("Povinná vojenská služba",8,"interview",-1,3));
        contentValues[536] = new ContentValues(problemToContentValue("Povinná vojenská služba",9,"interview",-2,3));
        contentValues[537] = new ContentValues(problemToContentValue("Povinná vojenská služba",10,"interview",-2,2.5));
        contentValues[538] = new ContentValues(problemToContentValue("Povinná vojenská služba",11,"interview",-2,3));
        contentValues[539] = new ContentValues(problemToContentValue("Povinná vojenská služba",12,"interview",-1.5,2));
        contentValues[540] = new ContentValues(problemToContentValue("Povinná vojenská služba",13,"interview",-1.5,3));
        contentValues[541] = new ContentValues(problemToContentValue("Povinná vojenská služba",14,"interview",-2,3));
        contentValues[542] = new ContentValues(problemToContentValue("Povinná vojenská služba",15,"interview",-2,3));
        contentValues[543] = new ContentValues(problemToContentValue("Povinná vojenská služba",16,"interview",-2,3));
        contentValues[544] = new ContentValues(problemToContentValue("Povinná vojenská služba",17,"interview",-2,3));
        contentValues[545] = new ContentValues(problemToContentValue("Povinná vojenská služba",18,"interview",-2,3));
        contentValues[546] = new ContentValues(problemToContentValue("Povinná vojenská služba",19,"interview",-1,4.5));
        contentValues[547] = new ContentValues(problemToContentValue("Povinná vojenská služba",20,"interview",-2,3));
        contentValues[548] = new ContentValues(problemToContentValue("Povinná vojenská služba",21,"interview",-2,3));
        contentValues[549] = new ContentValues(problemToContentValue("Povinná vojenská služba",22,"interview",-2,3));

        contentValues[550] = new ContentValues(problemToContentValue("Podpora národného vojska",1,"interview",0,3));
        contentValues[551] = new ContentValues(problemToContentValue("Podpora národného vojska",2,"interview",0,3));
        contentValues[552] = new ContentValues(problemToContentValue("Podpora národného vojska",3,"interview",-1,3));
        contentValues[553] = new ContentValues(problemToContentValue("Podpora národného vojska",4,"interview",0,3));
        contentValues[554] = new ContentValues(problemToContentValue("Podpora národného vojska",5,"interview",0,3));
        contentValues[555] = new ContentValues(problemToContentValue("Podpora národného vojska",6,"interview",0,3));
        contentValues[556] = new ContentValues(problemToContentValue("Podpora národného vojska",7,"interview",0,3));
        contentValues[557] = new ContentValues(problemToContentValue("Podpora národného vojska",8,"interview",0,3.5));
        contentValues[558] = new ContentValues(problemToContentValue("Podpora národného vojska",9,"interview",0,3));
        contentValues[559] = new ContentValues(problemToContentValue("Podpora národného vojska",10,"interview",0.5,2.5));
        contentValues[560] = new ContentValues(problemToContentValue("Podpora národného vojska",11,"interview",0,3));
        contentValues[561] = new ContentValues(problemToContentValue("Podpora národného vojska",12,"interview",-1,3));
        contentValues[562] = new ContentValues(problemToContentValue("Podpora národného vojska",13,"interview",0.5,3));
        contentValues[563] = new ContentValues(problemToContentValue("Podpora národného vojska",14,"interview",0,3));
        contentValues[564] = new ContentValues(problemToContentValue("Podpora národného vojska",15,"interview",0,3));
        contentValues[565] = new ContentValues(problemToContentValue("Podpora národného vojska",16,"interview",0,3));
        contentValues[566] = new ContentValues(problemToContentValue("Podpora národného vojska",17,"interview",0,3));
        contentValues[567] = new ContentValues(problemToContentValue("Podpora národného vojska",18,"interview",0,3));
        contentValues[568] = new ContentValues(problemToContentValue("Podpora národného vojska",19,"interview",-0.5,4));
        contentValues[569] = new ContentValues(problemToContentValue("Podpora národného vojska",20,"interview",0,3));
        contentValues[570] = new ContentValues(problemToContentValue("Podpora národného vojska",21,"interview",0,3));
        contentValues[571] = new ContentValues(problemToContentValue("Podpora národného vojska",22,"interview",0,3));

        contentValues[572] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",1,"interview",1,3));
        contentValues[573] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",2,"interview",1,3));
        contentValues[574] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",3,"interview",0,3));
        contentValues[575] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",4,"interview",1,2));
        contentValues[576] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",5,"interview",0,2));
        contentValues[577] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",6,"interview",0,3));
        contentValues[578] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",7,"interview",0,3));
        contentValues[579] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",8,"interview",0,3));
        contentValues[580] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",9,"interview",0,2));
        contentValues[581] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",10,"interview",0.5,3));
        contentValues[582] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",11,"interview",0,2));
        contentValues[583] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",12,"interview",-1,2));
        contentValues[584] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",13,"interview",0.5,2));
        contentValues[585] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",14,"interview",0,2));
        contentValues[586] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",15,"interview",0,2));
        contentValues[587] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",16,"interview",0,2));
        contentValues[588] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",17,"interview",0,2));
        contentValues[589] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",18,"interview",0,2));
        contentValues[590] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",19,"interview",1.5,2));
        contentValues[591] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",20,"interview",0,2));
        contentValues[592] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",21,"interview",0,2));
        contentValues[593] = new ContentValues(problemToContentValue("Poskytovanie dotácií za každé narodené dieťa",22,"interview",0,2));

        contentValues[594] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",1,"interview",-1,2));
        contentValues[595] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",2,"interview",-1,3));
        contentValues[596] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",3,"interview",0,2));
        contentValues[597] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",4,"interview",1,3));
        contentValues[598] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",5,"interview",0,3));
        contentValues[599] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",6,"interview",-1,3));
        contentValues[600] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",7,"interview",0,3));
        contentValues[601] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",8,"interview",0,3));
        contentValues[602] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",9,"interview",0,3));
        contentValues[603] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",10,"interview",0,3));
        contentValues[604] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",11,"interview",0,3));
        contentValues[605] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",12,"interview",-1,2));
        contentValues[606] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",13,"interview",0,3));
        contentValues[607] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",14,"interview",0,3));
        contentValues[608] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",15,"interview",0,3));
        contentValues[609] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",16,"interview",0,3));
        contentValues[610] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",17,"interview",0,3));
        contentValues[611] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",18,"interview",0,3));
        contentValues[612] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",19,"interview",-1,3));
        contentValues[613] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",20,"interview",0,3));
        contentValues[614] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",21,"interview",0,3));
        contentValues[615] = new ContentValues(problemToContentValue("Poskytovanie dotácií rodinám, ktoré majú len jedno dieťa",22,"interview",0,3));

        contentValues[616] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",1,"interview",1,3));
        contentValues[617] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",2,"interview",0,3));
        contentValues[618] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",3,"interview",1,3));
        contentValues[619] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",4,"interview",1,2));
        contentValues[620] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",5,"interview",0,2));
        contentValues[621] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",6,"interview",1,3));
        contentValues[622] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",7,"interview",1,3));
        contentValues[623] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",8,"interview",1,3));
        contentValues[624] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",9,"interview",1,3));
        contentValues[625] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",10,"interview",1.5,3.5));
        contentValues[626] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",11,"interview",1,3));
        contentValues[627] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",12,"interview",-0.5,2.5));
        contentValues[628] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",13,"interview",1,3.5));
        contentValues[629] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",14,"interview",1,3));
        contentValues[630] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",15,"interview",1,3));
        contentValues[631] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",16,"interview",1,3));
        contentValues[632] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",17,"interview",1,3));
        contentValues[633] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",18,"interview",1,3));
        contentValues[634] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",19,"interview",0.5,3.5));
        contentValues[635] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",20,"interview",1,3));
        contentValues[636] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",21,"interview",1,3));
        contentValues[637] = new ContentValues(problemToContentValue("Podpora nových podnikateľov",22,"interview",1,3));

        contentValues[638] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",1,"interview",1,3));
        contentValues[639] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",2,"interview",0,3));
        contentValues[640] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",3,"interview",0,3));
        contentValues[641] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",4,"interview",0,2));
        contentValues[642] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",5,"interview",0,3));
        contentValues[643] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",6,"interview",1,3));
        contentValues[644] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",7,"interview",1,3));
        contentValues[645] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",8,"interview",1,3));
        contentValues[646] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",9,"interview",0,3));
        contentValues[647] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",10,"interview",1,3.5));
        contentValues[648] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",11,"interview",0,3));
        contentValues[649] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",12,"interview",-0.5,2.5));
        contentValues[650] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",13,"interview",0.5,3.5));
        contentValues[651] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",14,"interview",0,3));
        contentValues[652] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",15,"interview",0,3));
        contentValues[653] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",16,"interview",0,3));
        contentValues[654] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",17,"interview",0,3));
        contentValues[655] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",18,"interview",0,3));
        contentValues[656] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",19,"interview",-0.5,4.5));
        contentValues[657] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",20,"interview",0,3));
        contentValues[658] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",21,"interview",0,3));
        contentValues[659] = new ContentValues(problemToContentValue("Daňové úľavy pre nových podnikateľov",22,"interview",0,3));

        contentValues[660] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",1,"interview",1,4));
        contentValues[661] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",2,"interview",1,4));
        contentValues[662] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",3,"interview",1,4));
        contentValues[663] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",4,"interview",2,3));
        contentValues[664] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",5,"interview",1,3));
        contentValues[665] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",6,"interview",2,4));
        contentValues[666] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",7,"interview",2,3));
        contentValues[667] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",8,"interview",1.5,3));
        contentValues[668] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",9,"interview",0,4));
        contentValues[669] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",10,"interview",1,4));
        contentValues[670] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",11,"interview",0,4));
        contentValues[671] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",12,"interview",-0.5,4));
        contentValues[672] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",13,"interview",1.5,4));
        contentValues[673] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",14,"interview",0,4));
        contentValues[674] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",15,"interview",0,4));
        contentValues[675] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",16,"interview",0,4));
        contentValues[676] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",17,"interview",0,4));
        contentValues[677] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",18,"interview",0,4));
        contentValues[678] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",19,"interview",0,4));
        contentValues[679] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",20,"interview",0,4));
        contentValues[680] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",21,"interview",0,4));
        contentValues[681] = new ContentValues(problemToContentValue("Vytváranie nových pracovných miest",22,"interview",0,4));

        contentValues[682] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",1,"interview",1,3));
        contentValues[683] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",2,"interview",1,3));
        contentValues[684] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",3,"interview",1,3));
        contentValues[685] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",4,"interview",1,2));
        contentValues[686] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",5,"interview",2,2));
        contentValues[687] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",6,"interview",1,3));
        contentValues[688] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",7,"interview",1,3));
        contentValues[689] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",8,"interview",1,3));
        contentValues[690] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",9,"interview",0,5));
        contentValues[691] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",10,"interview",1,3.5));
        contentValues[692] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",11,"interview",0,5));
        contentValues[693] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",12,"interview",-1,4.5));
        contentValues[694] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",13,"interview",1,4));
        contentValues[695] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",14,"interview",0,5));
        contentValues[696] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",15,"interview",0,5));
        contentValues[697] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",16,"interview",0,5));
        contentValues[698] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",17,"interview",0,5));
        contentValues[699] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",18,"interview",0,5));
        contentValues[700] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",19,"interview",0,5));
        contentValues[701] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",20,"interview",0,5));
        contentValues[702] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",21,"interview",0,5));
        contentValues[703] = new ContentValues(problemToContentValue("Ochrana historických pamiatok",22,"interview",0,5));

        contentValues[704] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",1,"interview",-1.5,3));
        contentValues[705] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",2,"interview",-2,3.5));
        contentValues[706] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",3,"interview",-2,3));
        contentValues[707] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",4,"interview",-1.5,1));
        contentValues[708] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",5,"interview",-2,1));
        contentValues[709] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",6,"interview",-2,3));
        contentValues[710] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",7,"interview",-2,3));
        contentValues[711] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",8,"interview",-2,3));
        contentValues[712] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",9,"interview",-2,4));
        contentValues[713] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",10,"interview",-2,4));
        contentValues[714] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",11,"interview",-2,4));
        contentValues[715] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",12,"interview",-2,3));
        contentValues[716] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",13,"interview",-2,3.5));
        contentValues[717] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",14,"interview",-2,4));
        contentValues[718] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",15,"interview",-2,4));
        contentValues[719] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",16,"interview",-2,4));
        contentValues[720] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",17,"interview",-2,4));
        contentValues[721] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",18,"interview",-2,4));
        contentValues[722] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",19,"interview",-2,4));
        contentValues[723] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",20,"interview",-2,4));
        contentValues[724] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",21,"interview",-2,4));
        contentValues[725] = new ContentValues(problemToContentValue("Zvýšenie dôchodkového veku",22,"interview",-2,4));

        contentValues[726] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",1,"interview",2,2));
        contentValues[727] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",2,"interview",1,3));
        contentValues[728] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",3,"interview",1,2));
        contentValues[729] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",4,"interview",2,1.5));
        contentValues[730] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",5,"interview",1.5,2.5));
        contentValues[731] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",6,"interview",2,3));
        contentValues[732] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",7,"interview",2,3));
        contentValues[733] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",8,"interview",1,3));
        contentValues[734] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",9,"interview",1,5));
        contentValues[735] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",10,"interview",1,4.5));
        contentValues[736] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",11,"interview",-0.5,5));
        contentValues[737] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",12,"interview",2,5));
        contentValues[738] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",13,"interview",1,4.5));
        contentValues[739] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",14,"interview",1,5));
        contentValues[740] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",15,"interview",1,5));
        contentValues[741] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",16,"interview",1,5));
        contentValues[742] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",17,"interview",1,5));
        contentValues[743] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",18,"interview",1,5));
        contentValues[744] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",19,"interview",1,5));
        contentValues[745] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",20,"interview",1,5));
        contentValues[746] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",21,"interview",1,5));
        contentValues[747] = new ContentValues(problemToContentValue("Poskytovanie zvýhodnených hypoték pre mladých",22,"interview",1,5));

        contentValues[748] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",1,"interview",2,4.5));
        contentValues[749] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",2,"interview",2,4.5));
        contentValues[750] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",3,"interview",2,5));
        contentValues[751] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",4,"interview",2,1.5));
        contentValues[752] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",5,"interview",1.5,4));
        contentValues[753] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",6,"interview",2,5));
        contentValues[754] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",7,"interview",2,3));
        contentValues[755] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",8,"interview",2,4));
        contentValues[756] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",9,"interview",2,5));
        contentValues[757] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",10,"interview",2,5));
        contentValues[758] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",11,"interview",2,5));
        contentValues[759] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",12,"interview",0,5));
        contentValues[760] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",13,"interview",2,5));
        contentValues[761] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",14,"interview",2,5));
        contentValues[762] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",15,"interview",2,5));
        contentValues[763] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",16,"interview",2,5));
        contentValues[764] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",17,"interview",2,5));
        contentValues[765] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",18,"interview",2,5));
        contentValues[766] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",19,"interview",2,5));
        contentValues[767] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",20,"interview",2,5));
        contentValues[768] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",21,"interview",2,5));
        contentValues[769] = new ContentValues(problemToContentValue("Spravodlivé riešenie korupcie ",22,"interview",2,5));

        contentValues[770] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",1,"interview",0,3));
        contentValues[771] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",2,"interview",2,3.5));
        contentValues[772] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",3,"interview",0,3));
        contentValues[773] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",4,"interview",1.5,1));
        contentValues[774] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",5,"interview",1.5,3));
        contentValues[775] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",6,"interview",1,3));
        contentValues[776] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",7,"interview",1,3));
        contentValues[777] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",8,"interview",1,3));
        contentValues[778] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",9,"interview",2,5));
        contentValues[779] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",10,"interview",1,4));
        contentValues[780] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",11,"interview",2,5));
        contentValues[781] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",12,"interview",0,3.5));
        contentValues[782] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",13,"interview",2,4.5));
        contentValues[783] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",14,"interview",2,5));
        contentValues[784] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",15,"interview",2,5));
        contentValues[785] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",16,"interview",2,5));
        contentValues[786] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",17,"interview",2,5));
        contentValues[787] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",18,"interview",2,5));
        contentValues[788] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",19,"interview",2,5));
        contentValues[789] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",20,"interview",2,5));
        contentValues[790] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",21,"interview",2,5));
        contentValues[791] = new ContentValues(problemToContentValue("Zníženie dôchodkového veku",22,"interview",2,5));

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                getContentResolver().bulkInsert(Contract.Problems.CONTENT_URI,(ContentValues[]) params);
                return null;
            }
        }.execute(contentValues);
    }

    private ContentValues problemToContentValue(String problemName, int idRegion, String actionType, double effect, double solvability) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Problems.PROBLEM_NAME,problemName);
        contentValues.put(Contract.Problems.ID_REGION,idRegion);
        contentValues.put(Contract.Problems.ACTION_TYPE,actionType);
        contentValues.put(Contract.Problems.EFFECT,effect);
        contentValues.put(Contract.Problems.SOLVABILITY,solvability);
        contentValues.put(Contract.Problems.CHANGED,System.currentTimeMillis());
        return contentValues;
    }

    private void insertToTableCharacter() {
        ContentValues[] contentValues = new ContentValues[4];
        contentValues[0] = new ContentValues();

        contentValues[0].put(Contract.Candidate.NAME_SURNAME,"Andrej Kiska");
        contentValues[0].put(Contract.Candidate.ID_BIRTH_REGION,7);
        contentValues[0].put(Contract.Candidate.CHARISMA,40);
        contentValues[0].put(Contract.Candidate.REPUTATION,35);
        contentValues[0].put(Contract.Candidate.MONEY,25);
        contentValues[0].put(Contract.Candidate.CREATED_BY_USER,0);
        contentValues[0].put(Contract.Candidate.CHANGED,System.currentTimeMillis());

        contentValues[1] = new ContentValues();
        contentValues[1].put(Contract.Candidate.NAME_SURNAME,"Róbert Fico");
        contentValues[1].put(Contract.Candidate.ID_BIRTH_REGION,4);
        contentValues[1].put(Contract.Candidate.CHARISMA,30);
        contentValues[1].put(Contract.Candidate.REPUTATION,25);
        contentValues[1].put(Contract.Candidate.MONEY,45);
        contentValues[1].put(Contract.Candidate.CREATED_BY_USER,0);
        contentValues[1].put(Contract.Candidate.CHANGED,System.currentTimeMillis());

        contentValues[2] = new ContentValues();
        contentValues[2].put(Contract.Candidate.NAME_SURNAME,"Miloš Zeman");
        contentValues[2].put(Contract.Candidate.ID_BIRTH_REGION,19);
        contentValues[2].put(Contract.Candidate.CHARISMA,25);
        contentValues[2].put(Contract.Candidate.REPUTATION,25);
        contentValues[2].put(Contract.Candidate.MONEY,50);
        contentValues[2].put(Contract.Candidate.CREATED_BY_USER,0);
        contentValues[2].put(Contract.Candidate.CHANGED,System.currentTimeMillis());

        contentValues[3] = new ContentValues();
        contentValues[3].put(Contract.Candidate.NAME_SURNAME,"Andrej Babiš");
        contentValues[3].put(Contract.Candidate.ID_BIRTH_REGION,22);
        contentValues[3].put(Contract.Candidate.CHARISMA,40);
        contentValues[3].put(Contract.Candidate.REPUTATION,30);
        contentValues[3].put(Contract.Candidate.MONEY,30);
        contentValues[3].put(Contract.Candidate.CREATED_BY_USER,0);
        contentValues[3].put(Contract.Candidate.CHANGED,System.currentTimeMillis());

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                getContentResolver().bulkInsert(Contract.Candidate.CONTENT_URI,(ContentValues[]) params);
                return null;
            }
        }.execute(contentValues);
    }

    private void insertToTableSettings() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Settings.NUMBER_OF_ELECTIONS_WEEK,6);
        contentValues.put(Contract.Settings.FIRST_STEP_IN_GAME,1);
        contentValues.put(Contract.Settings.RECOMMENDATION,1);
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
