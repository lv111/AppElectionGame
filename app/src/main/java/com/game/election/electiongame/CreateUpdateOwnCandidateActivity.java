package com.game.election.electiongame;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.game.election.electiongame.classes.ChangeVariable;
import com.game.election.electiongame.classes.TogetherFunctions;
import com.game.election.electiongame.provider.Contract;

import java.util.ArrayList;

public class CreateUpdateOwnCandidateActivity extends AppCompatActivity {

    /*
    *
    *  vytvorenie alebo uprava kandidata
    *  tato aktivita sa zapina cez aktivitu ChooseCandidate
    *
    * */

    TogetherFunctions tf;
    String state;                       //premenna, kde sa uklada stat, v ktorom bude kandidat vystupovat
    int idCharacter;                    //ak ide o aktualizaciu kandidata, tak tato premenna bude obsahovat id kandidata z DB
    ChangeVariable actualRegionPositionInArraylist, //oznacuje poziciu vybraneho rodneho regionu kandidata z arraylistu regions
            pointsLeft,                 //oznacuje, kolko bodov zostava pre rozdelenie medzi vlastnosti kandidata
            charisma,                   //oznacuje pocet bodov pre vlastnost charizma
            reputation,                 //oznacuje pocet bodov pre vlastnost reputacia
            money;                      //oznacuje pocet bodov pre vlastnost peniaze
    TextView textViewBirthRegion,       //oznacuje textview, ktory zobrazuje vybrany rodny region kandidata
            textViewPointsLeft,         //oznacuje textview, v ktorom sa zobrazuju volne body, teda hodnota premennej pointsLeft
            textViewCharisma,           //oznacuje textview, v ktorom sa zobrazuje hodnota premennej charisma
            textViewReputation,         //oznacuje textview, v ktorom sa zobrazuje hodnota premennej reputation
            textViewMoney;              //oznacuje textview, v ktorom sa zobrazuje hodnota premennej money
    ImageView imageViewCharismaUp,      //oznacuje imageview, pomocou ktoreho sa da navysovat hodnota premennej charisma
            imageViewCharismaDown,      //oznacuje imageview, pomocou ktoreho sa da znizovat hodnota premennej charisma
            imageViewReputationUp,      //oznacuje imageview, pomocou ktoreho sa da navysovat hodnota premennej reputation
            imageViewReputationDown,    //oznacuje imageview, pomocou ktoreho sa da znizovat hodnota premennej reputation
            imageViewMoneyUp,           //oznacuje imageview, pomocou ktoreho sa da navysovat hodnota premennej money
            imageViewMoneyDown;         //oznacuje imageview, pomocou ktoreho sa da znizovat hodnota premennej money
    ArrayList<String> regions;          //arraylist, ktory obsahuje nazvy regionov v state, ktory je ulozeny v premennej state
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_create_update_own_candidate);
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


    /*
    *
    * nastavuje sa cely layout v aktivite
    * nastavovanie premennych a ChangeVariable
    * nastavenie onClickListenerov
    *
    * */
    private void setLayout() {
        Intent intent = getIntent();

        //nastavenie premennych state a idCharacter
        //premenna idCharacter sa nastavi vtedy, ak ide o upravu kandidata, inak ma hodnotu -1
        state = intent.getStringExtra("state");
        idCharacter = intent.getIntExtra("idCharacter",-1);

        //nastavanie textu rodneho statu, nastavenie textviewov a imageviewov
        ((TextView)findViewById(R.id.textViewBirthState)).setText(state);
        textViewBirthRegion = findViewById(R.id.textViewBirthRegion);
        textViewPointsLeft = findViewById(R.id.textViewPoints);
        imageViewCharismaDown = findViewById(R.id.imageViewDownCharisma);
        imageViewCharismaUp = findViewById(R.id.imageViewUpCharisma);
        imageViewReputationDown = findViewById(R.id.imageViewDownReputation);
        imageViewReputationUp = findViewById(R.id.imageViewUpReputation);
        imageViewMoneyDown = findViewById(R.id.imageViewDownMoney);
        imageViewMoneyUp = findViewById(R.id.imageViewUpMoney);
        textViewCharisma = findViewById(R.id.textViewCharisma);
        textViewReputation = findViewById(R.id.textViewReputation);
        textViewMoney = findViewById(R.id.textViewMoney);

        //nastavenie premennych typu ChangeVariable
        actualRegionPositionInArraylist = new ChangeVariable(0);
        pointsLeft = new ChangeVariable(10);
        charisma = new ChangeVariable(30);
        reputation = new ChangeVariable(30);
        money = new ChangeVariable(30);

        //nastavenie arraylistu regions
        Bundle obj = getContentResolver().call(Contract.State.CONTENT_URI,"return all regions in state by stateName",state,null);
        int count = obj.getInt("count");
        regions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Bundle row = obj.getBundle("row" + String.valueOf(i));
            regions.add(row.getString("regionName"));
        }

        //nastavenie onClickListenerov
        setOnClickListeners();

        //ak sa jedna o upravu kandidata
        if (idCharacter != -1) {
            //spravi sa dopyt do DB pre informacie o kandidatovi podla jeho id
            obj = getContentResolver().call(Contract.Candidate.CONTENT_URI,"return saved character by idCharacter",String.valueOf(idCharacter),null);

            //nastavenie edittextu s menom a priezviskom kandidata
            ((EditText)findViewById(R.id.editTextNameSurname)).setText(obj.getString("nameSurname"));

            //nastavenie premennych typu ChangeVariable
            charisma.setValue(obj.getInt("charisma"));
            reputation.setValue(obj.getInt("reputation"));
            money.setValue(obj.getInt("money"));
            pointsLeft.setValue(100 - charisma.getIntValue() - reputation.getIntValue() - money.getIntValue());

            //nastavenie premennej actualRegionPositionInArraylist
            String regionName = obj.getString("regionName");
            for (int i = 0; i < regions.size(); i++) {
                if (regionName.equals(regions.get(i))) {
                    actualRegionPositionInArraylist.setValue(i);
                    break;
                }
            }
        }

        //zavolanie ChangeListenerov na ChangeVariable
        actualRegionPositionInArraylist.callListener();
        charisma.callListener();
        reputation.callListener();
        money.callListener();
    }


    /*
    *
    * funkcia, ktora sa vola v ramci onClickListenerov na tlacidla, ktore znizuju alebo zvysuju vlastnosti kandidata
    *
    * */
    private void onClickUpDown(int valueCharisma, int valueReputation, int valueMoney) {
        if (valueCharisma >= 0 && valueReputation >= 0 && valueMoney >= 0
                && valueCharisma + valueReputation + valueMoney <= 100) {
            charisma.setValue(valueCharisma);
            reputation.setValue(valueReputation);
            money.setValue(valueMoney);
        }

        if (valueCharisma == 100)
            imageViewCharismaUp.setVisibility(View.INVISIBLE);
        else if (valueCharisma == 0)
            imageViewCharismaDown.setVisibility(View.INVISIBLE);
        else {
            imageViewCharismaUp.setVisibility(View.VISIBLE);
            imageViewCharismaDown.setVisibility(View.VISIBLE);
        }

        if (valueReputation == 100)
            imageViewReputationUp.setVisibility(View.INVISIBLE);
        else if (valueReputation == 0)
            imageViewReputationDown.setVisibility(View.INVISIBLE);
        else {
            imageViewReputationUp.setVisibility(View.VISIBLE);
            imageViewReputationDown.setVisibility(View.VISIBLE);
        }

        if (valueMoney == 100)
            imageViewMoneyUp.setVisibility(View.INVISIBLE);
        else if (valueMoney == 0)
            imageViewMoneyDown.setVisibility(View.INVISIBLE);
        else {
            imageViewMoneyUp.setVisibility(View.VISIBLE);
            imageViewMoneyDown.setVisibility(View.VISIBLE);
        }
    }


    /*
    *
    * funkcia, pomocou ktorej sa nastavuje viacerov onCliCkListenerov
    *
    * */
    private void setOnClickListeners() {
        //nastavenie ChangeVariable actualRegionPositionInArraylist
        actualRegionPositionInArraylist = new ChangeVariable(0);
        actualRegionPositionInArraylist.setListener(new ChangeVariable.ChangeListener() {
            @Override
            public void onChange() {
                textViewBirthRegion.setText(regions.get(actualRegionPositionInArraylist.getIntValue()));
            }
        });

        //nastavenie onClickListenera na tlacidla pre posun v arrayliste regionov
        (findViewById(R.id.imageViewUpRegion)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = actualRegionPositionInArraylist.getIntValue() + 1;
                if (value == regions.size())
                    value = 0;
                actualRegionPositionInArraylist.setValue(value);
            }
        });
        (findViewById(R.id.imageViewDownRegion)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = actualRegionPositionInArraylist.getIntValue() - 1;
                if (value < 0)
                    value = regions.size() - 1;
                actualRegionPositionInArraylist.setValue(value);
            }
        });

        //nastavenie premennych ChangeVariable
        pointsLeft.setListener(new ChangeVariable.ChangeListener() {
            @Override
            public void onChange() {
                textViewPointsLeft.setText(getResources().getQuantityString(R.plurals.points_left, pointsLeft.getIntValue(),pointsLeft.getIntValue()));
            }
        });
        charisma.setListener(new ChangeVariable.ChangeListener() {
            @Override
            public void onChange() {
                pointsLeft.setValue(100 - charisma.getIntValue() - reputation.getIntValue() - money.getIntValue());
                textViewCharisma.setText(String.valueOf(charisma.getIntValue()));
            }
        });
        reputation.setListener(new ChangeVariable.ChangeListener() {
            @Override
            public void onChange() {
                pointsLeft.setValue(100 - charisma.getIntValue() - reputation.getIntValue() - money.getIntValue());
                textViewReputation.setText(String.valueOf(reputation.getIntValue()));
            }
        });
        money.setListener(new ChangeVariable.ChangeListener() {
            @Override
            public void onChange() {
                pointsLeft.setValue(100 - charisma.getIntValue() - reputation.getIntValue() - money.getIntValue());
                textViewMoney.setText(String.valueOf(money.getIntValue()));
            }
        });

        //nastavenie onClickListenera na tlacidla pre nastavenie ChangeVariable charisma, reputation, money
        imageViewCharismaUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pointsLeft.getIntValue() > 0)
                    onClickUpDown(charisma.getIntValue() + 1, reputation.getIntValue(), money.getIntValue());
            }
        });
        imageViewReputationUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pointsLeft.getIntValue() > 0)
                    onClickUpDown(charisma.getIntValue(), reputation.getIntValue() + 1, money.getIntValue());
            }
        });
        imageViewMoneyUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pointsLeft.getIntValue() > 0)
                    onClickUpDown(charisma.getIntValue(), reputation.getIntValue(), money.getIntValue() + 1);
            }
        });
        imageViewCharismaDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUpDown(charisma.getIntValue() - 1, reputation.getIntValue(), money.getIntValue());
            }
        });
        imageViewReputationDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUpDown(charisma.getIntValue(), reputation.getIntValue() - 1, money.getIntValue());
            }
        });
        imageViewMoneyDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUpDown(charisma.getIntValue(), reputation.getIntValue(), money.getIntValue() - 1);
            }
        });

        //nastavenie onClickListenera na tlacidlo pre posunutie do aktivity ChooseCandidate
        (findViewById(R.id.imageViewBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //nastavenie onClickListenera pre tlacidlo na vytvorenie alebo aktualizovanie kandidata
        (findViewById(R.id.imageViewCreateUpdate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ak nie je vyplnene meno, tak sa nemoze robit nic, pouzivatelovi bude ukazane oznamenie
                if (((EditText)findViewById(R.id.editTextNameSurname)).getText().toString().length() == 0)
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.you_have_to_fill_in_name_and_surname), Toast.LENGTH_SHORT).show();
                else {
                    if (idCharacter == -1) {
                        //vlozenie noveho charakteru
                        //otvorenie dialogu pre potvrdenie ulozenia noveho kandidata
                        final Dialog dialogSaveNewCandidate = tf.getDialogSimpleQuestion(CreateUpdateOwnCandidateActivity.this,true,
                                R.layout.dialog_simple_question, getResources().getText(R.string.save_new_candidate).toString(),
                                getResources().getText(R.string.sure_you_want_save_new_candidate).toString(), getResources().getText(R.string.save).toString(),
                                getResources().getText(R.string.cancel).toString());
                        //nastavenie onClickListenera pre pozitivnu odpoved, cize ulozenie noveho kandidata
                        (dialogSaveNewCandidate.findViewById(R.id.buttonPositiveAnswer)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                insertCandidateToDatabase();
                                dialogSaveNewCandidate.dismiss();
                            }
                        });
                        (dialogSaveNewCandidate.findViewById(R.id.buttonNegativeButton)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogSaveNewCandidate.dismiss();
                            }
                        });
                        dialogSaveNewCandidate.show();
                    }
                    else {
                        //aktualizacia ulozeneho charakteru
                        //otvorenie dialogu pre potvrdenie ulozenia informacii kandidata
                        final Dialog dialogUpdateCandidate = tf.getDialogSimpleQuestion(CreateUpdateOwnCandidateActivity.this,true,
                                R.layout.dialog_simple_question, getResources().getText(R.string.update_candidate).toString(),
                                getResources().getText(R.string.sure_you_want_save_candidate).toString(), getResources().getText(R.string.save).toString(),
                                getResources().getText(R.string.cancel).toString());
                        //nastavenie onClickListenera pre pozitivnu odpoved, cize ulozenie kandidata
                        (dialogUpdateCandidate.findViewById(R.id.buttonPositiveAnswer)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                updateCharacterInDatabase();
                                dialogUpdateCandidate.dismiss();
                            }
                        });
                        (dialogUpdateCandidate.findViewById(R.id.buttonNegativeButton)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogUpdateCandidate.dismiss();
                            }
                        });
                        dialogUpdateCandidate.show();
                    }
                }
            }
        });
    }


    /*
    *
    * vlozenie noveho kandidata do databazy
    *
    * */
    private void insertCandidateToDatabase() {
        //dopyt do DB pre zistenie identifikatora rodneho regionu
        String birthRegion = regions.get(actualRegionPositionInArraylist.getIntValue());
        String selection = Contract.Region.REGION_NAME+"=?";
        String[] selectionArgs = {birthRegion};
        @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
                cursor.moveToFirst();
                int idBirthRegion = cursor.getInt(Contract.Region.COLUMN_INDEX_ID);

                //nastavenie hodnot pre vlozenie noveho kandidata do DB
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.Candidate.NAME_SURNAME,((EditText)findViewById(R.id.editTextNameSurname)).getText().toString());
                contentValues.put(Contract.Candidate.ID_BIRTH_REGION,idBirthRegion);
                contentValues.put(Contract.Candidate.CHARISMA,charisma.getIntValue());
                contentValues.put(Contract.Candidate.REPUTATION,reputation.getIntValue());
                contentValues.put(Contract.Candidate.MONEY,money.getIntValue());
                contentValues.put(Contract.Candidate.CREATED_BY_USER,1);
                contentValues.put(Contract.Candidate.CHANGED,System.currentTimeMillis());
                AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
                    @Override
                    protected void onInsertComplete(int token, Object cookie, Uri uri) {
                        super.onInsertComplete(token, cookie, uri);
                        //po ukoncenie vkladania kandidata do DB nasleduje ukoncenie tejto aktivity
                        CreateUpdateOwnCandidateActivity.this.finish();
                    }
                };
                asyncQueryHandler.startInsert(0,null, Contract.Candidate.CONTENT_URI,contentValues);
            }
        };
        asyncQueryHandler.startQuery(0,null, Contract.Region.CONTENT_URI,null,selection,selectionArgs,null);
    }


    /*
    *
    * aktualizovanie kandidata v databaze
    *
    * */
    private void updateCharacterInDatabase() {
        //dopyt do DB pre zistenie identifikatora rodneho regionu
        String birthRegion = regions.get(actualRegionPositionInArraylist.getIntValue());
        String selection = Contract.Region.REGION_NAME+"=?";
        String[] selectionArgs = {birthRegion};
        @SuppressLint("HandlerLeak") AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
                cursor.moveToFirst();
                int idBirthRegion = cursor.getInt(Contract.Region.COLUMN_INDEX_ID);

                //nastavenie hodnot pre aktualizaciu kandidata v DB
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.Candidate.NAME_SURNAME,((EditText)findViewById(R.id.editTextNameSurname)).getText().toString());
                contentValues.put(Contract.Candidate.ID_BIRTH_REGION,idBirthRegion);
                contentValues.put(Contract.Candidate.CHARISMA,charisma.getIntValue());
                contentValues.put(Contract.Candidate.REPUTATION,reputation.getIntValue());
                contentValues.put(Contract.Candidate.MONEY,money.getIntValue());
                contentValues.put(Contract.Candidate.CREATED_BY_USER,1);
                contentValues.put(Contract.Candidate.CHANGED,System.currentTimeMillis());

                AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
                    @Override
                    protected void onUpdateComplete(int token, Object cookie, int result) {
                        super.onUpdateComplete(token, cookie, result);
                        //po ukoncenie aktualizovania kandidata do DB nasleduje ukoncenie tejto aktivity
                        CreateUpdateOwnCandidateActivity.this.finish();
                    }
                };
                Uri uri = ContentUris.withAppendedId(Contract.Candidate.CONTENT_URI, idCharacter);
                asyncQueryHandler.startUpdate(0,null,uri,contentValues,null,null);
            }
        };
        asyncQueryHandler.startQuery(0,null, Contract.Region.CONTENT_URI,null,selection,selectionArgs,null);
    }
}
