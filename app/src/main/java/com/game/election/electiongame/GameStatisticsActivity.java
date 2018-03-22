package com.game.election.electiongame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.game.election.electiongame.classes.ActionOfGame;
import com.game.election.electiongame.classes.Region;
import com.game.election.electiongame.classes.TogetherFunctions;

import java.util.ArrayList;

public class GameStatisticsActivity extends AppCompatActivity {

    TogetherFunctions tf;
    private float startX, startY;//premenne, ktore oznacuju bod pri dotyku pouzivatela s obrazovkou
    ArrayList<Region> regions;
    ArrayList<ActionOfGame> actionOfGames;

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
        setContentView(R.layout.activity_game_statistics);
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

    private void setArrayLists(Bundle obj) {
        int regionsSize = obj.getInt("regionsSize");
        regions = new ArrayList<>();
        ArrayList<Integer> idRegions = new ArrayList<>();
        for (int i = 0; i < regionsSize; i++) {
            Bundle objRegion = obj.getBundle("region"+String.valueOf(i));
            int id = objRegion.getInt("id");
            String regionName = objRegion.getString("regionName");
            regions.add(new Region(id,regionName,"",-1,-1,-1,false,
                    -1,"", "",0,0,0,0,0,
                    0));
            idRegions.add(id);
        }

        int actionsSize = obj.getInt("actionSize");
        actionOfGames = new ArrayList<>();
        for (int i = 0; i < actionsSize; i++) {
            Bundle objAction = obj.getBundle("action"+String.valueOf(i));
            String actionType = objAction.getString("actionType");
            int character = objAction.getInt("character");
            int week = objAction.getInt("week");
            Bundle objRegions = objAction.getBundle("regions");
            int count = objRegions.getInt("count");
            ArrayList<Region> regionsOfAction = new ArrayList<>();
            for (int j = 0; j < count; j++) {
                Bundle objRegion = objRegions.getBundle("region" + String.valueOf(j));
                int regionOfAction = objRegion.getInt("regionOfAction");
                int idRegion = objRegion.getInt("idRegion");
                String regionName = objRegion.getString("regionName");
                int idProblem = objRegion.getInt("idProblem");
                double proCandidate = objRegion.getDouble("proCandidate");
                double undecided = objRegion.getDouble("undecided");
                double proOpponent = objRegion.getDouble("proOpponent");
                regionsOfAction.add(new Region(idRegion,regionName,"",-1,character,idProblem,regionOfAction==1,
                        week,"", actionType,proCandidate,undecided,proOpponent,proCandidate,undecided,proOpponent));

                int position = idRegions.indexOf(idRegion);
                Region region = regions.get(position);
                region.setBeforeProCharacter(proCandidate);
                region.setBeforeUndecided(undecided);
                region.setBeforeProOpponent(proOpponent);
                region.setAfterProCharacter(proCandidate);
                region.setAfterUndecided(undecided);
                region.setAfterProOpponent(proOpponent);
                regions.set(position,region);
            }
            actionOfGames.add(new ActionOfGame(week,character,actionType,regionsOfAction));
        }
    }

    private String getTextForStatistics(ArrayList<String> list) {
        String text = "";
        if (list.size() == 1)
            text = list.get(0);
        else {
            text = list.get(0);
            for (int i = 1; i < list.size(); i++) {
                text += ",\n" + list.get(i);
            }
        }
        return text;
    }

