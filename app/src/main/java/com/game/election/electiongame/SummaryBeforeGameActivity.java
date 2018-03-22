package com.game.election.electiongame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.game.election.electiongame.classes.TogetherFunctions;
import com.game.election.electiongame.provider.Contract;

public class SummaryBeforeGameActivity extends AppCompatActivity {

    TogetherFunctions tf;
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_summary_before_game);
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
        Intent intent = getIntent();
        final String stateName = intent.getStringExtra("state");
        final int positionCharacter = intent.getIntExtra("positionCandidate",0);
        final int positionOpponent = intent.getIntExtra("positionOpponent",0);

        Bundle obj = getContentResolver().call(Contract.State.CONTENT_URI,"return all characters in state by stateName",stateName,null);
        Bundle characterObj = obj.getBundle("row" + String.valueOf(positionCharacter));
        String characterNameSurname = characterObj.getString("nameSurname");

        Bundle opponentObj = obj.getBundle("row" + String.valueOf(positionOpponent));
        String opponentNameSurname = opponentObj.getString("nameSurname");

        ((TextView)findViewById(R.id.textViewCandidate)).setText(characterNameSurname);
        ((TextView)findViewById(R.id.textViewOpponent)).setText(opponentNameSurname);

        (findViewById(R.id.constraintLayout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SummaryBeforeGameActivity.this,GameActivity.class);
                intent.putExtra("state",stateName);
                intent.putExtra("positionCharacter",positionCharacter);
                intent.putExtra("positionOpponent",positionOpponent);
                startActivity(intent);
                finish();
            }
        });
    }
}
