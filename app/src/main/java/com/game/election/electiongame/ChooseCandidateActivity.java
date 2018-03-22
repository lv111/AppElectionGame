package com.game.election.electiongame;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.game.election.electiongame.adapter.SimpleAdapter;
import com.game.election.electiongame.classes.Candidate;
import com.game.election.electiongame.classes.TogetherFunctions;
import com.game.election.electiongame.provider.Contract;

import java.util.ArrayList;
import java.util.Random;


public class ChooseCandidateActivity extends AppCompatActivity {

    /*
    *
    *  vyber kandidata, za ktory bude hrat pouzivatel
    *  prepnutie do aktivity CreateUpdateOwnCandidate, kde sa da vytvorit alebo upravit kandidat
    *  prepnutie do aktivity SummaryBeforeGame, po vybere kandidata
    *
    * */

    TogetherFunctions tf;
    String state;                       //stat konania volieb
    ListView listview;                  //listview s kandidatmi
    SimpleAdapter listViewAdapter;      //adapter k listviewu s kandidatmi
    ArrayList<Candidate> candidates;    //arraylist s kandidatmi v listviewe
    ArrayList<String> names;            //arraylist, ktory obsahuje len mena kandidatov
    float startX, startY;               //premenne, ktore oznacuju bod pri dotyku pouzivatela s obrazovkou


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
        setContentView(R.layout.activity_choose_candidate);
        setLayout();
    }


    /*
    *
    * vo funkcii onResume sa musi skontrolovat, ci data zobrazene v listviewe s kandidatmi su aktualne
    *
    * */
    @Override
    protected void onResume() {
        super.onResume();
        checkIfDataAreActual();
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
    * funkcia, kde sa kontroluje, ci su data zobrazene v listview aktualne
    *
    * */
    private void checkIfDataAreActual() {
        Bundle obj = getContentResolver().call(Contract.State.CONTENT_URI,"return all characters in state by stateName",state,null);
        int count = obj.getInt("count");
        if (count != listview.getCount()) {
            setArrayListCandidates(obj,count);
            listViewAdapter.updateData(names);
            listview.invalidateViews();
        }
    }


    /*
    *
    * nastavuje sa cely layout v aktivite
    * nastavi sa adapter pre listview s udajmi o kandidatoch
    * nastavia sa onItemClickListener a onItemLongClickListener pre listview a fab
    *
    * */
    private void setLayout() {
        tf = new TogetherFunctions();

        //nastavi sa premenne state ako vybrany stat z predchadzajucej aktivity
        state = getIntent().getStringExtra("state");

        //spravi sa dopyt do DB pre vyber kandidatov z vybraneho statu
        Bundle obj = getContentResolver().call(Contract.State.CONTENT_URI,"return all characters in state by stateName",state,null);
        int count = obj.getInt("count");

        //nastavi sa adapter pre listview s udajmi o kandidatoch
        listview = findViewById(R.id.listviewCandidates);
        setArrayListCandidates(obj,count);
        listViewAdapter = new SimpleAdapter(names,getApplicationContext());
        listview.setAdapter(listViewAdapter);

        //nastavi sa onItemClickListener pre listview
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                clickOnItemOfListView(view, position);
            }
        });

        //nastavi sa onItemLongClickListener pre listview
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                return longClickOnItemOfListView(position);
            }
        });

        //nastavi sa fab pre pridanie noveho kandidata
        //zaroven sa posiela aj nazov statu, aby bolo mozne pridat kandidata len do vybraneho statu
        (findViewById(R.id.floatingActionButtonAddCandidate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseCandidateActivity.this, CreateUpdateOwnCandidateActivity.class);
                intent.putExtra("state",state);
                startActivity(intent);
            }
        });

    }


    /*
    *
    * nastavuje sa arraylist s kandidatmi - candidates a names
    * arraylist candidates obsahuje viac informacii o kandidatoch
    * arraylist names obsahuje len mena a priezviska kandidatov, tento arraylist sa aj posiela pre adapter listviewu
    *
    * */
    private void setArrayListCandidates(Bundle obj, int count) {
        candidates = new ArrayList<>();
        names = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Bundle row = obj.getBundle("row" + String.valueOf(i));
            candidates.add(new Candidate(row.getInt("id"),row.getString("nameSurname"), 0,row.getString("birthRegionName"),
                    0,null,row.getInt("charisma"), row.getInt("reputation"),row.getInt("money"),row.getInt("createdByUser"), 0));
            names.add(row.getString("nameSurname"));
        }
    }


    /*
    *
    * funkcia pre onItemClickListener na listview
    * nastavi sa farba vybranej polozky z listviewu
    * podla premennej position sa vyberie pozicia objektu v listview a na zaklade nej sa vyhladaju
    *       informacie o kandidatovi v arrayliste candidates
    * nasledne sa ponastavuju textview pre zobrazovanie informacii o kandidatovi v dolnej casti obrazovky
    * nastavi sa onClickListener pre objekt, ktorym sa zapina aktivita SummaryBeforeGame
    *
    * */
    private void clickOnItemOfListView(View view, final int position) {
        //nastavenie poloziek v listviewe - farby vybraneho textu
        for (int i = 0; i < listview.getChildCount(); i++) {
            View child = listview.getChildAt(i);
            ((TextView) child.findViewById(R.id.textViewText)).setTextColor(getResources().getColor(R.color.color_white));
        }
        TextView textView = view.findViewById(R.id.textViewText);
        textView.setTextColor(getResources().getColor(R.color.color_blue_light));

        //informacie o kandidatovi na zaklade premennej position
        Candidate candidate = candidates.get(position);
        String candidateNameSurname = names.get(position);
        String birthRegionOfCandidate = candidate.getBirthRegionName();
        int charismaOfCandidate = candidate.getCharisma();
        int reputationOfCandidate = candidate.getReputation();
        int moneyOfCandidate = candidate.getMoney();

        //nastavenie textviewov pre zobrazovanie informacii o vybranom kandidatovi
        (findViewById(R.id.textView)).setVisibility(View.INVISIBLE);
        (findViewById(R.id.constraintLayout2)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.textViewCandidateName)).setText(candidateNameSurname);
        ((TextView)findViewById(R.id.textViewBirthRegion)).setText(String.format(getResources().getString(R.string.birth_region_of_candidate), birthRegionOfCandidate));

        //nastavenie textviewov pre urcenie sirky textviewov, ktore zobrazuju vlastnosti kandidata
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,charismaOfCandidate);
        (findViewById(R.id.textViewCharisma)).setLayoutParams(linearLayoutParams);
        linearLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,reputationOfCandidate);
        (findViewById(R.id.textViewReputation)).setLayoutParams(linearLayoutParams);
        linearLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,moneyOfCandidate);
        (findViewById(R.id.textViewMoney)).setLayoutParams(linearLayoutParams);
        linearLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
                100-charismaOfCandidate-reputationOfCandidate-moneyOfCandidate);
        (findViewById(R.id.textViewPointsLeft)).setLayoutParams(linearLayoutParams);

        //nastavenie onClickListener na objekt, ktorym sa zapina aktivita SummaryBeforeGame
        (findViewById(R.id.imageViewNext)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = listview.getChildCount();

                //vyber oponenta
                Random random = new Random();
                int positionOpponent = random.nextInt(count);
                while (position == positionOpponent)
                    positionOpponent = random.nextInt(count);

                //zatvorenie aktivity ChooseCandidate
                ChooseCandidateActivity.this.finish();
                Intent intentToFinishActivityChooseState = new Intent("finish");
                sendBroadcast(intentToFinishActivityChooseState);

                Intent intent = new Intent(ChooseCandidateActivity.this,SummaryBeforeGameActivity.class);
                intent.putExtra("state",state);
                intent.putExtra("positionCandidate",position);
                intent.putExtra("positionOpponent",positionOpponent);
                startActivity(intent);
            }
        });
    }


    /*
    *
    * funkcia pre onItemLongClickListener na listview
    * podla toho, ci bol kandidat vytvoreny pouzivatelom, funkcia vracia hodnotu true alebo false
    * ak bol kandidat vytvoreny pouzivatelom, zapne sa dialog s moznostami kandidata, cize upravit alebo vymazat kandidata
    *
    * */
    private boolean longClickOnItemOfListView(final int position) {
        Candidate candidate = candidates.get(position);
        if (candidate.getCreatedByUser() == 0)
            //kandidat nebol vytvoreny pouzivatelom
            return false;

        else {
            //kandidat bol vytvoreny pouzivatelom
            //zapnutie dialogu s moznostami kandidata
            final int idCandidate = candidate.getId();
            final Dialog dialogCandidateOptions = tf.getDialog(ChooseCandidateActivity.this,true,R.layout.dialog_candidate_options);
            dialogCandidateOptions.show();

            //nastavenie onClickListener na tlacidlo pre upravu kandidata
            (dialogCandidateOptions.findViewById(R.id.buttonUpdateCharacter)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ChooseCandidateActivity.this, CreateUpdateOwnCandidateActivity.class);
                    intent.putExtra("idCharacter",idCandidate);
                    intent.putExtra("state",state);
                    startActivity(intent);
                    dialogCandidateOptions.dismiss();
                }
            });

            //nastavenie onClickListener na tlacidlo pre vymazanie kandidata
            (dialogCandidateOptions.findViewById(R.id.buttonDeleteCharacter)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogCandidateOptions.dismiss();

                    //zapne sa dialog pre potvrdenie, ci chce pouzivatel vymazat kandidata
                    final Dialog dialogDeleteCandidate = tf.getDialogSimpleQuestion(ChooseCandidateActivity.this,true,
                            R.layout.dialog_simple_question, getResources().getText(R.string.delete_candidate).toString(),
                            getResources().getText(R.string.sure_you_want_delete_candidate).toString(), getResources().getText(R.string.delete).toString(),
                            getResources().getText(R.string.cancel).toString());
                            new Dialog(ChooseCandidateActivity.this);
                    (dialogDeleteCandidate.findViewById(R.id.buttonNegativeButton)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogDeleteCandidate.dismiss();
                        }
                    });

                    //nastavenie onClickListener na tlacidlo pre potvrdenie vymazania kandidata
                    (dialogDeleteCandidate.findViewById(R.id.buttonPositiveAnswer)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
                                @Override
                                protected void onDeleteComplete(int token, Object cookie, int result) {
                                    super.onDeleteComplete(token, cookie, result);
                                    //po vymazani kandidata nasleduje aktualizacia listviewu s kandidatmi
                                    //takisto je potrebne aktualizovat arraylisty candidates a names
                                    checkIfDataAreActual();
                                    dialogDeleteCandidate.dismiss();
                                }
                            };
                            Uri uri = ContentUris.withAppendedId(Contract.Candidate.CONTENT_URI, idCandidate);
                            asyncQueryHandler.startDelete(0,null,uri,null,null);
                        }
                    });
                    dialogDeleteCandidate.show();
                }
            });
            return true;
        }
    }
}

