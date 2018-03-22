package com.game.election.electiongame.classes;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.game.election.electiongame.R;
import com.game.election.electiongame.provider.Contract;
import com.game.election.electiongame.service.ClickMusicService;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class TogetherFunctions {

    public void checkIfIsClick(Context context, float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        if (differenceX <= 100 && differenceY <= 100)
            playMusic(context);
    }

    public void playMusic(Context context) {
        Intent music = new Intent();
        music.setClass(context, ClickMusicService.class);
        context.startService(music);
    }

    public double getPercent(double part, long full) {
        return part/full*100;
    }

    public double getImpactOfProblemSolvability(double problemSolvability, int startInterval, int endInterval) {
        if (startInterval < endInterval) { // interval <3;1>

            //problemSolvability su cisla z intervalu <1;5>
            //upravia sa na interval <-5;-1>
            problemSolvability = problemSolvability - 6;
            //upravia sa na interval <5;1>
            problemSolvability = problemSolvability * -1;
            //dalej sa musia upravit na interval <3;1>

        }
        // druhy interval moze byt interval <1;3>, ten sa nijako nepripravuje dopredu

        //ziska sa cislo, ktore nasledne pouzijeme pri odcitani ... pre 1.5 to bude cislo 1, pre 2 to bude cislo 2, pre 2.5 to bude cislo 3
        double substractNumber = (problemSolvability - 1) / 0.5;
        //odcita sa cislo ... pre 1.5 sa odcita 0.25 a dosiahne sa 1.25, pre 2 sa odcita 0.5 a dosiahne sa 1.5
        problemSolvability = problemSolvability - 0.25 * substractNumber;

        return problemSolvability;
    }

    public double getRandomNumberFromInterval(double down, double up) {
        int max = (int)((up-down)*10000);
        return ((double) new Random().nextInt(max)) / 10000 + down;
    }

    public double getImpactOfFeature(int feature) {
        if (feature < 5) return getRandomNumberFromInterval(0,0.05);
        else if (feature < 10) return getRandomNumberFromInterval(0.05,0.08);
        else if (feature < 15) return getRandomNumberFromInterval(0.08,0.11);
        else if (feature < 20) return getRandomNumberFromInterval(0.11,0.15);
        else if (feature < 25) return getRandomNumberFromInterval(0.15,0.20);
        else if (feature < 30) return getRandomNumberFromInterval(0.20,0.25);
        else if (feature < 35) return getRandomNumberFromInterval(0.25,0.30);
        else if (feature < 40) return getRandomNumberFromInterval(0.30,0.35);
        else if (feature < 45) return getRandomNumberFromInterval(0.35,0.45);
        else if (feature < 50) return getRandomNumberFromInterval(0.45,0.53);
        else if (feature < 55) return getRandomNumberFromInterval(0.53,0.60);
        else if (feature < 60) return getRandomNumberFromInterval(0.60,0.65);
        else if (feature < 65) return getRandomNumberFromInterval(0.65,0.70);
        else if (feature < 70) return getRandomNumberFromInterval(0.70,0.75);
        else if (feature < 75) return getRandomNumberFromInterval(0.75,0.80);
        else if (feature < 80) return getRandomNumberFromInterval(0.80,0.85);
        else if (feature < 85) return getRandomNumberFromInterval(0.85,0.89);
        else if (feature < 90) return getRandomNumberFromInterval(0.89,0.92);
        else if (feature < 95) return getRandomNumberFromInterval(0.92,0.95);
        else return getRandomNumberFromInterval(0.95,1);
    }

    public String getPrettyFormattedTextFromNumberWithDecimal(double number) {
        NumberFormat f = new DecimalFormat("#0.00");
        return f.format(number);
    }

    public String getPrettyFormattedTextFromNumberWithThousands(long number) {
        StringBuilder stringBuilder = new StringBuilder();
        String text = String.valueOf(number);

        int addedNumbers = 0;
        for (int i = 0; i < text.length(); i++) {
            stringBuilder.append(text.charAt(i));
            addedNumbers++;
            if (text.length() % 3 == i + 1) {
                stringBuilder.append(' ');
                addedNumbers = 0;
            }
            if (addedNumbers == 3) {
                stringBuilder.append(' ');
                addedNumbers = 0;
            }
        }
        return stringBuilder.toString();
    }

    public TextView getTextView(Context context, String text, int id, String number, float textSize, ViewGroup.LayoutParams params, boolean center, int style, String fontType, boolean allCaps) {
        Typeface typeface = null;
        switch (fontType) {
            case "bold": {
                typeface = Typeface.createFromAsset(context.getAssets(),"fonts/luckiestguy_regular.ttf");
                break;
            }
            case "regular": {
                typeface = Typeface.createFromAsset(context.getAssets(),"fonts/firasans_regular.ttf");
            }
        }
        TextView tv = new TextView(context);
        tv.setId(View.generateViewId());
        tv.setTextAppearance(context, style);
        tv.setAllCaps(allCaps);
        if (text != null)
            tv.setText(text);
        else if (id != -1)
            tv.setText(String.format(context.getResources().getString(id), number));
        tv.setTextSize( TypedValue.COMPLEX_UNIT_PX,textSize);
        if (typeface != null)
            tv.setTypeface(typeface);
        tv.setLayoutParams(params);
        if (center)
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return tv;
    }

    public ImageView getImageView(Context context, ViewGroup.LayoutParams params, Drawable background, int backgroundColor, int idImageResource, float alpha, int startPadding, int topPadding, int endPadding, int bottomPadding) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(params);
        if (background != null)
            imageView.setBackground(background);
        if (backgroundColor != -1)
            imageView.setBackgroundColor(backgroundColor);
        if (idImageResource != -1)
            imageView.setImageResource(idImageResource);
        if (alpha != -1)
            imageView.setAlpha(alpha);
        imageView.setId(View.generateViewId());
        imageView.setPadding(startPadding,topPadding,endPadding,bottomPadding);
        return imageView;
    }

    public Button getButton(Context context, ViewGroup.LayoutParams params, String text, int style, float textSize, int startPadding, int topPadding, int endPadding, int bottomPadding) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/luckiestguy_regular.ttf");
        Button button = new Button(context);
        button.setText(text);
        button.setTextAppearance(context, style);
        button.setTextSize( TypedValue.COMPLEX_UNIT_PX,textSize);
        button.setTypeface(typeface);
        button.setLayoutParams(params);
        button.setId(View.generateViewId());
        button.setPadding(startPadding,topPadding,endPadding,bottomPadding);
        return button;
    }

    public LinearLayout getLinearLayout(Context context,int backgroundColor, ViewGroup.LayoutParams layoutParams, int orientation, int gravity, int startPadding, int topPadding, int endPadding, int bottomPadding) {
        LinearLayout linearLayout = new LinearLayout(context);
        if (backgroundColor != -1)
            linearLayout.setBackgroundColor(backgroundColor);
        linearLayout.setLayoutParams(layoutParams);
        if (orientation != -1)
            linearLayout.setOrientation(orientation);
        if (gravity != -1)
            linearLayout.setGravity(gravity);
        linearLayout.setId(View.generateViewId());
        linearLayout.setPadding(startPadding,topPadding,endPadding,bottomPadding);
        return  linearLayout;
    }

    public RelativeLayout getRelativeLayout(Context context, ViewGroup.LayoutParams layoutParams) {
        RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setLayoutParams(layoutParams);
        return relativeLayout;
    }

    public Dialog getDialog(Context context, boolean cancelable, int idLayout) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(cancelable);
        dialog.setContentView(idLayout);
        return dialog;
    }

    public Dialog getDialogSimpleQuestion(Context context, boolean cancelable, int idLayout, String textTitle, String textQuestion, String textButtonPositive,
                                          String textButtonNegative) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(cancelable);
        dialog.setContentView(idLayout);

        ((TextView)dialog.findViewById(R.id.textViewTitle)).setText(textTitle);
        ((TextView)dialog.findViewById(R.id.textViewText)).setText(textQuestion);
        ((Button)dialog.findViewById(R.id.buttonPositiveAnswer)).setText(textButtonPositive);
        ((Button)dialog.findViewById(R.id.buttonNegativeButton)).setText(textButtonNegative);
        return dialog;
    }

    public Dialog getDialogSimpleNotice(Context context, boolean cancelable, int idLayout, String textTitle, String textNotice) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(cancelable);
        dialog.setContentView(idLayout);

        ((TextView)dialog.findViewById(R.id.textViewTitle)).setText(textTitle);
        ((TextView)dialog.findViewById(R.id.textViewText)).setText(textNotice);

        return dialog;
    }









    public String getMostUsedGameAction(Context context, ArrayList<ActionOfGame> actionOfGames, int character) {
        int countOfInterview = 0,countOfInvestment = 0;
        for (int i = 0; i < actionOfGames.size(); i++) {
            ActionOfGame actionOfGame = actionOfGames.get(i);
            if (actionOfGame.getCharacter() == character) {
                if (actionOfGame.getActionType().equals("interview"))
                    countOfInterview++;
                else if (actionOfGame.getActionType().equals("investment"))
                    countOfInvestment++;
            }
        }

        if (countOfInterview > countOfInvestment)
            return context.getResources().getString(R.string.interview);
        else if (countOfInterview < countOfInvestment)
            return context.getResources().getString(R.string.investment);
        else
            return "-";
    }

    public ArrayList<String> getMostUsedProblem(Context context, ArrayList<ActionOfGame> actionOfGames, int character) {
        ArrayList<Integer> idProblems = new ArrayList<>();
        for (int i = 0; i < actionOfGames.size(); i++) {
            ActionOfGame actionOfGame = actionOfGames.get(i);
            if (actionOfGame.getCharacter() == character) {
                ArrayList<Region> regionsOfAction = actionOfGame.getRegionsOfGames();

                for (int j = 0; j < regionsOfAction.size(); j++) {
                    Region region = regionsOfAction.get(j);
                    if (region.getRegionOfAction()) {
                        idProblems.add(region.getIdProblem());
                        break;
                    }
                }
            }
        }

        Bundle obj = new Bundle();
        obj.putIntegerArrayList("idProblems",idProblems);
        obj = context.getContentResolver().call(Contract.Problems.CONTENT_URI,"return problemNames by idProblems",null,obj);
        ArrayList<String> problemNames = obj.getStringArrayList("problemNames");
        ArrayList<Integer> problemCount = new ArrayList<>();
        for (int i = 0; i < problemNames.size(); i++) {
            problemCount.add(Collections.frequency(problemNames,problemNames.get(i)));
        }
        for (int i = problemNames.size() - 1; i >= 0; i--) {
            if (Collections.frequency(problemNames,problemNames.get(i)) > 1) {
                problemNames.remove(i);
                problemCount.remove(i);
            }
        }

        int max = 0;
        for (int i = 0; i < problemCount.size(); i++) {
            if (problemCount.get(i) > max)
                max = problemCount.get(i);
        }
        ArrayList<String> mostUsedProblems = new ArrayList<>();
        for (int i = 0; i < problemCount.size(); i++) {
            if (problemCount.get(i) == max)
                if (mostUsedProblems.indexOf(problemNames.get(i)) == -1)
                    mostUsedProblems.add(problemNames.get(i));
        }
        return mostUsedProblems;
    }

    public ArrayList<String> getMostUsedRegion(ArrayList<ActionOfGame> actionOfGames, int character) {
        ArrayList<String> regionNames = new ArrayList<>();
        for (int i = 0; i < actionOfGames.size(); i++) {
            ActionOfGame actionOfGame = actionOfGames.get(i);
            if (actionOfGame.getCharacter() == character) {
                ArrayList<Region> regionsOfAction = actionOfGame.getRegionsOfGames();

                for (int j = 0; j < regionsOfAction.size(); j++) {
                    Region region = regionsOfAction.get(j);
                    if (region.getRegionOfAction()) {
                        regionNames.add(region.getRegionName());
                        break;
                    }
                }
            }
        }

        ArrayList<Integer> regionCount = new ArrayList<>();
        for (int i = 0; i < regionNames.size(); i++) {
            regionCount.add(Collections.frequency(regionNames,regionNames.get(i)));
        }

        int max = 0;
        for (int i = 0; i < regionCount.size(); i++) {
            if (regionCount.get(i) > max)
                max = regionCount.get(i);
        }
        ArrayList<String> mostUsedRegions = new ArrayList<>();
        for (int i = 0; i < regionCount.size(); i++) {
            if (regionCount.get(i) == max)
                if (mostUsedRegions.indexOf(regionNames.get(i)) == -1)
                    mostUsedRegions.add(regionNames.get(i));
        }
        return mostUsedRegions;
    }

    public ArrayList<String> getMostVotedRegion(ArrayList<Region> regions, int character) {
        double max = 0;
        for (int i = 0; i < regions.size(); i++) {
            double voted = 0;
            if (character == 1) //kandidat
                voted = regions.get(i).getAfterProCharacter();
            else if (character == 0) //oponent
                voted = regions.get(i).getAfterProOpponent();
            if (voted > max)
                max = voted;
        }
        ArrayList<String> mostVotedRegions = new ArrayList<>();
        for (int i = 0; i < regions.size(); i++) {
            double voted = 0;
            if (character == 1) //kandidat
                voted = regions.get(i).getAfterProCharacter();
            else if (character == 0) //oponent
                voted = regions.get(i).getAfterProOpponent();
            if (voted == max)
                if (mostVotedRegions.indexOf(regions.get(i).getRegionName()) == -1)
                    mostVotedRegions.add(regions.get(i).getRegionName());
        }
        return mostVotedRegions;
    }

    public ArrayList<String> getLeastVotedRegion(ArrayList<Region> regions, int character) {
        double min = 0;
        if (character == 1) //kandidat
            min = regions.get(0).getAfterProCharacter();
        else if (character == 0) //oponent
            min = regions.get(0).getAfterProOpponent();

        for (int i = 1; i < regions.size(); i++) {
            double voted = 0;
            if (character == 1) //kandidat
                voted = regions.get(i).getAfterProCharacter();
            else if (character == 0) //oponent
                voted = regions.get(i).getAfterProOpponent();
            if (voted < min)
                min = voted;
        }
        ArrayList<String> leastVotedRegions = new ArrayList<>();
        for (int i = 1; i < regions.size(); i++) {
            double voted = 0;
            if (character == 1) //kandidat
                voted = regions.get(i).getAfterProCharacter();
            else if (character == 0) //oponent
                voted = regions.get(i).getAfterProOpponent();
            if (voted == min)
                if (leastVotedRegions.indexOf(regions.get(i).getRegionName()) == -1)
                    leastVotedRegions.add(regions.get(i).getRegionName());
        }

        return leastVotedRegions;
    }

    public ArrayList<String> getMostEffectedProblem(Context context, ArrayList<Region> regions, ArrayList<ActionOfGame> actionOfGames, int character) {
        for (int i = 0; i < regions.size(); i++) {
            regions.get(i).setAfterProCharacter(0);
            regions.get(i).setAfterProOpponent(0);
        }

        ArrayList<Integer> allIdProblems = new ArrayList<>();
        ArrayList<Double> allProVoters = new ArrayList<>();
        for (int i = 0; i < actionOfGames.size(); i++) {
            ActionOfGame actionOfGame = actionOfGames.get(i);
            ArrayList<Region> regionOfGame = actionOfGame.getRegionsOfGames();

            int idProblem = 0;
            for (int j = 0; j < regionOfGame.size(); j++) {
                Region region = regionOfGame.get(j);
                if (region.getRegionOfAction()) {
                    idProblem = region.getIdProblem();
                    break;
                }
            }
            for (int j = 0; j < regionOfGame.size(); j++) {
                Region region = regionOfGame.get(j);
                int positionOfRegion = 0;
                for (int k = 0; k < regions.size(); k++) {
                    if (regions.get(k).getId() == region.getId()) {
                        positionOfRegion = k;
                        k = regions.size();
                    }
                }
                Region regionToChange = regions.get(positionOfRegion);
                if (character == 1)
                    regionToChange.setAfterProCharacter(region.getAfterProCharacter());
                else if (character == 0)
                    regionToChange.setAfterProOpponent(region.getAfterProOpponent());
                regions.set(positionOfRegion, regionToChange);
            }

            double allPro = 0;
            for (int j = 0; j < regions.size(); j++) {
                Region region = regions.get(j);
                if (character == 1)
                    allPro += region.getAfterProCharacter();
                else if (character == 0)
                    allPro += region.getAfterProOpponent();
            }
            allIdProblems.add(idProblem);
            allProVoters.add(allPro);
        }

        ArrayList<Double> proVoters = new ArrayList<>();
        ArrayList<Integer> idProblems = new ArrayList<>();
        for (int i = 0; i < actionOfGames.size(); i++) {
            ActionOfGame actionOfGame = actionOfGames.get(i);
            if (actionOfGame.getCharacter() == character) {
                if (i - 1 >= 0)
                    proVoters.add(allProVoters.get(i) - allProVoters.get(i - 1));
                else
                    proVoters.add(allProVoters.get(i) - 0);
                idProblems.add(allIdProblems.get(i));
            }
        }

        double max = 0;
        for (int i = 0; i < proVoters.size(); i++)
            if (proVoters.get(i) > max)
                max = proVoters.get(i);

        ArrayList<Integer> idProblemsWithMostEffect = new ArrayList<>();
        for (int i = 0; i < proVoters.size(); i++)
            if (proVoters.get(i) == max)
                if (idProblemsWithMostEffect.indexOf(idProblems.get(i)) == -1)
                    idProblemsWithMostEffect.add(idProblems.get(i));

        Bundle obj = new Bundle();
        obj.putIntegerArrayList("idProblems",idProblemsWithMostEffect);
        obj = context.getContentResolver().call(Contract.Problems.CONTENT_URI,"return problemNames by idProblems",null,obj);
        return obj.getStringArrayList("problemNames");
    }

    public ArrayList<String> getLeastEffectedProblem(Context context, ArrayList<Region> regions, ArrayList<ActionOfGame> actionOfGames, int character) {
        for (int i = 0; i < regions.size(); i++) {
            regions.get(i).setAfterProCharacter(0);
            regions.get(i).setAfterProOpponent(0);
        }

        ArrayList<Integer> allIdProblems = new ArrayList<>();
        ArrayList<Double> allProVoters = new ArrayList<>();
        for (int i = 0; i < actionOfGames.size(); i++) {
            ActionOfGame actionOfGame = actionOfGames.get(i);
            ArrayList<Region> regionOfGame = actionOfGame.getRegionsOfGames();

            int idProblem = 0;
            for (int j = 0; j < regionOfGame.size(); j++) {
                Region region = regionOfGame.get(j);
                if (region.getRegionOfAction()) {
                    idProblem = region.getIdProblem();
                    break;
                }
            }
            for (int j = 0; j < regionOfGame.size(); j++) {
                Region region = regionOfGame.get(j);
                int positionOfRegion = 0;
                for (int k = 0; k < regions.size(); k++) {
                    if (regions.get(k).getId() == region.getId()) {
                        positionOfRegion = k;
                        k = regions.size();
                    }
                }
                Region regionToChange = regions.get(positionOfRegion);
                if (character == 1)
                    regionToChange.setAfterProCharacter(region.getAfterProCharacter());
                else if (character == 0)
                    regionToChange.setAfterProOpponent(region.getAfterProOpponent());
                regions.set(positionOfRegion, regionToChange);
            }

            double allPro = 0;
            for (int j = 0; j < regions.size(); j++) {
                Region region = regions.get(j);
                if (character == 1)
                    allPro += region.getAfterProCharacter();
                else if (character == 0)
                    allPro += region.getAfterProOpponent();
            }
            allIdProblems.add(idProblem);
            allProVoters.add(allPro);
        }

        ArrayList<Double> proVoters = new ArrayList<>();
        ArrayList<Integer> idProblems = new ArrayList<>();
        for (int i = 0; i < actionOfGames.size(); i++) {
            ActionOfGame actionOfGame = actionOfGames.get(i);
            if (actionOfGame.getCharacter() == character) {
                if (i - 1 >= 0)
                    proVoters.add(allProVoters.get(i) - allProVoters.get(i - 1));
                else
                    proVoters.add(allProVoters.get(i) - 0);
                idProblems.add(allIdProblems.get(i));
            }
        }

        double min = proVoters.get(0);
        for (int i = 1; i < proVoters.size(); i++)
            if (proVoters.get(i) < min)
                min = proVoters.get(i);

        ArrayList<Integer> idProblemsWithLeastEffect = new ArrayList<>();
        for (int i = 0; i < proVoters.size(); i++)
            if (proVoters.get(i) == min)
                if (idProblemsWithLeastEffect.indexOf(idProblems.get(i)) == -1)
                    idProblemsWithLeastEffect.add(idProblems.get(i));

        Bundle obj = new Bundle();
        obj.putIntegerArrayList("idProblems",idProblemsWithLeastEffect);
        obj = context.getContentResolver().call(Contract.Problems.CONTENT_URI,"return problemNames by idProblems",null,obj);
        return obj.getStringArrayList("problemNames");
    }

}
