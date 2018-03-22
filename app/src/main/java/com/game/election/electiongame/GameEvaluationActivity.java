package com.game.election.electiongame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.game.election.electiongame.classes.TogetherFunctions;

public class GameEvaluationActivity extends AppCompatActivity {

    TogetherFunctions tf;
    private float startX,startY;//premenne, ktore oznacuju bod pri dotyku pouzivatela s obrazovkou

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
        setContentView(R.layout.activity_game_evaluation);
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
        tf = new TogetherFunctions();

        Intent intent = getIntent();
        Bundle obj = intent.getBundleExtra("obj");

        String result = obj.getString("result");
        ((TextView)findViewById(R.id.textViewResult)).setText(result);
        if (result.equals(getResources().getString(R.string.lose)))
            ((TextView)findViewById(R.id.textViewResult)).setTextAppearance(getApplicationContext(),R.style.bold_red_textview_style_size_30sp);


        ((TextView)findViewById(R.id.textViewCharacterNameSurname)).setText(
                obj.getString("characterNameSurname"));
        ((TextView)findViewById(R.id.textViewProCharacter)).setText(obj.getString("characterPercent"));
        if (result.equals(getResources().getString(R.string.win)))
            ((TextView)findViewById(R.id.textViewProCharacter)).setTextAppearance(getApplicationContext(),R.style.bold_blue_textview_style_size_30sp);

        ((TextView)findViewById(R.id.textViewOpponentNameSurname)).setText(
                obj.getString("opponentNameSurname"));
        ((TextView)findViewById(R.id.textViewProOpponent)).setText(obj.getString("opponentPercent"));
        if (result.equals(getResources().getString(R.string.lose)))
            ((TextView)findViewById(R.id.textViewProOpponent)).setTextAppearance(getApplicationContext(),R.style.bold_red_textview_style_size_30sp);

        (findViewById(R.id.buttonSeeStatistics)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(GameEvaluationActivity.this,GameStatisticsActivity.class);
                intent1.putExtra("obj",getIntent().getBundleExtra("obj"));
                startActivity(intent1);
            }
        });
        (findViewById(R.id.buttonPlayAgain)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(GameEvaluationActivity.this,ChooseStateActivity.class);
                startActivity(intent1);
                finish();
            }
        });
    }
}