    private void setStatistics() {
        String mostUsedGameActionCandidate = tf.getMostUsedGameAction(getApplicationContext(),actionOfGames, 1);
        ((TextView)findViewById(R.id.textViewCandidateMostUsedGameAction)).setText(mostUsedGameActionCandidate);

        String mostUsedGameActionOpponent = tf.getMostUsedGameAction(getApplicationContext(),actionOfGames, 0);
        ((TextView)findViewById(R.id.textViewOpponentMostUsedGameAction)).setText(mostUsedGameActionOpponent);

        (findViewById(R.id.textViewMostUsedGameAction)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tf.getDialogSimpleNotice(GameStatisticsActivity.this,true,R.layout.dialog_simple_notice,
                        getResources().getString(R.string.statistics_most_used_game_action),
                        getResources().getString(R.string.statistics_most_used_game_action_detail)).show();
            }
        });



        ArrayList<String> mostUsedProblemCandidate = tf.getMostUsedProblem(getApplicationContext(), actionOfGames,1);
        String text = getTextForStatistics(mostUsedProblemCandidate);
        ((TextView)findViewById(R.id.textViewCandidateMostUsedProblem)).setText(text);

        ArrayList<String> mostUsedProblemOpponent = tf.getMostUsedProblem(getApplicationContext(), actionOfGames,0);
        text = getTextForStatistics(mostUsedProblemOpponent);
        ((TextView)findViewById(R.id.textViewOpponentMostUsedProblem)).setText(text);

        (findViewById(R.id.textViewMostUsedProblem)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tf.getDialogSimpleNotice(GameStatisticsActivity.this,true,R.layout.dialog_simple_notice,
                        getResources().getString(R.string.statistics_most_used_problem),
                        getResources().getString(R.string.statistics_most_used_problem_detail)).show();
            }
        });



        ArrayList<String> mostUsedRegionCandidate = tf.getMostUsedRegion(actionOfGames,1);
        text = getTextForStatistics(mostUsedRegionCandidate);
        ((TextView)findViewById(R.id.textViewCandidateMostUsedRegion)).setText(text);

        ArrayList<String> mostUsedRegionOpponent = tf.getMostUsedRegion(actionOfGames,0);
        text = getTextForStatistics(mostUsedRegionOpponent);
        ((TextView)findViewById(R.id.textViewOpponentMostUsedRegion)).setText(text);

        (findViewById(R.id.textViewMostUsedRegion)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tf.getDialogSimpleNotice(GameStatisticsActivity.this,true,R.layout.dialog_simple_notice,
                        getResources().getString(R.string.statistics_most_used_region),
                        getResources().getString(R.string.statistics_most_used_region_detail)).show();
            }
        });



        ArrayList<String> mostVotedRegionCandidate = tf.getMostVotedRegion(regions,1);
        text = getTextForStatistics(mostVotedRegionCandidate);
        ((TextView)findViewById(R.id.textViewCandidateMostVotedRegion)).setText(text);

        ArrayList<String> mostVotedRegionOpponent = tf.getMostVotedRegion(regions,0);
        text = getTextForStatistics(mostVotedRegionOpponent);
        ((TextView)findViewById(R.id.textViewOpponentMostVotedRegion)).setText(text);

        (findViewById(R.id.textViewMostVotedRegion)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tf.getDialogSimpleNotice(GameStatisticsActivity.this,true,R.layout.dialog_simple_notice,
                        getResources().getString(R.string.statistics_most_voted_region),
                        getResources().getString(R.string.statistics_most_voted_region_detail)).show();
            }
        });



        ArrayList<String> leastVotedRegionCandidate = tf.getLeastVotedRegion(regions,1);
        text = getTextForStatistics(leastVotedRegionCandidate);
        ((TextView)findViewById(R.id.textViewCandidateLeastVotedRegion)).setText(text);

        ArrayList<String> leastVotedRegionOpponent = tf.getLeastVotedRegion(regions,0);
        text = getTextForStatistics(leastVotedRegionOpponent);
        ((TextView)findViewById(R.id.textViewOpponentLeastVotedRegion)).setText(text);

        (findViewById(R.id.textViewLeastVotedRegion)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tf.getDialogSimpleNotice(GameStatisticsActivity.this,true,R.layout.dialog_simple_notice,
                        getResources().getString(R.string.statistics_least_voted_region),
                        getResources().getString(R.string.statistics_least_voted_region_detail)).show();
            }
        });



        ArrayList<String> mostEffectedProblemCandidate = tf.getMostEffectedProblem(getApplicationContext(), regions, actionOfGames, 1);
        text = getTextForStatistics(mostEffectedProblemCandidate);
        ((TextView)findViewById(R.id.textViewCandidateMostEffectedProblem)).setText(text);

        ArrayList<String> mostEffectedProblemOpponent = tf.getMostEffectedProblem(getApplicationContext(), regions, actionOfGames, 0);
        text = getTextForStatistics(mostEffectedProblemOpponent);
        ((TextView)findViewById(R.id.textViewOpponentMostEffectedProblem)).setText(text);

        (findViewById(R.id.textViewMostEffectedProblem)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tf.getDialogSimpleNotice(GameStatisticsActivity.this,true,R.layout.dialog_simple_notice,
                        getResources().getString(R.string.statistics_most_effected_problem),
                        getResources().getString(R.string.statistics_most_effected_problem_detail)).show();
            }
        });



        ArrayList<String> leastEffectedProblemCandidate = tf.getLeastEffectedProblem(getApplicationContext(), regions, actionOfGames, 1);
        text = getTextForStatistics(leastEffectedProblemCandidate);
        ((TextView)findViewById(R.id.textViewCandidateLeastEffectedProblem)).setText(text);

        ArrayList<String> leastEffectedProblemOpponent = tf.getLeastEffectedProblem(getApplicationContext(), regions, actionOfGames, 0);
        text = getTextForStatistics(leastEffectedProblemOpponent);
        ((TextView)findViewById(R.id.textViewOpponentLeastEffectedProblem)).setText(text);

        (findViewById(R.id.textViewLeastEffectedProblem)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tf.getDialogSimpleNotice(GameStatisticsActivity.this,true,R.layout.dialog_simple_notice,
                        getResources().getString(R.string.statistics_least_effected_problem),
                        getResources().getString(R.string.statistics_least_effected_problem_detail)).show();
            }
        });
    }

    private void setLayout() {
        Intent intent = getIntent();
        Bundle obj = intent.getBundleExtra("obj");

        String characterNameSurname = obj.getString("characterNameSurname");
        ((TextView) findViewById(R.id.textViewCandidate)).setText(characterNameSurname);

        String opponentNameSurname = obj.getString("opponentNameSurname");
        ((TextView) findViewById(R.id.textViewOpponent)).setText(opponentNameSurname);

        setArrayLists(obj);
        setStatistics();
    }
}
