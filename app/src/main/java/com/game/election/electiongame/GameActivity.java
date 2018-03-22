package com.game.election.electiongame;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.game.election.electiongame.adapter.HistoryAdapter;
import com.game.election.electiongame.classes.ActionOfGame;
import com.game.election.electiongame.classes.Animations;
import com.game.election.electiongame.classes.Candidate;
import com.game.election.electiongame.classes.ChangeVariable;
import com.game.election.electiongame.classes.History;
import com.game.election.electiongame.classes.MyRadioButton;
import com.game.election.electiongame.classes.Problem;
import com.game.election.electiongame.classes.Region;
import com.game.election.electiongame.classes.TogetherFunctions;
import com.game.election.electiongame.classes.ZoomableViewGroup;
import com.game.election.electiongame.provider.Contract;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
//TODO: ked mi otvori dialog a oznacim temu alebo oblast investicie, tak rozlisit ci tam modre alebo cervene kruzko a to iste plati pre nazov, aka farba bude


    /*
    *
    * aktivita, kde prebieha samotna hra
    * pouzivatel sa moze dostat do tejto aktivity cez aktivitu SummaryBeforeGame
    * po uplynuti volebnej kampani hra konci a nasleduje aktivita GameEvaluation
    *
    * */

    TogetherFunctions tf;
    Animations animations;

    ImageView imageViewMap;         //imageview s mapou vybraneho statu, nachadza sa v objekte zoomableViewGroup
    TextView tvCandidateName,       //textview s menom a priezviskom kandidata
            tvOpponentName,         //textview s menom a priezviskom oponenta
            tvCandidatePercent,     //textview s poctom percent volicov, ktori budu volit kandidata
            tvOpponentPercent;      //textview s poctom percent volicov, ktori budu volit oponenta
    Button buttonDoAction;          //tlacidlo v dialogu, ktorym sa potvrdi vyber temy na interview alebo oblasti investicie a zapne sa akcia
    ZoomableViewGroup zoomableViewGroup;    //objekt s mapou, ktory mozem posuvat, priblizovat a oddialovat
    RadioGroup radioGroupListOfProblems;    //skupina radiobuttonov, kde sa vybera tema na interview alebo oblast investicie
    RelativeLayout relativeLayoutRegionDetailOptions = null,    //relative layout s detailom regionu
            relativeLayoutMapObjects;       //relative layout, kde sa pridavaju relativeLayouty z arraylistu regionViews
    ArrayList<RelativeLayout> regionViews;  //arraylist s relativeLayoutmi, na ktore pouzivatel klika a otvara sa detail regionu

    Candidate candidate,            //trieda s informaciami o kandidatovi
            opponent;               //trieda s informaciami o oponentovi

    ArrayList<Region> regions;      //arraylist so zoznamom regionov vybraneho statu
    ArrayList<Problem> problems;    //arraylist so zoznamom problemov vo vybranom regione
    ArrayList<ActionOfGame> actionOfGames = new ArrayList<>();  //arraylist, kde sa zaznamenava historia krokov kandidata a oponenta

    int idSavedGame,                //ak sa hra ulozena hra, tak v tejto premennej bude identifikator ulozenej hry z DB
            idSelectedRegion,       //ak pouzivatel klikne na prislusny relative layout, ktory sa vztahuje na region, tak v tejto premennej sa ulozi identifikator tohto regionu
            positionSelectedRegion, //ak pouzivatel klikne na prislusny relative layout, ktory sa vztahuje na region, tak v tejto premennej sa ulozi pozicia vybraneho regionu vzhladom na arraylist regions
            numberOfWeeks,          //pocet tyzdnov volebnej kampane ulozeny v DB
            firstStepInGame,        //charakterizuje, kto urobi v hre prvy krok, tato hodnota je tiez ulozena v DB
            recommendation,         //charakterizuje, ci sa pouzivatelovi maju zobrazovat odporucania, aky vplyv bude mat dana problematika na volicov vo vybranom kraji
            idRegionNameTextviewInRelativeLayoutRegionDetailOptions,    //identifikator textviewu s nazvom regionu v relativeLayoute s detailom regionu
            idProCandidateTextviewInRelativeLayoutRegionDetailOptions,  //identifikator textviewu s poctom percent volicov kandidata v relativeLayoute s detailom regionu
            idProOpponentTextviewInRelativeLayoutRegionDetailOptions,   //identifikator textviewu s poctom percent volicov oponenta v relativeLayoute s detailom regionu
            idLastActionTextviewInRelativeLayoutRegionDetailOptions,    //identifikator textviewu s poslednou vykonanou akciou vo vybranom regione v relativeLayoute s detailom tohto regionu
            idInvestmentButtonInRelativeLayoutRegionDetailOptions,      //identifikator tlacidla pre vykonanie investicie vo vybranom kraji v relativeLayoute s detailom tohto regionu
            idInterviewButtonInRelativeLayoutRegionDetailOptions;       //identifikator tlacidla pre vykonanie interview vo vybranom kraji v relativeLayoute s detailom tohto regionu
    long population;                //hodnota s populaciou celeho statu
    float startX, startY;           //premenne, ktore oznacuju bod pri dotyku pouzivatela s obrazovkou
    boolean disabledOnClick = false,        //identifikuje, ci je dovolene alebo zakazane klikat, tuto logicku hodnotu vyuzivam, ak je na tahu oponent
            fabtoolbarVisibility = false,   //identifikuje, ci je otvorene alebo zavrete menu
            savedGame = false;              //identifikuje, ci tato hra bola ulozena, cize do aktivity GameActivity sa pouzivatel dostal cez MainMenuActivity pomocou dialogu s ulozenymi hrami
    String stateName;               //hodnota s nazvom statu

    ChangeVariable actualWeek,      //premenna s hodnotou aktualneho poradia tyzdna vo volebnej kampani
            actualOnStep;           //premenna s hodnotou, kto je momentalne na tahu

    //nastavenie onClickListenera pre prehratie zvucky kliknutia, tato zvucka sa ozve len pre kliknutia pouzivatela
    View.OnClickListener playMusicOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (actualOnStep.getIntValue() == 1)
                tf.playMusic(getApplicationContext());
        }
    };



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
        setContentView(R.layout.activity_game);
        setLayout();
    }


    /*
    *
    * funckia, ktora sa zavola pri kazdom dotyku pouzivatela, aj pri stlaceni tlacidla spet, ktory sa nenachadza na obrazovke, ale je priamo sucastou telefonu
    *
    * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //ak pouzivatel klikol na tlacidlo spet, tak sa otvori dialog s otazkou, ci chce pouzivatel naozaj ukoncit hru
            final Dialog dialog = tf.getDialogSimpleQuestion(GameActivity.this,true,
                    R.layout.dialog_simple_question, getResources().getText(R.string.end_game).toString(),
                    getResources().getText(R.string.sure_you_want_end_game).toString(), getResources().getText(R.string.ending).toString(),
                    getResources().getText(R.string.cancel).toString());
            //nastavuje sa pozitivna odpoved, cize naozaj chce ukoncit, v tom pripade sa aktivita ukonci a prepne sa na hlavne menu aplikacie
            (dialog.findViewById(R.id.buttonPositiveAnswer)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            (dialog.findViewById(R.id.buttonNegativeButton)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            return false;
        }
        else
            return super.onKeyDown(keyCode, event);
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
                if (!disabledOnClick) {
                    startX = ev.getX();
                    startY = ev.getY();
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (!disabledOnClick) {
                    float endX = ev.getX();
                    float endY = ev.getY();
                    tf.checkIfIsClick(getApplicationContext(), startX, endX, startY, endY);
                }
                break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    /*
    *
    * funkcia, kde sa nastavuje cely layout aktivity
    * zistovanie, ci bola hra ulozena alebo nie
    * volanie funkcii pre nastavenie konkretnych casti layoutu
    * spocitanie celkovej populacie statu
    *
    * */
    private void setLayout() {
        int positionCharacter = 0, positionOpponent = 0;
        Bundle obj = null;
        //zisti sa, ci hra bola ulozena
        Intent intent = getIntent();
        idSavedGame = intent.getIntExtra("idSavedGame",-1);
        if (idSavedGame != -1) {
            //hra bola ulozena
            savedGame = true;

            //nacitanie informacii o ulozenej hre
            obj = getContentResolver().call(Contract.State.CONTENT_URI,"return saved game by idSavedGame", String.valueOf(idSavedGame), null);

            //nastavenie hry
            actualWeek = new ChangeVariable(obj.getInt("actualWeek"));
            actualOnStep = new ChangeVariable(obj.getInt("onStep"));
            setOnChangeListeners();
            numberOfWeeks = obj.getInt("numberOfElectionWeek");
            firstStepInGame = obj.getInt("firstStepInGame");
            recommendation = obj.getInt("recommendation");
            actualWeek.callListener();
            stateName = obj.getString("stateName");
        }
        else {
            //hra nebola ulozena, jedna sa o novu hru
            actualWeek = new ChangeVariable(1);
            actualOnStep = new ChangeVariable(1);
            setOnChangeListeners();

            //nacitanie nastaveni hry z DB
            @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
                @Override
                protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                    super.onQueryComplete(token, cookie, cursor);

                    cursor.moveToFirst();
                    numberOfWeeks = cursor.getInt(Contract.Settings.COLUMN_INDEX_NUMBER_OF_ELECTIONS_WEEK);
                    firstStepInGame = cursor.getInt(Contract.Settings.COLUMN_INDEX_FIRST_STEP_IN_GAME);
                    actualOnStep.setValue(firstStepInGame);
                    recommendation = cursor.getInt(Contract.Settings.COLUMN_INDEX_RECOMMENDATION);
                    actualWeek.callListener();
                }
            };
            asyncQueryHandler.startQuery(0, null, Contract.Settings.CONTENT_URI, null, null, null, null);

            //nastavenie premennych pre nazov statu a pozicie kandidata a oponenta
            stateName = intent.getStringExtra("state");
            positionCharacter = intent.getIntExtra("positionCharacter", 0);
            positionOpponent = intent.getIntExtra("positionOpponent", 0);
        }

        idSelectedRegion = -1;
        positionSelectedRegion = -1;
        tvCandidateName = findViewById(R.id.textViewCandidate);
        tvCandidatePercent = findViewById(R.id.textViewCandidatePercent);
        tvOpponentName = findViewById(R.id.textViewOpponent);
        tvOpponentPercent = findViewById(R.id.textViewOpponentPercent);
        animations = new Animations();
        tf = new TogetherFunctions();
        zoomableViewGroup = findViewById(R.id.zoomableViewGroup);

        //zapnutie funkcii potrebnych pre nastavenie hry
        setRegions(obj);
        setCharacterOpponent(positionCharacter,positionOpponent,obj);
        addViewsOfRegions();


        //nastavenie onClickListenera na FAB tlacidlo, ktorym sa zapina menu
        (findViewById(R.id.fabtoolbarFab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!disabledOnClick) {
                    RelativeLayout fabtoolbar = findViewById(R.id.fabtoolbar);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) fabtoolbar.getLayoutParams();
                    params.height = (int) getResources().getDimension(R.dimen.space_70dp);
                    params.width = 0;
                    fabtoolbar.setLayoutParams(params);
                    v.setVisibility(View.INVISIBLE);
                    fabtoolbarVisibility = true;
                }
            }
        });

        //nastavenie onClickListenera na polozku Ulozit z menu
        (findViewById(R.id.fabtoolbarSave)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idSavedGame == -1) {
                    //hra este nebola ulozena
                    //vo funkcii saveGame sa prepise idSavedGame
                    saveGame();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.game_saved), Toast.LENGTH_LONG).show();
                }
                else {
                    //otvorenie dialogu pre vyber aktualizacie ulozenej hry alebo ulozenia novej hry
                    final Dialog dialog = tf.getDialogSimpleQuestion(GameActivity.this,true,
                            R.layout.dialog_simple_question, getResources().getText(R.string.save_game).toString(),
                            getResources().getText(R.string.save_new_game_or_update_saved_game).toString(),
                            getResources().getText(R.string.update).toString(),
                            getResources().getText(R.string.save).toString());
                    dialog.show();

                    //aktualizuje sa ulozena hra
                    (dialog.findViewById(R.id.buttonPositiveAnswer)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //ulozit
                            if(!disabledOnClick) {
                                dialog.dismiss();
                                updateSavedGame();
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.game_saved), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //ulozi sa nova hra
                    (dialog.findViewById(R.id.buttonNegativeButton)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //update
                            if(!disabledOnClick) {
                                dialog.dismiss();
                                saveGame();
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.game_saved), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        //nastavenie onClickListenera na polozku Historia z menu
        (findViewById(R.id.fabtoolbarHistory)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeHistory();
            }
        });

        //nastavenie onClickListenera na polozku Koniec z menu
        (findViewById(R.id.fabtoolbarEnd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //otvorenie dialogu, ci chce pouzivatel ukoncit hru bez ulozenia
                final Dialog dialog = tf.getDialogSimpleQuestion(GameActivity.this,true,
                        R.layout.dialog_simple_question, getResources().getText(R.string.end_game).toString(),
                        getResources().getText(R.string.sure_you_want_end_game_without_saving).toString(), getResources().getText(R.string.save).toString(),
                        getResources().getText(R.string.ending).toString());
                dialog.show();

                //pouzivatel ulozi hru
                (dialog.findViewById(R.id.buttonPositiveAnswer)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //ulozit
                        saveGame();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.game_saved), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                //pouzivatel ukonci hru bez ulozenia
                (dialog.findViewById(R.id.buttonNegativeButton)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //update
                        finish();
                    }
                });
            }
        });

        //nastavenie onClickListenera na menu, ak je viditelne, tak sa zavrie
        (findViewById(R.id.fabtoolbarBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout fabtoolbar = findViewById(R.id.fabtoolbar);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) fabtoolbar.getLayoutParams();
                params.height = 0;
                params.width = 0;
                fabtoolbar.setLayoutParams(params);
                (findViewById(R.id.fabtoolbarFab)).setVisibility(View.VISIBLE);
                fabtoolbarVisibility = false;
            }
        });

        //spocitanie celkovej populacie statu
        population = 0;
        for (int i = 0; i < regions.size(); i++)
            population += regions.get(i).getPopulation();
    }


    /*
    *
    * nastavenie ChangeListenerov pre premenne typu ChangeVariable
    *
    * */
    private void setOnChangeListeners() {
        //nastavenie premennej pre aktualny tyzden
        actualWeek.setListener(new ChangeVariable.ChangeListener() {
            @Override
            public void onChange() {
                ((TextView) findViewById(R.id.textViewActualElectionWeek)).setText(String.valueOf( actualWeek.getIntValue() ) + "/" + String.valueOf(numberOfWeeks));
            }
        });
        //nastavenie premennej pre oznacenie, kto je aktualne na tahu
        actualOnStep.setListener(new ChangeVariable.ChangeListener() {
            @Override
            public void onChange() {
                if (actualOnStep.getIntValue() == 0) {//krok vykonava opponent
                    animations.shake(1500,500,tvOpponentName);
                    seeOpponentStep();
                }
                else if (actualOnStep.getIntValue() == 1) {
                    animations.shake(1500,500,tvCandidateName);
                }
            }
        });
    }


    /*
    *
    * funkcia, kde sa nastavuje arrayList regions a v pripade ulozenej hry aj historii krokov, takze arraylist actionOfGames
    *
    * */
    private void setRegions(Bundle obj) {
        if (!savedGame) {
            //nova hra
            //nacitanie vsetkych regionov podla nazvu statu, kde sa konaju volby
            obj = getContentResolver().call(Contract.State.CONTENT_URI, "return all regions in state by stateName", stateName, null);
            int count = obj.getInt("count");

            //naplnenie arraylistu regions
            regions = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                Bundle row = obj.getBundle("row" + String.valueOf(i));
                regions.add(new Region(row.getInt("id"), row.getString("regionName"),
                        row.getString("abbreviation"), row.getLong("population"),
                        actualOnStep.getIntValue(), 0, false,
                        actualWeek.getIntValue(), null, null, 0,
                        row.getLong("population"), 0, 0, row.getLong("population"), 0));
            }
        }
        else {
            //ulozena hra
            int objIdState = obj.getInt("idState");

            Bundle objRegions = obj.getBundle("regions");

            //naplnenie arraylistu idRegions pre vsetky regiony z ulozenej hry
            int objCountRegions = objRegions.getInt("count");
            ArrayList<Integer> idRegions = new ArrayList<>();
            for(int i = 0; i < objCountRegions; i++) {
                Bundle objRegion = objRegions.getBundle("region" + String.valueOf(i));
                idRegions.add(objRegion.getInt("idRegion"));
            }

            //nacitanie vsetkych regionov podla identifikatora statu
            Bundle callObj = getContentResolver().call(Contract.State.CONTENT_URI, "return all regions in state by idState", String.valueOf(objIdState), null);
            int count = callObj.getInt("count");

            //naplnenie arraylistu regions a arraylistu initialRegions
            regions = new ArrayList<>();
            ArrayList<Region> initialRegions = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                Bundle row = callObj.getBundle("row" + String.valueOf(i));
                int idRegion = row.getInt("id");
                int position = idRegions.indexOf(idRegion);
                Bundle objRegion = objRegions.getBundle("region" + String.valueOf(position));
                long proCharacter = objRegion.getLong("proCharacter");
                long undecided = objRegion.getLong("undecided");
                long proOpponent = objRegion.getLong("proOpponent");
                regions.add(new Region(row.getInt("id"), row.getString("regionName"), row.getString("abbreviation"),
                        row.getLong("population"), actualOnStep.getIntValue(), 0,
                        false, actualWeek.getIntValue(),null, null,
                        proCharacter, undecided, proOpponent, proCharacter, undecided, proOpponent));
                initialRegions.add(new Region(row.getInt("id"), row.getString("regionName"),
                        row.getString("abbreviation"),row.getLong("population"),
                        actualOnStep.getIntValue(), 0, false,
                        actualWeek.getIntValue(), null, null, 0,
                        row.getLong("population"), 0, 0, row.getLong("population"),
                        0));
            }

            //nacitanie objektu pre ulozenie historie
            Bundle objHistory = obj.getBundle("history");
            if (objHistory != null) {
                //tato podmienka je dolezita kvoli tomu, ze ak sa ulozi hra hned na zaciatku, bez historie, tak v tabulke historia sa nevytvori ziaden zaznam
                //ak takuto hru bez historie pouzivatel zapne, bez tejto podmienky by spadla aplikacia
                int objCountHistory = objHistory.getInt("count");

                //naplnenie arraylistu actionOfGames
                actionOfGames = new ArrayList<>(objCountHistory);
                for (int i = 0; i < objCountHistory; i++) {
                    Bundle objWeekActions = objHistory.getBundle("history" + String.valueOf(i));
                    int objWeek = objWeekActions.getInt("week");
                    int objCharacter = objWeekActions.getInt("character");
                    String objActionType = objWeekActions.getString("actionType");

                    //nacitanie stavu regionov pri konkretnom kroku kandidata alebo oponenta
                    //do arraylistu actionOfGames pridavam ako regiony akcie arraylist regionsToAdd
                    Bundle objHistoryRegions = objWeekActions.getBundle("regions");
                    int objCountHistoryRegions = objHistoryRegions.getInt("count");
                    ArrayList<Region> regionsToAdd = new ArrayList<>();
                    for (int j = 0; j < objCountHistoryRegions; j++) {
                        Bundle objRegion = objHistoryRegions.getBundle("region" + String.valueOf(j));

                        //podla id regionu z DB prehladavam arraylist initialRegions, kde najdem ostatne informacie o regione
                        int objIdRegion = objRegion.getInt("idRegion");
                        for (int k = 0; k < initialRegions.size(); k++) {
                            Region region = initialRegions.get(k);
                            if (region.getId() == objIdRegion) {
                                //do tejto podmienky vstupim, ak najdem region v arrayliste
                                String objProblemName = objRegion.getString("problemName");
                                if (objRegion.getInt("regionOfAction") == 1)
                                    region.setRegionOfAction(true);
                                else
                                    region.setRegionOfAction(false);
                                region.setBeforeProCharacter(objRegion.getLong("beforeProPlayer"));
                                region.setBeforeUndecided(objRegion.getLong("beforeUndecided"));
                                region.setBeforeProOpponent(objRegion.getLong("beforeProOpponent"));
                                region.setAfterProCharacter(objRegion.getLong("afterProPlayer"));
                                region.setAfterUndecided(objRegion.getLong("afterUndecided"));
                                region.setAfterProOpponent(objRegion.getLong("afterProOpponent"));
                                region.setActionType(objActionType);
                                region.setCharacter(objCharacter);
                                region.setWeek(objWeek);
                                region.setIdProblem(objRegion.getInt("idProblem"));

                                String lastAction = "";
                                if (objActionType.equals("interview"))
                                    lastAction = getResources().getString(R.string.interview);
                                else
                                    lastAction = getResources().getString(R.string.investment);
                                lastAction = lastAction.substring(0,1).toUpperCase() + lastAction.substring(1) + " - " + objProblemName;
                                region.setLastAction(lastAction);
                                regionsToAdd.add(region);

                                //nastavenie poslednej akcie v arrayliste regions
                                regions.get(k).setLastAction(region.getLastAction());
                                break;
                            }
                        }
                    }
                    actionOfGames.add(new ActionOfGame(objWeek, objCharacter, objActionType, regionsToAdd));
                }
            }
        }
    }


    /*
    *
    * nastavenie tried kandidata a oponenta
    *
    * */
    private void setCharacterOpponent(int positionCharacter, int positionOpponent, Bundle obj) {
        if (!savedGame) {
            //nova hra

            //nacitanie vsetkych kandidatov ktori mozu hrat vo zvolenom state
            obj = getContentResolver().call(Contract.State.CONTENT_URI, "return all characters in state by stateName", stateName, null);
            //podla pozicie kandidata sa vyberie kandidat
            Bundle objCharacter = obj.getBundle("row" + String.valueOf(positionCharacter));
            candidate = new Candidate(objCharacter.getInt("id"), objCharacter.getString("nameSurname"),
                    objCharacter.getInt("idBirthRegion"), objCharacter.getString("birthRegionName"),
                    objCharacter.getInt("idBirthState"), objCharacter.getString("birthStateName"),
                    objCharacter.getInt("charisma"), objCharacter.getInt("reputation"),
                    objCharacter.getInt("money"), objCharacter.getInt("createdByUser"),
                    objCharacter.getLong("changed"));

            //podla pozicie oponenta sa vyberie oponent
            Bundle objOpponent = obj.getBundle("row" + String.valueOf(positionOpponent));
            opponent = new Candidate(objOpponent.getInt("id"), objOpponent.getString("nameSurname"),
                    objOpponent.getInt("idBirthRegion"), objOpponent.getString("birthRegionName"),
                    objOpponent.getInt("idBirthState"), objOpponent.getString("birthStateName"),
                    objOpponent.getInt("charisma"), objOpponent.getInt("reputation"),
                    objOpponent.getInt("money"), objOpponent.getInt("createdByUser"),
                    objOpponent.getLong("changed"));
        }
        else {
            //ulozena hra

            //nacitanie triedy kandidata
            Bundle objCandidate = obj.getBundle("candidate");
            int objCharacterCharisma = objCandidate.getInt("charisma");
            int objCharacterReputation = objCandidate.getInt("reputation");
            int objCharacterMoney = objCandidate.getInt("money");
            int objCharacterId = objCandidate.getInt("idCharacter");
            Bundle objCharacter = getContentResolver().call(Contract.State.CONTENT_URI, "return character by idCharacter", String.valueOf(objCharacterId), null);
            candidate = new Candidate(objCharacter.getInt("id"), objCharacter.getString("nameSurname"),
                    objCharacter.getInt("idBirthRegion"), objCharacter.getString("birthRegionName"),
                    objCharacter.getInt("idBirthState"),
                    objCharacter.getString("birthStateName"), objCharacterCharisma,
                    objCharacterReputation,
                    objCharacterMoney, objCharacter.getInt("createdByUser"),
                    objCharacter.getLong("changed"));

            //nacitanie triedy oponenta
            Bundle objOpponent = obj.getBundle("opponent");
            int objOpponentCharisma = objOpponent.getInt("charisma");
            int objOpponentReputation = objOpponent.getInt("reputation");
            int objOpponentMoney = objOpponent.getInt("money");
            int objOpponentId = objOpponent.getInt("idCharacter");
            objCharacter = getContentResolver().call(Contract.State.CONTENT_URI, "return character by idCharacter", String.valueOf(objOpponentId), null);
            opponent = new Candidate(objCharacter.getInt("id"), objCharacter.getString("nameSurname"),
                    objCharacter.getInt("idBirthRegion"), objCharacter.getString("birthRegionName"),
                    objCharacter.getInt("idBirthState"),
                    objCharacter.getString("birthStateName"), objOpponentCharisma,
                    objOpponentReputation,
                    objOpponentMoney, objCharacter.getInt("createdByUser"),
                    objCharacter.getLong("changed"));
        }

        //nastavenie textviewov pre zobrazenie mena a priezviska kandidata a oponenta
        tvCandidateName.setText(candidate.getNameSurname());
        tvOpponentName.setText(opponent.getNameSurname());

        //nastavenie textviewov pre zobrazenie preferencii kandidata a oponenta
        setProCandidateTextViewAndProOpponentTextView();
    }


    /*
    *
    * funkcia pre pridanie objektov regionov, ktorymi pouzivatel zapina detail konkretneho regionu
    *
    * */
    private void addViewsOfRegions() {
        regionViews = new ArrayList<>();
        imageViewMap = findViewById(R.id.imageViewMapState);
        switch (stateName) {
            //ak pouzivatel vybral pre konanie volieb Slovensku republiku
            case "Slovenská republika" : {
                //nastavenie obrazka
                Drawable d = getResources().getDrawable(R.drawable.map_sr);
                imageViewMap.setImageResource(R.drawable.map_sr);

                //nastavenie premennych pre vysku a sirku obrazka
                final int imageViewHeight = d.getIntrinsicHeight();
                final int imageViewWidth = d.getIntrinsicWidth();

                //nastavenie objektu, kde sa budu vkladat objekty regionov
                relativeLayoutMapObjects = findViewById(R.id.relativeLayoutMapObjects);
                for (int i = 0; i < regions.size(); i++) {
                    int viewWidth = 0;
                    int viewHeight = 0;
                    int marginTop = 0;
                    int marginStart = 0;
                    int paddingStart = 0;
                    int paddingEnd = 0;
                    int paddingTop = 0;
                    int paddingBottom = 0;
                    int flagMarginTop = 0;
                    int flagMarginStart = 0;
                    Region region = regions.get(i);

                    //switch pre kazdy region v Slovenskej republike
                    switch (region.getRegionName()) {
                        case "Bratislavský kraj": {
                            viewWidth = (int)(imageViewWidth*0.09);
                            viewHeight = (int)(imageViewHeight*0.27);
                            marginTop = (int)(imageViewHeight*0.49);
                            marginStart = (int)(imageViewWidth*0.02);
                            paddingStart = (int)(imageViewWidth*0.01);
                            paddingBottom = (int)(imageViewHeight*0.02);
                            flagMarginTop = (int)(imageViewWidth*0.02);
                            flagMarginStart = (int)(imageViewHeight*0.07);
                            break;
                        }
                        case "Trnavský kraj": {
                            viewWidth = (int)(imageViewWidth*0.09);
                            viewHeight = (int)(imageViewHeight*0.42);
                            marginTop = (int)(imageViewHeight*0.5);
                            marginStart = (int)(imageViewWidth*0.11);
                            paddingBottom = (int)(imageViewHeight*0.12);
                            flagMarginTop = (int)(imageViewWidth*0.03);
                            flagMarginStart = (int)(imageViewHeight*0.1);
                            break;
                        }
                        case "Trenčiansky kraj": {
                            viewWidth = (int)(imageViewWidth*0.19);
                            viewHeight = (int)(imageViewHeight*0.24);
                            marginTop = (int)(imageViewHeight*0.28);
                            marginStart = (int)(imageViewWidth*0.16);
                            paddingStart = (int)(imageViewWidth*0.03);
                            paddingBottom = (int)(imageViewHeight*0.06);
                            flagMarginTop = (int)(imageViewWidth*0.05);
                            flagMarginStart = (int)(imageViewHeight*0.26);
                            break;
                        }
                        case "Nitriansky kraj": {
                            viewWidth = (int)(imageViewWidth*0.16);
                            viewHeight = (int)(imageViewHeight*0.48);
                            marginTop = (int)(imageViewHeight*0.52);
                            marginStart = (int)(imageViewWidth*0.2);
                            paddingEnd = (int)(imageViewWidth*0.03);
                            paddingTop = (int)(imageViewHeight*0.01);
                            flagMarginTop = (int)(imageViewWidth*0.12);
                            flagMarginStart = (int)(imageViewHeight*0.17);
                            break;
                        }
                        case "Žilinský kraj": {
                            viewWidth = (int)(imageViewWidth*0.23);
                            viewHeight = (int)(imageViewHeight*0.33);
                            marginTop = (int)(imageViewHeight*0.06);
                            marginStart = (int)(imageViewWidth*0.33);
                            paddingEnd = (int)(imageViewWidth*0.01);
                            paddingTop = (int)(imageViewHeight*0.01);
                            flagMarginTop = (int)(imageViewWidth*0.08);
                            flagMarginStart = (int)(imageViewHeight*0.27);
                            break;
                        }
                        case "Banskobystrický kraj": {
                            viewWidth = (int)(imageViewWidth*0.26);
                            viewHeight = (int)(imageViewHeight*0.42);
                            marginTop = (int)(imageViewHeight*0.39);
                            marginStart = (int)(imageViewWidth*0.36);
                            flagMarginTop = (int)(imageViewWidth*0.07);
                            flagMarginStart = (int)(imageViewHeight*0.32);
                            break;
                        }
                        case "Prešovský kraj": {
                            viewWidth = (int)(imageViewWidth*0.36);
                            viewHeight = (int)(imageViewHeight*0.28);
                            marginTop = (int)(imageViewHeight*0.15);
                            marginStart = (int)(imageViewWidth*0.615);
                            paddingEnd = (int)(imageViewWidth*0.03);
                            paddingTop = (int)(imageViewHeight*0.06);
                            flagMarginTop = (int)(imageViewWidth*0.045);
                            flagMarginStart = (int)(imageViewHeight*0.39);
                            break;
                        }
                        case "Košický kraj": {
                            viewWidth = (int)(imageViewWidth*0.36);
                            viewHeight = (int)(imageViewHeight*0.32);
                            marginTop = (int)(imageViewHeight*0.43);
                            marginStart = (int)(imageViewWidth*0.62);
                            paddingStart = (int)(imageViewWidth*0.08);
                            paddingBottom = (int)(imageViewHeight*0.08);
                            flagMarginTop = (int)(imageViewWidth*0.05);
                            flagMarginStart = (int)(imageViewHeight*0.5);
                            break;
                        }
                    }

                    //vytvorenie objektu, na ktory pouzivatel klikne a otvori sa mu detail regionu
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(viewWidth, viewHeight);
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
                    params.topMargin = marginTop;
                    params.setMarginStart(marginStart);
                    RelativeLayout relativeLayout = tf.getRelativeLayout(getApplicationContext(),params);
                    relativeLayout.setTag(i);

                    //nastavenie onClickListenera, ktory otvori detail regionu
                    relativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!disabledOnClick)
                                onClickRelativeLayoutRegionSelectRegion((RelativeLayout)view, imageViewWidth, imageViewHeight);
                        }
                    });

                    //objekt s prida do arraylistu regionViews
                    regionViews.add(relativeLayout);
                    relativeLayoutMapObjects.addView(relativeLayout);

                    //vytvorenie textviewu so skratkou regionu
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
                    TextView textView = tf.getTextView(getApplicationContext(),region.getAbbreviation(),-1,null,
                            getResources().getDimensionPixelSize(R.dimen.text_size_12sp),layoutParams,true,R.style.regular_blue_textview_style_size_10sp,"bold",true);
                    textView.setPadding(paddingStart,paddingTop,paddingEnd,paddingBottom);
                    //nastavenie farby textu podla preferencii obyvatelov regionu
                    if (region.getAfterProCharacter() > region.getAfterProOpponent())
                        textView.setTextColor(getResources().getColor(R.color.color_blue_light));
                    else if (region.getAfterProCharacter() < region.getAfterProOpponent())
                        textView.setTextColor(getResources().getColor(R.color.color_red_light));
                    else
                        textView.setTextColor(getResources().getColor(R.color.color_grey_dark));
                    relativeLayout.addView(textView);

                    //vytvorenie objektu oznacujuceho rodny region kandidata
                    if (candidate.getBirthRegionName().equals(region.getRegionName())) {
                        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.topMargin = flagMarginTop;
                        layoutParams.setMarginStart(flagMarginStart);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
                        ImageView imageView = tf.getImageView(getApplicationContext(), layoutParams, null, -1,
                                R.drawable.flag_candidate, 1, 0, 0, 0, 0);
                        imageView.setTag("flag");
                        relativeLayout.addView(imageView);
                    }
                    //vytvorenie objektu oznacujuceho rodny region oponenta
                    else if (opponent.getBirthRegionName().equals(region.getRegionName())) {
                        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.topMargin = flagMarginTop;
                        layoutParams.setMarginStart(flagMarginStart);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
                        ImageView imageView = tf.getImageView(getApplicationContext(), layoutParams, null, -1,
                                R.drawable.flag_opponent, 1, 0, 0, 0, 0);
                        imageView.setTag("flag");
                        relativeLayout.addView(imageView);
                    }
                }
                break;
            }

            //ak pouzivatel vybral pre konanie volieb Cesku republiku
            case "Česká republika" : {
                //nastavenie obrazka
                Drawable d = getResources().getDrawable(R.drawable.map_cr);
                imageViewMap.setImageResource(R.drawable.map_cr);

                //nastavenie premennych pre vysku a sirku obrazka
                final int imageViewHeight = d.getIntrinsicHeight();
                final int imageViewWidth = d.getIntrinsicWidth();

                //nastavenie objektu, kde sa budu vkladat objekty regionov
                relativeLayoutMapObjects = findViewById(R.id.relativeLayoutMapObjects);
                for (int i = 0; i < regions.size(); i++) {
                    int viewWidth = 0;
                    int viewHeight = 0;
                    int marginTop = 0;
                    int marginStart = 0;
                    int paddingStart = 0;
                    int paddingEnd = 0;
                    int paddingTop = 0;
                    int paddingBottom = 0;
                    int flagMarginTop = 0;
                    int flagMarginStart = 0;
                    Region region = regions.get(i);

                    //switch pre kazdy region v Ceskej republike
                    switch (region.getRegionName()) {
                        case "Jihočeský kraj": {
                            viewWidth = (int)(imageViewWidth*0.2);
                            viewHeight = (int)(imageViewHeight*0.4);
                            marginTop = (int)(imageViewHeight*0.58);
                            marginStart = (int)(imageViewWidth*0.22);
                            paddingBottom = (int)(imageViewHeight*0.05);
                            flagMarginTop = (int)(imageViewHeight*0.1);
                            flagMarginStart = (int)(imageViewWidth*0.13);
                            break;
                        }
                        case "Jihomoravský kraj": {
                            viewWidth = (int)(imageViewWidth*0.305);
                            viewHeight = (int)(imageViewHeight*0.37);
                            marginTop = (int)(imageViewHeight*0.62);
                            marginStart = (int)(imageViewWidth*0.48);
                            paddingStart = (int)(imageViewWidth*0.04);
                            paddingTop = (int)(imageViewHeight*0.05);
                            flagMarginTop = (int)(imageViewHeight*0.22);
                            flagMarginStart = (int)(imageViewWidth*0.195);
                            break;
                        }
                        case "Karlovarský kraj": {
                            viewWidth = (int)(imageViewWidth*0.18);
                            viewHeight = (int)(imageViewHeight*0.21);
                            marginTop = (int)(imageViewHeight*0.17);
                            marginStart = (int)(imageViewWidth*0.01);
                            flagMarginTop = (int)(imageViewHeight*0.09);
                            flagMarginStart = (int)(imageViewWidth*0.12);
                            break;
                        }
                        case "Královéhradecký kraj": {
                            viewWidth = (int)(imageViewWidth*0.19);
                            viewHeight = (int)(imageViewHeight*0.23);
                            marginTop = (int)(imageViewHeight*0.19);
                            marginStart = (int)(imageViewWidth*0.47);
                            paddingEnd = (int)(imageViewWidth*0.03);
                            flagMarginTop = (int)(imageViewHeight*0.1);
                            flagMarginStart = (int)(imageViewWidth*0.105);
                            break;
                        }
                        case "Liberecký kraj": {
                            viewWidth = (int)(imageViewWidth*0.17);
                            viewHeight = (int)(imageViewHeight*0.18);
                            marginTop = (int)(imageViewHeight*0.06);
                            marginStart = (int)(imageViewWidth*0.35);
                            paddingTop = (int)(imageViewHeight*0.05);
                            flagMarginTop = (int)(imageViewHeight*0.09);
                            flagMarginStart = (int)(imageViewWidth*0.11);
                            break;
                        }
                        case "Moravskoslezský kraj": {
                            viewWidth = (int)(imageViewWidth*0.2);
                            viewHeight = (int)(imageViewHeight*0.3);
                            marginTop = (int)(imageViewHeight*0.44);
                            marginStart = (int)(imageViewWidth*0.785);
                            paddingEnd = (int)(imageViewWidth*0.01);
                            paddingTop = (int)(imageViewHeight*0.05);
                            flagMarginTop = (int)(imageViewHeight*0.1);
                            flagMarginStart = (int)(imageViewWidth*0.045);
                            break;
                        }
                        case "Olomoucký kraj": {
                            viewWidth = (int)(imageViewWidth*0.105);
                            viewHeight = (int)(imageViewHeight*0.38);
                            marginTop = (int)(imageViewHeight*0.35);
                            marginStart = (int)(imageViewWidth*0.68);
                            paddingStart = (int)(imageViewWidth*0.02);
                            paddingTop = (int)(imageViewHeight*0.22);
                            flagMarginTop = (int)(imageViewHeight*0.21);
                            flagMarginStart = (int)(imageViewWidth*0.03);
                            break;
                        }
                        case "Pardubický kraj": {
                            viewWidth = (int)(imageViewWidth*0.21);
                            viewHeight = (int)(imageViewHeight*0.205);
                            marginTop = (int)(imageViewHeight*0.415);
                            marginStart = (int)(imageViewWidth*0.47);
                            paddingStart = (int)(imageViewWidth*0.05);
                            flagMarginTop = (int)(imageViewHeight*0.09);
                            flagMarginStart = (int)(imageViewWidth*0.16);
                            break;
                        }
                        case "Plzeňský kraj": {
                            viewWidth = (int)(imageViewWidth*0.17);
                            viewHeight = (int)(imageViewHeight*0.44);
                            marginTop = (int)(imageViewHeight*0.38);
                            marginStart = (int)(imageViewWidth*0.05);
                            paddingStart = (int)(imageViewWidth*0.03);
                            paddingBottom = (int)(imageViewHeight*0.19);
                            flagMarginTop = (int)(imageViewHeight*0.16);
                            flagMarginStart = (int)(imageViewWidth*0.1);
                            break;
                        }
                        case "Středočeský kraj": {
                            viewWidth = (int)(imageViewWidth*0.25);
                            viewHeight = (int)(imageViewHeight*0.34);
                            marginTop = (int)(imageViewHeight*0.24);
                            marginStart = (int)(imageViewWidth*0.22);
                            paddingStart = (int)(imageViewWidth*0.17);
                            paddingTop = (int)(imageViewHeight*0.13);
                            flagMarginTop = (int)(imageViewHeight*0.15);
                            flagMarginStart = (int)(imageViewWidth*0.2);
                            break;
                        }
                        case "Praha": {
                            viewWidth = (int)(imageViewWidth*0.07);
                            viewHeight = (int)(imageViewHeight*0.12);
                            marginTop = (int)(imageViewHeight*0.345);
                            marginStart = (int)(imageViewWidth*0.315);
                            flagMarginTop = (int)(imageViewHeight*0.0);
                            flagMarginStart = (int)(imageViewWidth*0.042);
                            break;
                        }
                        case "Ústecký kraj": {
                            viewWidth = (int)(imageViewWidth*0.16);
                            viewHeight = (int)(imageViewHeight*0.22);
                            marginTop = (int)(imageViewHeight*0.08);
                            marginStart = (int)(imageViewWidth*0.19);
                            paddingTop = (int)(imageViewHeight*0.03);
                            flagMarginTop = (int)(imageViewHeight*0.055);
                            flagMarginStart = (int)(imageViewWidth*0.11);
                            break;
                        }
                        case "Kraj Vysošina": {
                            viewWidth = (int)(imageViewWidth*0.18);
                            viewHeight = (int)(imageViewHeight*0.29);
                            marginTop = (int)(imageViewHeight*0.58);
                            marginStart = (int)(imageViewWidth*0.42);
                            paddingStart = (int)(imageViewWidth*0.02);
                            paddingBottom = (int)(imageViewHeight*0.06);
                            flagMarginTop = (int)(imageViewHeight*0.05);
                            flagMarginStart = (int)(imageViewWidth*0.135);
                            break;
                        }
                        case "Zlínsky kraj": {
                            viewWidth = (int)(imageViewWidth*0.2);
                            viewHeight = (int)(imageViewHeight*0.19);
                            marginTop = (int)(imageViewHeight*0.74);
                            marginStart = (int)(imageViewWidth*0.725);
                            paddingEnd = (int)(imageViewWidth*0.04);
                            flagMarginTop = (int)(imageViewHeight*0.03);
                            flagMarginStart = (int)(imageViewWidth*0.11);
                            break;
                        }
                    }
                    //vytvorenie objektu, na ktory pouzivatel klikne a otvori sa mu detail regionu
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(viewWidth, viewHeight);
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
                    params.topMargin = marginTop;
                    params.setMarginStart(marginStart);
                    RelativeLayout relativeLayout = tf.getRelativeLayout(getApplicationContext(),params);
                    relativeLayout.setTag(i);

                    //nastavenie onClickListenera, ktory otvori detail regionu
                    relativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!disabledOnClick)
                                onClickRelativeLayoutRegionSelectRegion((RelativeLayout)view, imageViewWidth, imageViewHeight);
                        }
                    });

                    //objekt s prida do arraylistu regionViews
                    regionViews.add(relativeLayout);
                    relativeLayoutMapObjects.addView(relativeLayout);

                    //vytvorenie textviewu so skratkou regionu
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
                    TextView textView = tf.getTextView(getApplicationContext(),region.getAbbreviation(),-1,null,
                            getResources().getDimensionPixelSize(R.dimen.text_size_12sp),layoutParams,true,R.style.regular_blue_textview_style_size_10sp,"bold",true);
                    textView.setPadding(paddingStart,paddingTop,paddingEnd,paddingBottom);
                    //nastavenie farby textu podla preferencii obyvatelov regionu
                    if (region.getAfterProCharacter() > region.getAfterProOpponent())
                        textView.setTextColor(getResources().getColor(R.color.color_blue_light));
                    else if (region.getAfterProCharacter() < region.getAfterProOpponent())
                        textView.setTextColor(getResources().getColor(R.color.color_red_light));
                    else
                        textView.setTextColor(getResources().getColor(R.color.color_grey_dark));
                    relativeLayout.addView(textView);

                    //vytvorenie objektu oznacujuceho rodny region kandidata
                    if (candidate.getBirthRegionName().equals(region.getRegionName())) {
                        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.topMargin = flagMarginTop;
                        layoutParams.setMarginStart(flagMarginStart);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
                        ImageView imageView = tf.getImageView(getApplicationContext(), layoutParams, null, -1,
                                R.drawable.flag_candidate, 1, 0, 0, 0, 0);
                        imageView.setTag("flag");
                        relativeLayout.addView(imageView);
                    }
                    //vytvorenie objektu oznacujuceho rodny region oponenta
                    else if (opponent.getBirthRegionName().equals(region.getRegionName())) {
                        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.topMargin = flagMarginTop;
                        layoutParams.setMarginStart(flagMarginStart);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
                        ImageView imageView = tf.getImageView(getApplicationContext(), layoutParams, null, -1,
                                R.drawable.flag_opponent, 1, 0, 0, 0, 0);
                        imageView.setTag("flag");
                        relativeLayout.addView(imageView);
                    }
                }
                break;
            }
            default: {
            }
        }
    }


    /*
    *
    * funkcia pre nastavovanie preferencii kandidata a oponenta vo vsetkych regionoch a zapis tychto hodnot do prislusnych textviewov
    *
    * */
    private void setProCandidateTextViewAndProOpponentTextView() {
        //vypocet preferencii
        double proCandidate = 0;
        double proOpponent = 0;
        long population = 0;
        for(int i = 0; i < regions.size(); i++) {
            Region region = regions.get(i);
            proCandidate += region.getAfterProCharacter();
            proOpponent += region.getAfterProOpponent();
            population += region.getPopulation();
        }

        //zapis hodnot do textview
        String text = tf.getPrettyFormattedTextFromNumberWithDecimal(tf.getPercent(proCandidate,population)) + " %";
        tvCandidatePercent.setText(text);
        text = tf.getPrettyFormattedTextFromNumberWithDecimal(tf.getPercent(proOpponent,population)) + " %";
        tvOpponentPercent.setText(text);
    }


    /*
    *
    * funkcia pre nastavenie viditelnosti objektov pre vsetky kraje, t.j. skratky krajov a vlajky oznacujuce rodne kraje kandidata a oponenta
    *
    * */
    private void setVisibilityOnRegionAbbreviations() {
        //tento cyklus prechadza vsetkymi krajmi v state
        for (int i = 0; i < relativeLayoutMapObjects.getChildCount(); i++) { // od 0 az po 7, lebo mam 8 krajov
            //jeden kraj v state
            RelativeLayout layout = (RelativeLayout) relativeLayoutMapObjects.getChildAt(i); //jeden kraj
            if (!layout.getTag().toString().equals("regionDetail")) {
                //TODO: pravdepodobne nasledujuce 2 riadky nepotrebujem, ale skontrolovat to
                for (int j = layout.getChildCount() - 1; j >= 2; j--)
                    layout.removeViewAt(j);
                //nastavenie viditelnosti textviewu so skratkou kraju
                layout.getChildAt(0).setVisibility(View.VISIBLE);
                //nastavenie viditelnosti imageviewu oznacujuceho rodny kraj kandidata alebo oponenta
                if (layout.getChildCount() >= 2 && layout.getChildAt(1).getTag().toString().equals("flag"))
                    layout.getChildAt(1).setVisibility(View.VISIBLE);
            }
        }
    }


    /*
    *
    * funkcia pre nastavenie onClickListenera pre objekty, pomocou ktorych sa vybera region a otvara sa detail regionu
    *
    * */
    private void onClickRelativeLayoutRegionSelectRegion(RelativeLayout relativeLayout, int imageViewWidth, int imageViewHeight) {
        int position = Integer.parseInt(relativeLayout.getTag().toString());
        Region region = regions.get(position);
        //podla nazvu regionu vyber obrazka so zvyraznenim regionom
        switch (region.getRegionName()) {
            case "Bratislavský kraj": {
                imageViewMap.setImageResource(R.drawable.map_sr_ba);
                break;
            }
            case "Trnavský kraj": {
                imageViewMap.setImageResource(R.drawable.map_sr_tn);
                break;
            }
            case "Trenčiansky kraj": {
                imageViewMap.setImageResource(R.drawable.map_sr_tt);
                break;
            }
            case "Nitriansky kraj": {
                imageViewMap.setImageResource(R.drawable.map_sr_nt);
                break;
            }
            case "Žilinský kraj": {
                imageViewMap.setImageResource(R.drawable.map_sr_za);
                break;
            }
            case "Banskobystrický kraj": {
                imageViewMap.setImageResource(R.drawable.map_sr_bb);
                break;
            }
            case "Prešovský kraj": {
                imageViewMap.setImageResource(R.drawable.map_sr_po);
                break;
            }
            case "Košický kraj": {
                imageViewMap.setImageResource(R.drawable.map_sr_ke);
                break;
            }
            case "Jihočeský kraj": {
                imageViewMap.setImageResource(R.drawable.map_cr_jhc);
                break;
            }
            case "Jihomoravský kraj": {
                imageViewMap.setImageResource(R.drawable.map_cr_jhm);
                break;
            }
            case "Karlovarský kraj": {
                imageViewMap.setImageResource(R.drawable.map_cr_kvk);
                break;
            }
            case "Královéhradecký kraj": {
                imageViewMap.setImageResource(R.drawable.map_cr_hkk);
                break;
            }
            case "Liberecký kraj": {
                imageViewMap.setImageResource(R.drawable.map_cr_lbk);
                break;
            }
            case "Moravskoslezský kraj": {
                imageViewMap.setImageResource(R.drawable.map_cr_msk);
                break;
            }
            case "Olomoucký kraj": {
                imageViewMap.setImageResource(R.drawable.map_cr_olk);
                break;
            }
            case "Pardubický kraj": {
                imageViewMap.setImageResource(R.drawable.map_cr_pak);
                break;
            }
            case "Plzeňský kraj": {
                imageViewMap.setImageResource(R.drawable.map_cr_plk);
                break;
            }
            case "Středočeský kraj": {
                imageViewMap.setImageResource(R.drawable.map_cr_stc);
                break;
            }
            case "Praha": {
                imageViewMap.setImageResource(R.drawable.map_cr_pha);
                break;
            }
            case "Ústecký kraj": {
                imageViewMap.setImageResource(R.drawable.map_cr_ulk);
                break;
            }
            case "Kraj Vysošina": {
                imageViewMap.setImageResource(R.drawable.map_cr_vys);
                break;
            }
            case "Zlínsky kraj": {
                imageViewMap.setImageResource(R.drawable.map_cr_zlk);
                break;
            }
        }

        //nastavenie viditelnosti skratiek regionov
        setVisibilityOnRegionAbbreviations();

        //nastavenie premennych popisujucich vybrany region
        idSelectedRegion = region.getId();
        positionSelectedRegion = position;

        //pre vybrany region nastavenie viditelnosti skratky regionu a pripadne aj vlajky oznacujucej rodny region kandidata a oponenta
        for (int i = 0; i < relativeLayout.getChildCount(); i++) {
            relativeLayout.getChildAt(i).setVisibility(View.INVISIBLE);
        }

        //nastavenie parametrov pre poziciu detailu regionu
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)relativeLayout.getLayoutParams();
        //urci sa vyska a sirka detailu regionu
        int relativeLayoutRegionDetailOptionsWidth = (int)(imageViewWidth*0.5);
        int relativeLayoutRegionDetailOptionsHeight = (int)(imageViewHeight*0.5);
        //nasledne sa urci stredny bod regionu, na ktory pouzivatel klikol
        int middlePointXWidth = layoutParams.getMarginStart() + layoutParams.width/2;
        int middlePointYHeight = layoutParams.topMargin + layoutParams.height/2;
        //urci sa odsadenie detailu regionu zlava
        int relativeLayoutRegionDetailOptionsMarginStart = middlePointXWidth - relativeLayoutRegionDetailOptionsWidth/2;
        if (relativeLayoutRegionDetailOptionsMarginStart < 0)
            relativeLayoutRegionDetailOptionsMarginStart = 0;
        else if (relativeLayoutRegionDetailOptionsMarginStart + relativeLayoutRegionDetailOptionsWidth > imageViewWidth)
            relativeLayoutRegionDetailOptionsMarginStart = imageViewWidth - relativeLayoutRegionDetailOptionsWidth;
        //urci sa odsadenie detailu regionu zhora
        int relativeLayoutRegionDetailOptionsMarginTop = middlePointYHeight - relativeLayoutRegionDetailOptionsHeight/2;
        if (relativeLayoutRegionDetailOptionsMarginTop < 0)
            relativeLayoutRegionDetailOptionsMarginTop = 0;
        else if (relativeLayoutRegionDetailOptionsMarginTop + relativeLayoutRegionDetailOptionsHeight > imageViewHeight)
            relativeLayoutRegionDetailOptionsMarginTop = imageViewHeight - relativeLayoutRegionDetailOptionsHeight;
        //z vypocitanych premennych sa odvodia parametre pre poziciu detailu regionu
        layoutParams = new RelativeLayout.LayoutParams(relativeLayoutRegionDetailOptionsWidth,
                relativeLayoutRegionDetailOptionsHeight);
        layoutParams.setMargins(relativeLayoutRegionDetailOptionsMarginStart,relativeLayoutRegionDetailOptionsMarginTop,0,0);

        if (relativeLayoutRegionDetailOptions == null) {
            //ak doteraz nebol detail regionu otvoreny, tak sa vytvori
            setRelativeLayoutRegionDetailOptions(layoutParams,region);
        }
        else {
            //v pripade, ak bol detail regionu uz otvoreny, tak sa len nastavi jeho viditelnost a obnovia sa textview
            relativeLayoutRegionDetailOptions.setVisibility(View.VISIBLE);
            relativeLayoutRegionDetailOptions.setLayoutParams(layoutParams);
            ((TextView)findViewById(idRegionNameTextviewInRelativeLayoutRegionDetailOptions)).setText(region.getRegionName());
            ((TextView)findViewById(idProCandidateTextviewInRelativeLayoutRegionDetailOptions)).setText(tf.getPrettyFormattedTextFromNumberWithDecimal(tf.getPercent(region.getAfterProCharacter(),region.getPopulation())) + " %");
            ((TextView)findViewById(idProOpponentTextviewInRelativeLayoutRegionDetailOptions)).setText(tf.getPrettyFormattedTextFromNumberWithDecimal(tf.getPercent(region.getAfterProOpponent(),region.getPopulation())) + " %");

            //ak vo vybranom regione je vyplnena posledna akcia, tak sa zobrazi
            if (region.getLastAction() != null) {
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams1.setMargins(0, getResources().getDimensionPixelSize(R.dimen.space_5dp), 0,
                        getResources().getDimensionPixelSize(R.dimen.space_5dp));
                TextView textView = findViewById(idLastActionTextviewInRelativeLayoutRegionDetailOptions);
                textView.setLayoutParams(layoutParams1);
                textView.setText(region.getLastAction());
            }
        }
    }


    /*
    *
    * funkcia pre nastavenie detailu regionu
    *
    * */
    private void setRelativeLayoutRegionDetailOptions(RelativeLayout.LayoutParams layoutParams, final Region region) {
        //vytvorenie objektu
        relativeLayoutRegionDetailOptions = tf.getRelativeLayout(getApplicationContext(), layoutParams);
        relativeLayoutRegionDetailOptions.setTag("regionDetail");
        relativeLayoutMapObjects.addView(relativeLayoutRegionDetailOptions);

        //nastavenie pozadia
        layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        ImageView imageView = tf.getImageView(getApplicationContext(), layoutParams, getResources().getDrawable(R.drawable.blurred_oval), -1, -1, (float) 0.8, 0, 0, 0, 0);
        //pre pozadie sa nastavi onClickListener, ktorym sa skryje detail regionu
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!disabledOnClick)
                    onClickRelativeLayoutRegionUnselectRegion();
            }
        });
        relativeLayoutRegionDetailOptions.addView(imageView);

        //pridanie LinearLayoutu, do ktoreho sa vlozia vsetky objekty
        layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        LinearLayout linearLayout = tf.getLinearLayout(getApplicationContext(), -1, layoutParams, LinearLayout.VERTICAL,
                Gravity.CENTER, 0, 0, 0, 0);
        relativeLayoutRegionDetailOptions.addView(linearLayout);

        //pridanie textViewu s nazvom regionu
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams1.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.space_5dp));
        TextView textView = tf.getTextView(getApplicationContext(), region.getRegionName(), -1, null,
                getResources().getDimensionPixelSize(R.dimen.text_size_12sp), layoutParams, true, R.style.regular_blue_textview_style_size_12sp,"bold",true);
        idRegionNameTextviewInRelativeLayoutRegionDetailOptions = textView.getId();
        linearLayout.addView(textView);

        //vytvorenie LinearLayoutu pre preferencie volicov vo vybranom kraji
        layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayoutPreferences = tf.getLinearLayout(getApplicationContext(), -1, layoutParams, LinearLayout.HORIZONTAL,
                Gravity.CENTER_VERTICAL, 0, getResources().getDimensionPixelSize(R.dimen.space_3dp), 0, 0);
        linearLayout.addView(linearLayoutPreferences);

        //pridanie obrazku s ikonkou "like"
        imageView = tf.getImageView(getApplicationContext(), layoutParams1, getResources().getDrawable(R.drawable.blue_border_and_fill), -1, R.drawable.like, -1,
                getResources().getDimensionPixelSize(R.dimen.space_5dp), getResources().getDimensionPixelSize(R.dimen.space_5dp), getResources().getDimensionPixelSize(R.dimen.space_5dp),
                getResources().getDimensionPixelSize(R.dimen.space_5dp));
        linearLayoutPreferences.addView(imageView);

        //pridanie textu s percentom volicov, ktori budu volit kandidata
        String text = tf.getPrettyFormattedTextFromNumberWithDecimal(tf.getPercent(region.getAfterProCharacter(),region.getPopulation())) + " %";
        textView = tf.getTextView(getApplicationContext(), text, -1, null, getResources().getDimensionPixelSize(R.dimen.text_size_10sp), layoutParams1, false,
                R.style.regular_blue_textview_style_size_10sp,"bold",true);
        textView.setPadding(getResources().getDimensionPixelSize(R.dimen.space_5dp), 0, getResources().getDimensionPixelSize(R.dimen.space_5dp), 0);
        idProCandidateTextviewInRelativeLayoutRegionDetailOptions = textView.getId();
        linearLayoutPreferences.addView(textView);

        //pridanie obrazku s ikonkou "dislike"
        imageView = tf.getImageView(getApplicationContext(), layoutParams1, getResources().getDrawable(R.drawable.red_border_and_fill), -1, R.drawable.dislike, -1,
                getResources().getDimensionPixelSize(R.dimen.space_5dp), getResources().getDimensionPixelSize(R.dimen.space_5dp), getResources().getDimensionPixelSize(R.dimen.space_5dp),
                getResources().getDimensionPixelSize(R.dimen.space_5dp));
        linearLayoutPreferences.addView(imageView);

        //pridanie textu s percentom volicov, ktori budu volit oponenta
        text = tf.getPrettyFormattedTextFromNumberWithDecimal(tf.getPercent(region.getAfterProOpponent(),region.getPopulation())) + " %";
        textView = tf.getTextView(getApplicationContext(), text, -1, null,
                getResources().getDimensionPixelSize(R.dimen.text_size_10sp), layoutParams1, false, R.style.regular_red_textview_style_size_10sp,"bold",true);
        textView.setPadding(getResources().getDimensionPixelSize(R.dimen.space_5dp), 0, getResources().getDimensionPixelSize(R.dimen.space_5dp), 0);
        idProOpponentTextviewInRelativeLayoutRegionDetailOptions = textView.getId();
        linearLayoutPreferences.addView(textView);

        //rozlisenie, ci pre region je vyplnena posledna akcia
        if (region.getLastAction() != null) {
            layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1.setMargins(0, getResources().getDimensionPixelSize(R.dimen.space_5dp), 0,
                    getResources().getDimensionPixelSize(R.dimen.space_5dp));
        }
        else {
            layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1.setMargins(0, 0, 0,0);
        }
        //pridanie textu s poslednou akciou vykonanou v regione
        textView = tf.getTextView(getApplicationContext(), region.getLastAction(), -1, null,
                getResources().getDimensionPixelSize(R.dimen.text_size_10sp), layoutParams1, true, R.style.regular_grey_textview_style_size_10sp,"regular",false);
        idLastActionTextviewInRelativeLayoutRegionDetailOptions = textView.getId();
        linearLayout.addView(textView);

        //vytvorenie LinearLayoutu, kde pridam tlacidla pre vykonanie investicie alebo poskytnutie rozhovoru
        layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams1.setMargins(0, 0, 0, 0);
        LinearLayout linearLayoutOptions = tf.getLinearLayout(getApplicationContext(), -1, layoutParams1,
                LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL, 0,0,0, 0);
        linearLayout.addView(linearLayoutOptions);

        //pridanie tlacidla pre vykonanie investicie
        Button button = tf.getButton(getApplicationContext(), layoutParams1, getResources().getString(R.string.investment),
                R.style.bold_blue_button_style, getResources().getDimensionPixelSize(R.dimen.text_size_12sp),
                getResources().getDimensionPixelSize(R.dimen.space_5dp), getResources().getDimensionPixelSize(R.dimen.space_3dp),
                getResources().getDimensionPixelSize(R.dimen.space_5dp), getResources().getDimensionPixelSize(R.dimen.space_3dp));
        idInvestmentButtonInRelativeLayoutRegionDetailOptions = button.getId();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!disabledOnClick)
                    setDialogForChooseGameAction(region,"investment");
            }
        });
        linearLayoutOptions.addView(button);

        //pridanie tlacidla pre poskytnutie interview
        button = tf.getButton(getApplicationContext(), layoutParams1, getResources().getString(R.string.interview), R.style.bold_red_button_style,
                getResources().getDimensionPixelSize(R.dimen.text_size_12sp),
                getResources().getDimensionPixelSize(R.dimen.space_5dp), getResources().getDimensionPixelSize(R.dimen.space_3dp),
                getResources().getDimensionPixelSize(R.dimen.space_5dp), getResources().getDimensionPixelSize(R.dimen.space_3dp));
        idInterviewButtonInRelativeLayoutRegionDetailOptions = button.getId();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!disabledOnClick)
                    setDialogForChooseGameAction(region,"interview");
            }
        });
        linearLayoutOptions.addView(button);
    }


    /*
    *
    * funkcie pre nastavenie onClickListenera na pozadie detailu regionu, ktorym sa vypina detail regionu
    *
    * */
    private void onClickRelativeLayoutRegionUnselectRegion() {
        //nastavenie mapy
        switch (stateName) {
            case "Slovenská republika": {
                imageViewMap.setImageResource(R.drawable.map_sr);
                break;
            }
            case "Česká republika": {
                imageViewMap.setImageResource(R.drawable.map_cr);
                break;
            }
        }

        //nastavenie premennych popisujucich vybrany region
        idSelectedRegion = -1;
        positionSelectedRegion = -1;

        //nastavenie viditelnosti vsetkych skratiek regionu a vlajok oznacujucich rodne regiony kandidata a oponenta
        setVisibilityOnRegionAbbreviations();

        //nastavenie viditelnosti detailu regionu
        relativeLayoutRegionDetailOptions.setVisibility(View.INVISIBLE);
    }


    /*
    *
    * funkcia pre nastavenie dialogu, kde si pouzivatel vybera oblast investicie alebo temu interview
    *
    * */
    private void setDialogForChooseGameAction(final Region region, final String actionType) {
        //nacitanie vsetkych riesenych problemov z DB podla nazvu regionu a typu akcie
        String where = " WHERE " + (Contract.Region.TABLE_NAME + "." + Contract.Region.REGION_NAME) + "='" + region.getRegionName() + "'" +
                " AND " + (Contract.Problems.TABLE_NAME + "." + Contract.Problems.ACTION_TYPE) + "='" + actionType + "';";
        Bundle obj = getContentResolver().call(Contract.Region.CONTENT_URI,"return all problems of region by regionName and actionType",where,null);
        int count = obj.getInt("count");

        //naplnenie arrayListu problems problemami z DB
        problems = new ArrayList<>();
        ArrayList<String> problemNames = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Bundle row = obj.getBundle("row" + String.valueOf(i));
            problems.add(new Problem(row.getInt("id"), row.getString("problemName"), row.getInt("idRegion"),
                    row.getString("actionType"), row.getDouble("effect"), row.getDouble("solvability"), row.getLong("changed")));
            problemNames.add(row.getString("problemName"));
        }

        //vytvorenie dialogu
        final Dialog dialog = tf.getDialog(GameActivity.this,true,R.layout.dialog_choose_game_action);
        dialog.show();

        //nastavenie textViewu v lavej casti dialogu, ktory popisuje, co ma pouzivatel urobit
        switch (actionType) {
            case "investment": {
                ((TextView)dialog.findViewById(R.id.textViewChooseProblemOfActionType)).setText(getResources().getString(R.string.choose_investment_area));
                break;
            }
            case "interview": {
                ((TextView)dialog.findViewById(R.id.textViewChooseProblemOfActionType)).setText(getResources().getString(R.string.choose_interview_theme));
                break;
            }
        }

        //nastavenie objektu radioGroup so zoznamom problemov
        (dialog.findViewById(R.id.linearLayout)).setOnClickListener(playMusicOnClickListener);
        radioGroupListOfProblems = dialog.findViewById(R.id.radioGroupListOfProblems);
        radioGroupListOfProblems.setOnClickListener(playMusicOnClickListener);
        for (int i = 0; i < problemNames.size(); ++i) {
            //vytvorenie jednej polozky zoznamu s problemami
            MyRadioButton radioButton = new MyRadioButton(getApplicationContext());
            radioGroupListOfProblems.addView(radioButton);
            radioButton.setText(problemNames.get(i));
            radioButton.setChecked(false);
            radioButton.setTag(i);
            //nastavenie onClickListenera, ktory meni lavu cast dialogu
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!disabledOnClick) {
                        if (actualOnStep.getIntValue() == 1)
                            tf.playMusic(getApplicationContext());
                        MyRadioButton radioButton1 = ((MyRadioButton) view);

                        if (radioButton1.getChecked()) {
                            //odznacenie radioButtonu, v lavej casti dialogu sa objavi text pre popis, co ma pouzivatel urobit
                            radioButton1.setChecked(false);
                            (dialog.findViewById(R.id.textViewChooseProblemOfActionType)).setVisibility(View.VISIBLE);
                            (dialog.findViewById(R.id.constraintLayoutDetailProblem)).setVisibility(View.INVISIBLE);
                            switch (actionType) {
                                case "investment": {
                                    ((TextView) dialog.findViewById(R.id.textViewChooseProblemOfActionType)).setText(getResources().getString(R.string.choose_investment_area));
                                    break;
                                }
                                case "interview": {
                                    ((TextView) dialog.findViewById(R.id.textViewChooseProblemOfActionType)).setText(getResources().getString(R.string.choose_interview_theme));
                                    break;
                                }
                            }
                        } else {
                            //oznacenie radioButtonu, v lavej casti dialogu sa objavi detail vybraneho problemu
                            radioButton1.setChecked(true);
                            final int position = Integer.parseInt(radioButton1.getTag().toString());
                            for (int j = 0; j < position; j++) {
                                ((MyRadioButton) radioGroupListOfProblems.getChildAt(j)).setChecked(false);
                            }
                            for (int j = position + 1; j < radioGroupListOfProblems.getChildCount(); j++) {
                                ((MyRadioButton) radioGroupListOfProblems.getChildAt(j)).setChecked(false);
                            }
                            (dialog.findViewById(R.id.textViewChooseProblemOfActionType)).setVisibility(View.INVISIBLE);
                            (dialog.findViewById(R.id.constraintLayoutDetailProblem)).setVisibility(View.VISIBLE);
                            Problem problem = problems.get(position);
                            ((TextView) dialog.findViewById(R.id.textViewProblemName)).setText(problem.getProblemName());
                            if (recommendation == 1) { //zobrazovanie napoved
                                getLevelFromNumber((TextView) dialog.findViewById(R.id.textViewProblemEffect), problem.getEffect(),"effect");
                                getLevelFromNumber((TextView) dialog.findViewById(R.id.textViewProblemSolvability), problem.getSolvability(),"solvability");
                            } else {
                                (dialog.findViewById(R.id.linearLayoutRecommendation)).setVisibility(View.GONE);
                            }

                            //nastavenie tlacidla pre vykonanie akcie
                            buttonDoAction = dialog.findViewById(R.id.buttonDoAction);
                            buttonDoAction.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(!disabledOnClick) {
                                        dialog.dismiss();
                                        if (actualOnStep.getIntValue() == 1)
                                            tf.playMusic(getApplicationContext());

                                        //nastavenie regionov pred vykonanim akcie
                                        setRegionsBeforeAction(actionType, problems.get(position));

                                        //vyber regionov, ktore budu zaradene do historie hry, cize vyber tych, ktorych sa akcia dotkla
                                        ArrayList<Region> regionsToAdd = new ArrayList<>();
                                        for (int i = 0; i < regions.size(); i++) {
                                            Region region = regions.get(i);
                                            if (region.getIdProblem() != 0)
                                                regionsToAdd.add(new Region(region.getId(), region.getRegionName(), region.getAbbreviation(), region.getPopulation(),
                                                        region.getCharacter(), region.getIdProblem(), region.getRegionOfAction(), region.getWeek(),
                                                        region.getLastAction(), region.getActionType(), region.getBeforeProCharacter(), region.getBeforeUndecided(),
                                                        region.getBeforeProOpponent(), region.getAfterProCharacter(), region.getAfterUndecided(),
                                                        region.getAfterProOpponent()));

                                        }
                                        actionOfGames.add(new ActionOfGame(actualWeek.getIntValue(), actualOnStep.getIntValue(), actionType, regionsToAdd));

                                        //nastavenie regionov po vykonani akcie
                                        setRegionsAfterAction();

                                        //zmena celkovych preferencii volicov
                                        setProCandidateTextViewAndProOpponentTextView();

                                        //aktualizacia hodnot pre dany region v zobrazenom detaile regionu
                                        Region selectedRegion = regions.get(positionSelectedRegion);
                                        String text = tf.getPrettyFormattedTextFromNumberWithDecimal(tf.getPercent(selectedRegion.getAfterProCharacter(),selectedRegion.getPopulation())) + " %";
                                        ((TextView)findViewById(idProCandidateTextviewInRelativeLayoutRegionDetailOptions)).setText(text);
                                        text = tf.getPrettyFormattedTextFromNumberWithDecimal(tf.getPercent(selectedRegion.getAfterProOpponent(),selectedRegion.getPopulation())) + " %";
                                        ((TextView)findViewById(idProOpponentTextviewInRelativeLayoutRegionDetailOptions)).setText(text);
                                        setTextColorOfRegionAbbreviation();

                                        //detail regionu bude zobrazeny este 1 sekundu, aby bolo viditelne, ze sa zmenili preferencie voliov za vybrany kraj
                                        disabledOnClick = true;
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                disabledOnClick = false;
                                                onClickRelativeLayoutRegionUnselectRegion();

                                                //ak prave tiahol pouzivatel
                                                if (actualOnStep.getIntValue() == 1) {
                                                    if (firstStepInGame == 1)
                                                        //ak prvy na tahu je kandidat
                                                        actualOnStep.setValue(0);
                                                    else {
                                                        //ak prvy na tahu je oponent
                                                        if (actualWeek.getIntValue() + 1 <= numberOfWeeks) {
                                                            //ak este nekonci volebna kampan
                                                            actualOnStep.setValue(0);
                                                            actualWeek.setValue(actualWeek.getIntValue() + 1);
                                                        } else
                                                            //ak konci volebna kampan
                                                            EndOfGame();
                                                    }
                                                }
                                                //ak prave tiahol oponent
                                                else if (actualOnStep.getIntValue() == 0) {
                                                    if (firstStepInGame == 0)
                                                        //ak prvy na tahu je oponent
                                                        actualOnStep.setValue(1);
                                                    else {
                                                        //ak prvy na tahu je kandidat
                                                        if (actualWeek.getIntValue() + 1 <= numberOfWeeks) {
                                                            //ak este nekonci volebna kampan
                                                            actualOnStep.setValue(1);
                                                            actualWeek.setValue(actualWeek.getIntValue() + 1);
                                                        } else
                                                            //ak konci volebna kampan
                                                            EndOfGame();
                                                    }
                                                }
                                            }
                                        }, 1000);
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }


    /*
    *
    * funkcia pre vratenie zavaznosti vplyvu problemu na volicov vo vybranom regione
    * pouziva sa pre urcenie efektu a riesitelnosti problemu v dialogu pre vyber akcie
    *
    * */
    private void getLevelFromNumber(TextView textView, double number, String numberType) {
        switch (numberType) {
            case "effect": { //-2 az 2
                if (number < -0.5) {
                    textView.setText(getResources().getString(R.string.negative));
                    textView.setTextAppearance(getApplicationContext(),R.style.regular_grey_textview_style_size_12sp);
                }
                else if (number >= -0.5 && number <= 0.5) {
                    textView.setText(getResources().getString(R.string.neutral));
                    textView.setTextAppearance(getApplicationContext(),R.style.regular_grey_textview_style_size_12sp);
                }
                else if (number > 0.5) {
                    textView.setText(getResources().getString(R.string.positive));
                    textView.setTextAppearance(getApplicationContext(),R.style.regular_red_textview_style_size_12sp);
                }
                break;
            }
            case "solvability": {//1 az 5
                if (number < 2.5) {
                    textView.setText(getResources().getString(R.string.low));
                    textView.setTextAppearance(getApplicationContext(),R.style.regular_grey_textview_style_size_12sp);
                }
                else if (number >= 2.5 && number < 3.5) {
                    textView.setText(getResources().getString(R.string.middle));
                    textView.setTextAppearance(getApplicationContext(),R.style.regular_grey_textview_style_size_12sp);
                }
                else if (number >= 3.5) {
                    textView.setText(getResources().getString(R.string.high));
                    textView.setTextAppearance(getApplicationContext(),R.style.regular_red_textview_style_size_12sp);
                }
                break;
            }
        }
    }


    /*
    *
    * funkcia pre nastavenie farby textu skratky regionu
    *
    * */
    private void setTextColorOfRegionAbbreviation() {
        for (int i = 0; i < regions.size(); i++) {
            Region region = regions.get(i);
            TextView textView = (TextView) regionViews.get(i).getChildAt(0);
            if (region.getAfterProCharacter() > region.getAfterProOpponent())
                //viac volicov, ktori budu volit kandidata
                textView.setTextColor(getResources().getColor(R.color.color_blue_light));
            else if (region.getAfterProCharacter() < region.getAfterProOpponent())
                //viac volicov, ktori budu volit oponenta
                textView.setTextColor(getResources().getColor(R.color.color_red_light));
            else
                //vyvazene, rovnaky pocet volicov, ktori budu volit kandidata a aj oponenta
                textView.setTextColor(getResources().getColor(R.color.color_grey_dark));
        }
    }


    /*
    *
    * funkcia pre ulozenie hry do DB
    *
    * */
    @SuppressLint("HandlerLeak")
    private void saveGame() {
        //vlozenie dat do tabulky SAVED_GAME
        ContentValues contentValue = new ContentValues();
        contentValue.put(Contract.SavedGame.ACTUAL_ELECTION_WEEK, actualWeek.getIntValue());
        contentValue.put(Contract.SavedGame.NUMBER_OF_ELECTIONS_WEEK, numberOfWeeks);
        contentValue.put(Contract.SavedGame.ON_THE_STEP, actualOnStep.getIntValue());
        contentValue.put(Contract.SavedGame.FIRST_STEP_IN_GAME, firstStepInGame);
        contentValue.put(Contract.SavedGame.RECOMMENDATION, recommendation);
        contentValue.put(Contract.SavedGame.CHANGED, System.currentTimeMillis());
        AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {};
        asyncQueryHandler.startInsert(0,null,Contract.SavedGame.CONTENT_URI,contentValue);

        //dopyt do DB pre id vlozenej hry v tabulke SAVED_GAME
        asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
                cursor.moveToLast();
                idSavedGame = cursor.getInt(Contract.SavedGame.COLUMN_INDEX_ID);

                //vlozenie dat do tabulky SAVED_CHARACTER
                ContentValues[] contentValues = new ContentValues[2];
                contentValues[0] = new ContentValues();
                contentValues[0].put(Contract.SavedCandidate.ID_CANDIDATE, candidate.getId());
                contentValues[0].put(Contract.SavedCandidate.ID_SAVED_GAME, idSavedGame);
                contentValues[0].put(Contract.SavedCandidate.CANDIDATE, 1);
                contentValues[0].put(Contract.SavedCandidate.CHARISMA, candidate.getCharisma());
                contentValues[0].put(Contract.SavedCandidate.REPUTATION, candidate.getReputation());
                contentValues[0].put(Contract.SavedCandidate.MONEY, candidate.getMoney());
                contentValues[0].put(Contract.SavedCandidate.CHANGED, System.currentTimeMillis());
                contentValues[1] = new ContentValues();
                contentValues[1].put(Contract.SavedCandidate.ID_CANDIDATE, opponent.getId());
                contentValues[1].put(Contract.SavedCandidate.ID_SAVED_GAME, idSavedGame);
                contentValues[1].put(Contract.SavedCandidate.CANDIDATE, 0);
                contentValues[1].put(Contract.SavedCandidate.CHARISMA, opponent.getCharisma());
                contentValues[1].put(Contract.SavedCandidate.REPUTATION, opponent.getReputation());
                contentValues[1].put(Contract.SavedCandidate.MONEY, opponent.getMoney());
                contentValues[1].put(Contract.SavedCandidate.CHANGED, System.currentTimeMillis());
                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        getContentResolver().bulkInsert(Contract.SavedCandidate.CONTENT_URI,(ContentValues[]) params);
                        return null;
                    }
                }.execute(contentValues);

                //vlozenie dat do tabulky SAVED_REGION
                contentValues = new ContentValues[regions.size()];
                for(int i = 0; i < regions.size(); i++) {
                    Region region = regions.get(i);
                    contentValues[i] = new ContentValues();
                    contentValues[i].put(Contract.SavedRegion.ID_SAVED_GAME, idSavedGame);
                    contentValues[i].put(Contract.SavedRegion.ID_REGION, region.getId());
                    contentValues[i].put(Contract.SavedRegion.PRO_CANDIDATE, region.getAfterProCharacter());
                    contentValues[i].put(Contract.SavedRegion.UNDECIDED, region.getAfterUndecided());
                    contentValues[i].put(Contract.SavedRegion.PRO_OPPONENT, region.getAfterProOpponent());
                    contentValues[i].put(Contract.SavedRegion.CHANGED, System.currentTimeMillis());
                }
                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        getContentResolver().bulkInsert(Contract.SavedRegion.CONTENT_URI,(ContentValues[]) params);
                        return null;
                    }
                }.execute(contentValues);

                //vlozenie dat do tabulky SAVED_HISTORY
                for(int i = 0; i < actionOfGames.size(); i++) {
                    final ActionOfGame actionOfGame = actionOfGames.get(i);
                    final ArrayList<Region> regions = actionOfGame.getRegionsOfGames();

                    ContentValues contentValues1 = new ContentValues();
                    contentValues1.put(Contract.SavedHistory.ID_SAVED_GAME, idSavedGame);
                    contentValues1.put(Contract.SavedHistory.WEEK, actionOfGame.getWeek());
                    contentValues1.put(Contract.SavedHistory.CHANGED, System.currentTimeMillis());

                    AsyncQueryHandler asyncQueryHandler1 = new AsyncQueryHandler(getContentResolver()) {
                        @Override
                        protected void onInsertComplete(int token, Object cookie, Uri uri) {
                            super.onInsertComplete(token, cookie, uri);

                            //vlozenie dat do tabulky REGION_OF_SAVED_HISTORY
                            String uriString = uri.toString();
                            int idSavedHistory = Integer.parseInt(uriString.substring(uriString.lastIndexOf('/') + 1,uriString.length()));

                            ContentValues[] contentValues2 = new ContentValues[regions.size()];
                            for (int j = 0; j < regions.size(); j++) {
                                Region region = regions.get(j);
                                contentValues2[j] = new ContentValues();
                                contentValues2[j].put(Contract.RegionOfSavedHistory.ID_SAVED_HISTORY,idSavedHistory);
                                contentValues2[j].put(Contract.RegionOfSavedHistory.CANDIDATE, actionOfGame.getCharacter());
                                contentValues2[j].put(Contract.RegionOfSavedHistory.ID_REGION, region.getId());
                                if (region.getRegionOfAction())
                                    contentValues2[j].put(Contract.RegionOfSavedHistory.REGION_OF_ACTION, 1);
                                else
                                    contentValues2[j].put(Contract.RegionOfSavedHistory.REGION_OF_ACTION, 0);
                                contentValues2[j].put(Contract.RegionOfSavedHistory.ID_PROBLEM, region.getIdProblem());
                                contentValues2[j].put(Contract.RegionOfSavedHistory.BEFORE_PRO_CANDIDATE, region.getBeforeProCharacter());
                                contentValues2[j].put(Contract.RegionOfSavedHistory.BEFORE_UNDECIDED, region.getBeforeUndecided());
                                contentValues2[j].put(Contract.RegionOfSavedHistory.BEFORE_PRO_OPPONENT, region.getBeforeProOpponent());
                                contentValues2[j].put(Contract.RegionOfSavedHistory.AFTER_PRO_CANDIDATE, region.getAfterProCharacter());
                                contentValues2[j].put(Contract.RegionOfSavedHistory.AFTER_UNDECIDED, region.getAfterUndecided());
                                contentValues2[j].put(Contract.RegionOfSavedHistory.AFTER_PRO_OPPONENT, region.getAfterProOpponent());
                                contentValues2[j].put(Contract.RegionOfSavedHistory.CHANGED, System.currentTimeMillis());
                            }
                            new AsyncTask() {
                                @Override
                                protected Object doInBackground(Object[] params) {
                                    getContentResolver().bulkInsert(Contract.RegionOfSavedHistory.CONTENT_URI,(ContentValues[]) params);
                                    return null;
                                }
                            }.execute(contentValues2);
                        }
                    };
                    asyncQueryHandler1.startInsert(0,null, Contract.SavedHistory.CONTENT_URI,contentValues1);
                }
            }
        };
        asyncQueryHandler.startQuery(0,null,Contract.SavedGame.CONTENT_URI,null,null,null,null);
    }


    /*
    *
    * funkcia pre aktualizaciu ulozenej hry v DB
    *
    * */
    @SuppressLint("HandlerLeak")
    private void updateSavedGame() {
        //aktualizacia dat v tabulke SAVED_GAME
        ContentValues contentValue = new ContentValues();
        contentValue.put(Contract.SavedGame.ACTUAL_ELECTION_WEEK, actualWeek.getIntValue());
        contentValue.put(Contract.SavedGame.ON_THE_STEP, actualOnStep.getIntValue());
        contentValue.put(Contract.SavedGame.CHANGED, System.currentTimeMillis());
        AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {};
        Uri uri = ContentUris.withAppendedId(Contract.SavedGame.CONTENT_URI, idSavedGame);
        asyncQueryHandler.startUpdate(0,null,uri,contentValue,null,null);

        //aktualizacia dat v tabulke SAVED_CHARACTER
        asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
                cursor.moveToFirst();
                do {
                    if (cursor.getInt(Contract.SavedCandidate.COLUMN_INDEX_CANDIDATE) == 1) { //charakter
                        int idSavedCharacter = cursor.getInt(Contract.SavedCandidate.COLUMN_INDEX_ID);
                        ContentValues contentValue = new ContentValues();
                        contentValue.put(Contract.SavedCandidate.CHARISMA, candidate.getCharisma());
                        contentValue.put(Contract.SavedCandidate.REPUTATION, candidate.getReputation());
                        contentValue.put(Contract.SavedCandidate.MONEY, candidate.getMoney());
                        contentValue.put(Contract.SavedCandidate.CHANGED, System.currentTimeMillis());
                        AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {};
                        Uri uri = ContentUris.withAppendedId(Contract.SavedCandidate.CONTENT_URI, idSavedCharacter);
                        asyncQueryHandler.startUpdate(0,null,uri,contentValue,null,null);
                    } else if (cursor.getInt(Contract.SavedCandidate.COLUMN_INDEX_CANDIDATE) == 0) {//oponent
                        int idSavedCharacter = cursor.getInt(Contract.SavedCandidate.COLUMN_INDEX_ID);
                        ContentValues contentValue = new ContentValues();
                        contentValue.put(Contract.SavedCandidate.CHARISMA, opponent.getCharisma());
                        contentValue.put(Contract.SavedCandidate.REPUTATION, opponent.getReputation());
                        contentValue.put(Contract.SavedCandidate.MONEY, opponent.getMoney());
                        contentValue.put(Contract.SavedCandidate.CHANGED, System.currentTimeMillis());
                        AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {};
                        Uri uri = ContentUris.withAppendedId(Contract.SavedCandidate.CONTENT_URI, idSavedCharacter);
                        asyncQueryHandler.startUpdate(0,null,uri,contentValue,null,null);
                    }
                }while (cursor.moveToNext());
            }
        };
        asyncQueryHandler.startQuery(0,null, Contract.SavedCandidate.CONTENT_URI,null, Contract.SavedCandidate.ID_SAVED_GAME + "=?",new String[] {String.valueOf(idSavedGame)},null);

        //aktualizacia dat v tabulke SAVED_REGION
        asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
                cursor.moveToFirst();
                do {
                    int idRegion = cursor.getInt(Contract.SavedRegion.COLUMN_INDEX_ID_REGION);
                    for(int i = 0; i < regions.size(); i++) {
                        Region region = regions.get(i);
                        if (idRegion == region.getId()) { //nasla som hladany region
                            ContentValues contentValue = new ContentValues();
                            double proCharacter = region.getAfterProCharacter();
                            double undecided = region.getAfterUndecided();
                            double proOpponent = region.getAfterProOpponent();
                            contentValue.put(Contract.SavedRegion.PRO_CANDIDATE, proCharacter);
                            contentValue.put(Contract.SavedRegion.UNDECIDED, undecided);
                            contentValue.put(Contract.SavedRegion.PRO_OPPONENT, proOpponent);
                            contentValue.put(Contract.SavedRegion.CHANGED, System.currentTimeMillis());
                            AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {};
                            int idSavedRegion = cursor.getInt(Contract.SavedRegion.COLUMN_INDEX_ID);
                            Uri uri = ContentUris.withAppendedId(Contract.SavedRegion.CONTENT_URI, idSavedRegion);
                            asyncQueryHandler.startUpdate(0,null,uri,contentValue,null,null);
                            break;
                        }
                    }
                }while (cursor.moveToNext());
            }
        };
        asyncQueryHandler.startQuery(0,null, Contract.SavedRegion.CONTENT_URI,null,Contract.SavedRegion.ID_SAVED_GAME + "=?",new String[] {String.valueOf(idSavedGame)},null);

        //aktualizacia dat v tabulke SAVED_HISTORY
        asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    boolean seeIfSaved = true;
                    for (int i = 0; i < actionOfGames.size(); i++) {
                        final ActionOfGame actionOfGame = actionOfGames.get(i);
                        if (seeIfSaved) {
                            boolean saved = false;
                            do {
                                if (cursor.getInt(Contract.SavedHistory.COLUMN_INDEX_WEEK) == actionOfGame.getWeek()) {
                                    saved = true;
                                    break;
                                }
                            } while (cursor.moveToNext());
                            if (!saved)
                                seeIfSaved = false;
                        }
                        if (!seeIfSaved) {
                            //nemoze tu byt else, pretoze v cykle if (seeIfSaved) sa mohlo nastavit na false a tak by sa tento cyklus nevykonal
                            final ArrayList<Region> regions = actionOfGame.getRegionsOfGames();

                            ContentValues contentValues1 = new ContentValues();
                            contentValues1.put(Contract.SavedHistory.ID_SAVED_GAME, idSavedGame);
                            contentValues1.put(Contract.SavedHistory.WEEK, actionOfGame.getWeek());
                            contentValues1.put(Contract.SavedHistory.CHANGED, System.currentTimeMillis());

                            AsyncQueryHandler asyncQueryHandler1 = new AsyncQueryHandler(getContentResolver()) {
                                @SuppressLint("StaticFieldLeak")
                                @Override
                                protected void onInsertComplete(int token, Object cookie, Uri uri) {
                                    super.onInsertComplete(token, cookie, uri);

                                    String uriString = uri.toString();
                                    int idSavedHistory = Integer.parseInt(uriString.substring(uriString.lastIndexOf('/') + 1, uriString.length()));

                                    ContentValues[] contentValues = new ContentValues[regions.size()];
                                    for (int j = 0; j < regions.size(); j++) {
                                        Region region = regions.get(j);
                                        contentValues[j] = new ContentValues();
                                        contentValues[j].put(Contract.RegionOfSavedHistory.ID_SAVED_HISTORY, idSavedHistory);
                                        contentValues[j].put(Contract.RegionOfSavedHistory.CANDIDATE, actionOfGame.getCharacter());
                                        contentValues[j].put(Contract.RegionOfSavedHistory.ID_REGION, region.getId());
                                        if (region.getRegionOfAction())
                                            contentValues[j].put(Contract.RegionOfSavedHistory.REGION_OF_ACTION, 1);
                                        else
                                            contentValues[j].put(Contract.RegionOfSavedHistory.REGION_OF_ACTION, 0);
                                        contentValues[j].put(Contract.RegionOfSavedHistory.ID_PROBLEM, region.getIdProblem());
                                        contentValues[j].put(Contract.RegionOfSavedHistory.BEFORE_PRO_CANDIDATE, region.getBeforeProCharacter());
                                        contentValues[j].put(Contract.RegionOfSavedHistory.BEFORE_UNDECIDED, region.getBeforeUndecided());
                                        contentValues[j].put(Contract.RegionOfSavedHistory.BEFORE_PRO_OPPONENT, region.getBeforeProOpponent());
                                        contentValues[j].put(Contract.RegionOfSavedHistory.AFTER_PRO_CANDIDATE, region.getAfterProCharacter());
                                        contentValues[j].put(Contract.RegionOfSavedHistory.AFTER_UNDECIDED, region.getAfterUndecided());
                                        contentValues[j].put(Contract.RegionOfSavedHistory.AFTER_PRO_OPPONENT, region.getAfterProOpponent());
                                        contentValues[j].put(Contract.RegionOfSavedHistory.CHANGED, System.currentTimeMillis());
                                    }
                                    new AsyncTask() {
                                        @Override
                                        protected Object doInBackground(Object[] params) {
                                            getContentResolver().bulkInsert(Contract.RegionOfSavedHistory.CONTENT_URI, (ContentValues[]) params);
                                            return null;
                                        }
                                    }.execute(contentValues);
                                }
                            };
                            asyncQueryHandler1.startInsert(0, null, Contract.SavedHistory.CONTENT_URI, contentValues1);
                        }
                    }
                }
            }
        };
        asyncQueryHandler.startQuery(0,null, Contract.SavedHistory.CONTENT_URI,null,Contract.SavedHistory.ID_SAVED_GAME + "=?",new String[] {String.valueOf(idSavedGame)},null);
    }


    /*
    *
    * funcia pre zobrazenie historii
    *
    * */
    private void seeHistory() {
        if (actionOfGames.size() > 0) {
            //ak je dostupna nejaka historia
            //vytvorenie dialogu
            final Dialog dialog = tf.getDialog(GameActivity.this,true,R.layout.dialog_history);
            dialog.show();

            //okopirovanie udajov o regionoch do ArrayListu regionsAfterStartGame a nastavenie tohto ArrayListu tak, ako bol na zaciatku hry
            ArrayList<Region> regionsAfterStartGame = regions;
            ArrayList<Integer> idRegionsAfterStartGame = new ArrayList<>();
            for (int i = 0; i < regionsAfterStartGame.size(); i++) {
                Region region = regionsAfterStartGame.get(i);
                region.setAfterProCharacter(0);
                region.setAfterProOpponent(0);
                idRegionsAfterStartGame.add(region.getId());
            }

            //vytvorenie ArrayListu s historiou
            ArrayList<History> histories = new ArrayList<>();
            for (int i = 0; i < actionOfGames.size(); i++) {
                //naplnenie retazcov pre mena a priezviska kandidata a oponenta
                String firstNameAndSurname = "", secondNameAndSurname = "";
                if (firstStepInGame == 1) {
                    //prvy ide kandidat
                    firstNameAndSurname = candidate.getNameSurname();
                    secondNameAndSurname = opponent.getNameSurname();
                } else {
                    //prvy ide oponent
                    firstNameAndSurname = opponent.getNameSurname();
                    secondNameAndSurname = candidate.getNameSurname();
                }

                //nastavenie premennych pre vypocet preferencii za kandidata a oponenta, nastavenie nazvu akcie, ktora bola vykonana ako prva v danom tyzdni
                ActionOfGame actionOfGame = actionOfGames.get(i);
                double firstProCandidate = 0, firstProOpponent = 0;
                String firstLastAction = "";
                ArrayList<Region> regionsOfGames = actionOfGame.getRegionsOfGames();
                ArrayList<Integer> idRegionsOfAction = new ArrayList<>();
                //v ArrayListe actionOfGame su ulozene len tie regiony, ktorych sa akcia dotkla
                for (int j = 0; j < regionsOfGames.size(); j++) {
                    Region region = regionsOfGames.get(j);
                    int id = region.getId();
                    idRegionsOfAction.add(id);
                    firstProCandidate += region.getAfterProCharacter();
                    firstProOpponent += region.getAfterProOpponent();
                    if (firstLastAction.length() == 0 && region.getRegionOfAction())
                        firstLastAction = region.getLastAction() + " (" + region.getAbbreviation() + ")";
                    int position = idRegionsAfterStartGame.indexOf(id);
                    Region regionToChange = regionsAfterStartGame.get(position);
                    regionToChange.setAfterProCharacter(region.getAfterProCharacter());
                    regionToChange.setAfterProOpponent(region.getAfterProOpponent());
                    regionsAfterStartGame.set(position, regionToChange);
                }
                //pripocitanie pre regiony, ktorych sa akcia nedotkla
                for (int j = 0; j < regionsAfterStartGame.size(); j++) {
                    Region region = regionsAfterStartGame.get(j);
                    if (idRegionsOfAction.indexOf(region.getId()) == -1) { //nenaslo sa
                        firstProCandidate += region.getAfterProCharacter();
                        firstProOpponent += region.getAfterProOpponent();
                    }
                }

                if (i + 1 < actionOfGames.size()) {
                    //v pripade ze v danom tyzdni tiahol kandidat aj oponent
                    i++;

                    //nastavenie premennych pre vypocet preferencii za kandidata a oponenta, nastavenie nazvu akcie, ktora bola vykonana ako druha v danom tyzdni
                    actionOfGame = actionOfGames.get(i);
                    double secondProCandidate = 0, secondProOpponent = 0;
                    String secondLastAction = "";
                    regionsOfGames = actionOfGame.getRegionsOfGames();
                    idRegionsOfAction = new ArrayList<>();
                    //v ArrayListe actionOfGame su ulozene len tie regiony, ktorych sa akcia dotkla
                    for (int j = 0; j < regionsOfGames.size(); j++) {
                        Region region = regionsOfGames.get(j);
                        int id = region.getId();
                        idRegionsOfAction.add(id);
                        secondProCandidate += region.getAfterProCharacter();
                        secondProOpponent += region.getAfterProOpponent();
                        if (secondLastAction.length() == 0 && region.getRegionOfAction())
                            secondLastAction = region.getLastAction() + " (" + region.getAbbreviation() + ")";
                        int position = idRegionsAfterStartGame.indexOf(id);
                        Region regionToChange = regionsAfterStartGame.get(position);
                        regionToChange.setAfterProCharacter(region.getAfterProCharacter());
                        regionToChange.setAfterProOpponent(region.getAfterProOpponent());
                        regionsAfterStartGame.set(position, regionToChange);
                    }
                    //pripocitanie pre regiony, ktorych sa akcia nedotkla
                    for (int j = 0; j < regionsAfterStartGame.size(); j++) {
                        Region region = regionsAfterStartGame.get(j);
                        if (idRegionsOfAction.indexOf(region.getId()) == -1) { //nenaslo sa
                            secondProCandidate += region.getAfterProCharacter();
                            secondProOpponent += region.getAfterProOpponent();
                        }
                    }

                    histories.add(new History(actionOfGame.getWeek(), firstNameAndSurname, secondNameAndSurname, firstLastAction, secondLastAction, firstStepInGame == 1,
                            firstProCandidate, firstProOpponent, secondProCandidate, secondProOpponent, population));
                }
                else {
                    //v pripade ze v danom tyzdni tiahol iba jeden, cize bud kandidat alebo oponent (stale ale vyjde ze to je oponent)
                    histories.add(new History(actionOfGame.getWeek(), firstNameAndSurname, secondNameAndSurname, firstLastAction, "", firstStepInGame == 1,
                            firstProCandidate, firstProOpponent, -1, -1, population));
                }
            }

            //nastavenie adapteru s udajmi s historiou pre ListView
            ListView listView = dialog.findViewById(R.id.listViewHistory);
            HistoryAdapter adapter = new HistoryAdapter(histories, getApplicationContext());
            listView.setAdapter(adapter);
        }
        else {
            //v pripade, ak nie je ziadna historia, tak sa otvori dialog s tymto oznamom
            Dialog dialog = tf.getDialogSimpleNotice(GameActivity.this,true,R.layout.dialog_simple_notice,
                    getResources().getString(R.string.history),getResources().getString(R.string.any_history));
            dialog.show();
        }
    }


    /*
    *
    * funkcia pre zobrazenie kroku oponenta
    *
    * */
    private void seeOpponentStep() {
        disabledOnClick = true;
        zoomableViewGroup.setIsScrollingEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                disabledOnClick = false;
                zoomableViewGroup.setIsScrollingEnabled(true);
                //nahodny vyber regionu a kliknutie na tento region
                final int position = new Random().nextInt(relativeLayoutMapObjects.getChildCount()-1);
                relativeLayoutMapObjects.getChildAt(position).callOnClick();


                disabledOnClick = true;
                zoomableViewGroup.setIsScrollingEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        disabledOnClick = false;
                        zoomableViewGroup.setIsScrollingEnabled(true);
                        //nahodny vyber typu akcie a kliknutie na tlacidlo pre tuto akciu
                        final int actionType = new Random().nextInt(2);
                        if (actionType == 0)
                            (findViewById(idInvestmentButtonInRelativeLayoutRegionDetailOptions)).callOnClick();
                        else if (actionType == 1)
                            (findViewById(idInterviewButtonInRelativeLayoutRegionDetailOptions)).callOnClick();


                        disabledOnClick = true;
                        zoomableViewGroup.setIsScrollingEnabled(false);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                disabledOnClick = false;
                                zoomableViewGroup.setIsScrollingEnabled(true);
                                //nahodny vyber zo zoznamu problemov a kliknutie na neho
                                final int position = new Random().nextInt(problems.size());
                                radioGroupListOfProblems.getChildAt(position).callOnClick();


                                disabledOnClick = true;
                                zoomableViewGroup.setIsScrollingEnabled(false);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        disabledOnClick = false;
                                        zoomableViewGroup.setIsScrollingEnabled(true);
                                        //kliknutie na tlacidlo pre vykonanie akcie
                                        buttonDoAction.callOnClick();
                                    }
                                }, 2000);
                            }
                        }, 2000);
                    }
                }, 2000);
            }
        }, 2000);
    }


    /*
    *
    * funkcia pre ukonenie hry po uplynuti volebnej kampane
    *
    * */
    private void EndOfGame() {
        //vytvorenie objektu, kde vlozim vsetky informacie o hre
        Bundle obj = new Bundle();
        obj.putInt("regionsSize",regions.size());

        //vlozenie vsetkych regionov a ich aktualnych konecnych preferencii
        for (int i = 0; i < regions.size(); i++) {
            Region region = regions.get(i);
            Bundle objRegion = new Bundle();
            objRegion.putInt("id",region.getId());
            objRegion.putString("regionName",region.getRegionName());
            obj.putBundle("region"+String.valueOf(i),objRegion);
        }

        //vlozenie charakteru
        obj.putInt("characterId",candidate.getId());
        obj.putString("characterNameSurname",candidate.getNameSurname());
        obj.putString("characterPercent",tvCandidatePercent.getText().toString());

        //vlozenie oponenta
        obj.putInt("opponentId",opponent.getId());
        obj.putString("opponentNameSurname",opponent.getNameSurname());
        obj.putString("opponentPercent",tvOpponentPercent.getText().toString());

        //vlozenie vysledku volieb - vyhral si, prehral si, remiza
        double proCandidate = 0;
        double proOpponent = 0;
        for (int i = 0; i < regions.size(); i++) {
            Region region = regions.get(i);
            proCandidate += region.getAfterProCharacter();
            proOpponent += region.getAfterProOpponent();
        }
        String result = "";
        if (proCandidate > proOpponent)
            result = getResources().getString(R.string.win);
        else if (proCandidate < proOpponent)
            result = getResources().getString(R.string.lose);
        else
            result = getResources().getString(R.string.draw);
        obj.putString("result",result);

        //vlozenie vykonanych akcii
        obj.putInt("actionSize",actionOfGames.size());
        for (int i = 0; i < actionOfGames.size(); i++) {
            ActionOfGame action = actionOfGames.get(i);
            Bundle objAction = new Bundle();
            objAction.putString("actionType",action.getActionType());
            objAction.putInt("character",action.getCharacter());
            objAction.putInt("week",action.getWeek());

            ArrayList<Region> regionOfGame = action.getRegionsOfGames();
            Bundle objRegions = new Bundle();
            objRegions.putInt("count",regionOfGame.size());
            for (int j = 0; j < regionOfGame.size(); j++) {
                Region region = regionOfGame.get(j);
                Bundle objRegion = new Bundle();
                if (region.getRegionOfAction())
                    objRegion.putInt("regionOfAction",1);
                else
                    objRegion.putInt("regionOfAction",0);
                objRegion.putInt("idRegion",region.getId());
                objRegion.putString("regionName",region.getRegionName());
                objRegion.putInt("idProblem",region.getIdProblem());
                objRegion.putDouble("proCandidate",region.getAfterProCharacter());
                objRegion.putDouble("undecided",region.getAfterUndecided());
                objRegion.putDouble("proOpponent",region.getAfterProOpponent());
                objRegions.putBundle("region"+String.valueOf(j),objRegion);
            }
            objAction.putBundle("regions",objRegions);
            obj.putBundle("action"+String.valueOf(i),objAction);
        }

        //zapnutie aktivity GameEvaluation
        Intent intent = new Intent(GameActivity.this,GameEvaluationActivity.class);
        intent.putExtra("obj",obj);
        startActivity(intent);
        finish();
    }


    /*
    *
    * funkcia pre nastavenie regionov pred vykonanim akcie
    *
    * */
    private void setRegionsBeforeAction(String actionType, Problem problem) {
        //dopyt do DB pre urcenie susednych regionov
        Bundle obj = getContentResolver().call(Contract.Region.CONTENT_URI,"return all neighboring regions by idRegion",String.valueOf(idSelectedRegion),null);
        int count = obj.getInt("count");
        //do ArrayListu idNeighboringRegions sa vlozia identifikatory susednych regionov
        ArrayList<Integer> idNeighboringRegions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Bundle row = obj.getBundle("row" + String.valueOf(i));
            idNeighboringRegions.add(row.getInt("id"));
        }

        for(int i = 0; i < regions.size(); i++) {
            Region region = regions.get(i);
            //vsetky regiony, ktorych sa v predchadzujucom kole dotkli nejake akcie musia byt vynulovane
            region.setIdProblem(0);
            region.setRegionOfAction(false);
            region.setActionType(null);

            //nastavenie, kto vykonal akciu
            region.setCharacter(actualOnStep.getIntValue());
            //nastavenie aktualneho tyzdna
            region.setWeek(actualWeek.getIntValue());

            //nastavenie ostatnych premennych
            double beforeProCharacter = region.getBeforeProCharacter();
            double beforeUndecided = region.getBeforeUndecided();
            double beforeProOpponent = region.getBeforeProOpponent();
            long regionPopulation = region.getPopulation();

            boolean birthRegionOfCharacter = false;
            if (region.getRegionName().equals(candidate.getBirthRegionName()))
                birthRegionOfCharacter = true;

            boolean birthRegionOfOpponent = false;
            if (region.getRegionName().equals(opponent.getBirthRegionName()))
                birthRegionOfOpponent = true;

            double afterProCharacter = 0;
            double afterProOpponent = 0;
            double afterUndecided = 0;

            if (region.getId() == idSelectedRegion) {
                //vybrany region, v ktorom prebehla akcia
                region.setIdProblem(problem.getId());
                region.setRegionOfAction(true);
                region.setActionType(actionType);
                if (actualOnStep.getIntValue() == 1) {
                    //krok vykonal kandidat
                    obj = doActionOnRegion(true,actionType,beforeProCharacter,beforeUndecided,
                            beforeProOpponent,regionPopulation,birthRegionOfCharacter,
                            candidate.getCharisma(),candidate.getReputation(),candidate.getMoney(),
                            birthRegionOfOpponent,opponent.getCharisma(),opponent.getReputation(),
                            opponent.getMoney(),problem.getEffect(),problem.getSolvability());
                    afterProCharacter = obj.getDouble("pro");
                    afterUndecided = obj.getDouble("undecided");
                    afterProOpponent = obj.getDouble("against");

                    String lastAction = "";
                    if (actionType.equals("interview"))
                        lastAction = getResources().getString(R.string.interview);
                    else
                        lastAction = getResources().getString(R.string.investment);
                    lastAction = lastAction.substring(0,1).toUpperCase() + lastAction.substring(1) + " - " + problem.getProblemName();
                    region.setLastAction(lastAction);
                } else {
                    //krok vykonal opponent
                    obj = doActionOnRegion(true,actionType,beforeProOpponent,beforeUndecided,
                            beforeProCharacter,regionPopulation,birthRegionOfOpponent,
                            opponent.getCharisma(),opponent.getReputation(),opponent.getMoney(),
                            birthRegionOfCharacter,candidate.getCharisma(),candidate.getReputation(),
                            candidate.getMoney(),problem.getEffect(),problem.getSolvability());
                    afterProCharacter = obj.getDouble("against");
                    afterUndecided = obj.getDouble("undecided");
                    afterProOpponent = obj.getDouble("pro");

                    String lastAction = "";
                    if (actionType.equals("interview"))
                        lastAction = getResources().getString(R.string.interview);
                    else
                        lastAction = getResources().getString(R.string.investment);
                    lastAction = lastAction.substring(0,1).toUpperCase() + lastAction.substring(1) + " - " + problem.getProblemName();
                    region.setLastAction(lastAction);
                }
            } else {
                //nastavenie regionov, v ktorych neprebehla akcia a ani nie su susedne regiony
                if (idNeighboringRegions.indexOf(i) == -1) {
                    region.setIdProblem(0);
                    region.setRegionOfAction(false);
                    region.setActionType(null);
                    afterProCharacter = region.getBeforeProCharacter();
                    afterUndecided = region.getBeforeUndecided();
                    afterProOpponent = region.getBeforeProOpponent();
                }
                //nastavenie susednych regionov
                else {
                    region.setIdProblem(problem.getId());
                    region.setRegionOfAction(false);
                    region.setActionType(actionType);
                    if (actualOnStep.getIntValue() == 1) {
                        //krok vykonal kandidat
                        obj = doActionOnRegion(false,actionType,beforeProCharacter,beforeUndecided,
                                beforeProOpponent,regionPopulation,birthRegionOfCharacter,
                                candidate.getCharisma(),candidate.getReputation(),candidate.getMoney(),
                                birthRegionOfOpponent,opponent.getCharisma(),opponent.getReputation(),
                                opponent.getMoney(),problem.getEffect(),problem.getSolvability());
                        afterProCharacter = obj.getDouble("against");
                        afterUndecided = obj.getDouble("undecided");
                        afterProOpponent = obj.getDouble("pro");
                    } else if (actualOnStep.getIntValue() == 0) {
                        //krok vykonal oponent
                        obj = doActionOnRegion(false,actionType,beforeProOpponent,beforeUndecided,
                                beforeProCharacter,regionPopulation,birthRegionOfOpponent,
                                opponent.getCharisma(),opponent.getReputation(),opponent.getMoney(),
                                birthRegionOfCharacter,candidate.getCharisma(),candidate.getReputation(),
                                candidate.getMoney(),problem.getEffect(),problem.getSolvability());
                        afterProCharacter = obj.getDouble("against");
                        afterUndecided = obj.getDouble("undecided");
                        afterProOpponent = obj.getDouble("pro");
                    }
                }
            }
            region.setAfterProCharacter(afterProCharacter);
            region.setAfterUndecided(afterUndecided);
            region.setAfterProOpponent(afterProOpponent);
            regions.set(i, region);
        }
    }


    /*
    *
    * funkcia pre vykonanie akcie v regione
    *
    * */
    private Bundle doActionOnRegion(boolean regionOfAction, String actionType,
                                    double beforeProFirst, double beforeUndecided, double beforeProSecond,
                                    long population,
                                    boolean birthRegionOfFirstCandidate, double charismaFirst, double reputationFirst, double moneyFirst,
                                    boolean birthRegionOfSecondCandidate, double charismaSecond, double reputationSecond, double moneySecond,
                                    double problemEffect, double problemSolvability) {

        //uprava vlastnosti kandidata a oponenta
        charismaFirst /= 10;
        reputationFirst /= 10;
        moneyFirst /= 10;
        charismaSecond /= 10;
        reputationSecond /= 10;
        moneySecond /= 10;

        double problemSolvabilityFirst = tf.getImpactOfProblemSolvability(problemSolvability,3,1);
        double problemSolvabilitySecond = tf.getImpactOfProblemSolvability(problemSolvability,1,3);

        double editCharismaFirst = 0;
        double editMoneyFirst = 0;
        double denominatorFirst = 0;
        double editCharismaSecond = 0;
        double editMoneySecond = 0;
        double denominatorSecond = 0;
        switch (actionType) {
            case "interview" : {
                editCharismaFirst = charismaFirst;
                editMoneyFirst = moneyFirst / 10;
                denominatorFirst = moneyFirst;
                editCharismaSecond = charismaSecond;
                editMoneyFirst = moneySecond / 10;
                denominatorSecond = moneyFirst;
            }
            case "investment" : {
                editCharismaFirst = charismaFirst / 10;
                editMoneyFirst = moneyFirst;
                denominatorFirst = charismaFirst;
                editCharismaSecond = charismaSecond / 10;
                editMoneySecond = moneySecond;
                denominatorSecond = charismaSecond;
            }
            default: {
            }
        }

        double birthRegionFirst = 0;
        if (birthRegionOfFirstCandidate)
            birthRegionFirst = (charismaFirst + reputationFirst) / moneyFirst;
        double birthRegionSecond = 0;
        if (birthRegionOfSecondCandidate)
            birthRegionSecond = (charismaSecond + reputationSecond) / moneySecond;

        double proFirst =
                (editCharismaFirst + reputationFirst + editMoneyFirst) / denominatorFirst
                + birthRegionFirst
                + problemEffect
                + problemSolvabilityFirst
                + new Random(2).nextDouble() - 1;
        double proSecond =
                (editCharismaSecond + reputationSecond + editMoneySecond) / denominatorSecond
                + birthRegionSecond
                + problemEffect
                + problemSolvabilitySecond
                + new Random(2).nextDouble() - 1;

        double pom = proFirst - proSecond / 10;
        proSecond = proSecond - proFirst / 10;
        proFirst = pom;

        double afterProFirst = beforeProFirst + population * proFirst;
        double afterProSecond = beforeProSecond + population * proSecond;
        double afterUndecided = population - afterProFirst - afterProSecond;

/*
        //TODO: co ak afterPro a afterAgainst bude vecsie ako population?? moze nastat taka situacia?
        //ohranicenie poctu volicov, pocet nemoze byt mensi ako 0
        if (afterPro >= 0 && afterAgainst >= 0 && afterUndecided >= 0) {
            Bundle obj = new Bundle();
            obj.putDouble("pro", afterPro);
            obj.putDouble("undecided", afterUndecided);
            obj.putDouble("against", afterAgainst);
            return obj;
        } else if (afterPro < 0 && afterAgainst < 0) {
            afterPro = 0;
            afterAgainst = 0;
            afterUndecided = population;
            Bundle obj = new Bundle();
            obj.putDouble("pro", afterPro);
            obj.putDouble("undecided", afterUndecided);
            obj.putDouble("against", afterAgainst);
            return obj;
        } else if (afterPro < 0) {
            afterAgainst = afterAgainst + -1*afterPro/population;
            afterPro = 0;
            afterUndecided = population - afterPro - afterAgainst;
            Bundle obj = new Bundle();
            obj.putDouble("pro", afterPro);
            obj.putDouble("undecided", afterUndecided);
            obj.putDouble("against", afterAgainst);
            return obj;
        }
        else if (afterAgainst < 0) {
            afterPro = afterPro + -1*afterAgainst/population;
            afterAgainst = 0;
            afterUndecided = population - afterPro - afterAgainst;
            Bundle obj = new Bundle();
        }
        else
            return null;*/

        Bundle obj = new Bundle();
        obj.putDouble("pro", afterProFirst);
        obj.putDouble("undecided", afterUndecided);
        obj.putDouble("against", afterProSecond);
        return obj;
    }


    /*
    *
    * funkcia pre nastavenie regionov po vykonanim akcie
    *
    * */
    private void setRegionsAfterAction() {
        for (int i = 0; i < regions.size(); i++) {
            Region region = regions.get(i);
            region.setBeforeProCharacter(region.getAfterProCharacter());
            region.setBeforeUndecided(region.getAfterUndecided());
            region.setBeforeProOpponent(region.getAfterProOpponent());
        }
    }
}