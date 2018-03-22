package com.game.election.electiongame;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.game.election.electiongame.broadcastReceiver.FinishActivityReceiver;
import com.game.election.electiongame.classes.TogetherFunctions;
import com.game.election.electiongame.provider.Contract;

public class ChooseStateActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /*
    *
    *  vyber statu, kde sa budu konat volby
    *  prepnutie do aktivity ChooseCandidate, kde sa vybera kandidat, s ktorym pouzivatel bude hrat
    *
    * */

    TogetherFunctions tf;
    ListView listview;                      //listview so statmi
    SimpleCursorAdapter listViewAdapter;    //adapter na listview
    private float startX, startY;           //premenne, ktore oznacuju bod pri dotyku pouzivatela s obrazovkou


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
        setContentView(R.layout.activity_choose_state);
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


    /*
    *
    * nastavuje sa cely layout v aktivite
    * nastavi sa broadcast receiver, s ktorym sa vypne aktivita po vybere kandidata, pre zacatim hry
    * nastavi sa adapter pre listview s udajmi o statoch
    * nastavia sa onItemClickListener a onItemLongClickListener pre listview a fab
    *
    * */
    private void setLayout() {
        //nastavenie broadcast receiveru, s ktorym sa vypne aktivita po vybere kandidata, pre zacatim hry
        FinishActivityReceiver broadcast_receiver = new FinishActivityReceiver();
        registerReceiver(broadcast_receiver, new IntentFilter("finish"));

        tf = new TogetherFunctions();

        //nastavenie listviewu so statmi a adapteru tohto lisviewu
        listview = findViewById(R.id.listviewStates);
        String[] from = {Contract.State.STATE_NAME};
        int[] to = {R.id.textViewText};
        //je vyhodnote pouzit SimpleCursorAdapter, pretoze sa vyberu vsetky staty z DB, nie len nejake
        listViewAdapter = new SimpleCursorAdapter(this, R.layout.item_of_listview_simple_only_textview, null, from, to, 0);
        listview.setAdapter(listViewAdapter);

        //nastavenie onItemClickListeneru na listview
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //nastavenie farby vybranej polozky v listview
                TextView tv = view.findViewById(R.id.textViewText);
                tv.setTextColor(getResources().getColor(R.color.color_blue_light));
                for (int i = 0; i < listview.getChildCount(); i++) {
                    if (i != position) {
                        View child = listview.getChildAt(i);
                        ((TextView)child.findViewById(R.id.textViewText)).setTextColor(getResources().getColor(R.color.color_white));
                    }
                }

                //podla textu vybranej polozky sa zistia informacie o state dopytom do DB
                String stateName = tv.getText().toString();
                Bundle obj = getContentResolver().call(Contract.State.CONTENT_URI,"return informations for state by stateName",stateName,null);
                long population = obj.getLong("population");
                int regionCount = obj.getInt("regionCount");

                //nastavia sa informacie o state
                (findViewById(R.id.textView)).setVisibility(View.INVISIBLE);
                (findViewById(R.id.constraintLayout2)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.textViewStateName)).setText(stateName);
                ((TextView)findViewById(R.id.textViewPopulation)).setText(String.format(getResources().getString(R.string.population), tf.getPrettyFormattedTextFromNumberWithThousands(population)));
                ((TextView)findViewById(R.id.textViewRegions)).setText(String.format(getResources().getString(R.string.num_of_regions), String.valueOf(regionCount)));

                //nastavenie onClickListenera na objekt, ktorym sa posuva pouzivatel dalej na vyber kandidata
                (findViewById(R.id.imageViewNext)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //mohla by som pouzit aj premennu stateName, ale ked som pouzila, tak nemala vzdy spravnu hodnotu, preto sa zistuje znovu nazov statu
                        String state = ((TextView)findViewById(R.id.textViewStateName)).getText().toString();
                        Intent intent = new Intent(ChooseStateActivity.this,ChooseCandidateActivity.class);
                        intent.putExtra("state",state);
                        startActivity(intent);
                    }
                });
            }
        });
        getLoaderManager().initLoader(0, Bundle.EMPTY, this);
    }


    /*
    *
    * funkcia, kde sa vytvara loader, pomocou ktoreho sa nacitava zoznam statov do listviewu
    *
    * */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id != 0) {
            throw new IllegalStateException("Invalid loader ID " + id);
        }
        CursorLoader cursorLoader = new CursorLoader(this);
        cursorLoader.setUri(Contract.State.CONTENT_URI);
        return cursorLoader;
    }


    /*
    *
    * funkcia, ktora sa zapne po skonceni nacitavania zoznamu statov do listviewu
    * data sa posunu adapteru
    *
    * */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.setNotificationUri(getContentResolver(), Contract.State.CONTENT_URI);
        listViewAdapter.swapCursor(data);
    }


    /*
    *
    * funkcia, ktora sa zapne ak bol posledny loader resetnuty
    *
    * */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        listViewAdapter.swapCursor(null);
    }

}