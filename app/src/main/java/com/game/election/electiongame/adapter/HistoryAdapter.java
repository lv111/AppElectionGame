package com.game.election.electiongame.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.game.election.electiongame.R;
import com.game.election.electiongame.classes.History;
import com.game.election.electiongame.classes.TogetherFunctions;

import java.util.ArrayList;


public class HistoryAdapter extends ArrayAdapter<History> {

    TogetherFunctions tf;
    ArrayList<History> data;

    //trieda so vsetkymi objektmi, s ktorymi sa v adapteri pracuje
    private class ViewHolder {
        TextView textViewWeek;
        ImageView imageViewLine;
        TextView textViewFirstNameAndSurname;
        TextView textViewFirstLastAction;
        ImageView imageViewFirstProCandidate;
        TextView textViewFirstProCandidate;
        ImageView imageViewFirstProOpponent;
        TextView textViewFirstProOpponent;
        ImageView imageViewLine2;
        TextView textViewSecondNameAndSurname;
        TextView textViewSecondLastAction;
        ImageView imageViewSecondProCandidate;
        TextView textViewSecondProCandidate;
        ImageView imageViewSecondProOpponent;
        TextView textViewSecondProOpponent;
    }

    //konstruktor
    public HistoryAdapter(ArrayList<History> data, Context context) {
        super(context, R.layout.item_of_listview_saved_game, data);
        tf = new TogetherFunctions();
        this.data = data;
    }

    /*
    *
    * funkcia pre vytvorenie polozky
    *
    * */
    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        History history = getItem(position);
        ViewHolder viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.item_of_listview_history, parent, false);

        //inicializacia objektov z triedy ViewHolder
        viewHolder.textViewWeek = convertView.findViewById(R.id.textViewWeek);
        viewHolder.imageViewLine = convertView.findViewById(R.id.imageViewLine);
        viewHolder.textViewFirstNameAndSurname = convertView.findViewById(R.id.textViewFirstNameAndSurname);
        viewHolder.textViewFirstLastAction = convertView.findViewById(R.id.textViewFirstLastAction);
        viewHolder.imageViewFirstProCandidate = convertView.findViewById(R.id.imageViewFirstProCandidate);
        viewHolder.textViewFirstProCandidate = convertView.findViewById(R.id.textViewFirstProCandidate);
        viewHolder.imageViewFirstProOpponent = convertView.findViewById(R.id.imageViewFirstProOpponent);
        viewHolder.textViewFirstProOpponent = convertView.findViewById(R.id.textViewFirstProOpponent);
        viewHolder.imageViewLine2 = convertView.findViewById(R.id.imageViewLine2);
        viewHolder.textViewSecondNameAndSurname = convertView.findViewById(R.id.textViewSecondNameAndSurname);
        viewHolder.textViewSecondLastAction = convertView.findViewById(R.id.textViewSecondLastAction);
        viewHolder.imageViewSecondProCandidate = convertView.findViewById(R.id.imageViewSecondProCandidate);
        viewHolder.textViewSecondProCandidate = convertView.findViewById(R.id.textViewSecondProCandidate);
        viewHolder.imageViewSecondProOpponent = convertView.findViewById(R.id.imageViewSecondProOpponent);
        viewHolder.textViewSecondProOpponent = convertView.findViewById(R.id.textViewSecondProOpponent);

        //nastavenie objektov z triedy ViewHolder
        viewHolder.textViewWeek.setText(String.format(getContext().getResources().getString(R.string.num_week),history.getWeek()));
        viewHolder.imageViewLine.setBackgroundColor(getContext().getResources().getColor(R.color.color_grey_dark));
        viewHolder.textViewFirstNameAndSurname.setText(history.getFirstNameAndSurname());
        viewHolder.textViewFirstLastAction.setText(history.getFirstLastAction());
        viewHolder.imageViewFirstProCandidate.setImageResource(R.drawable.like);
        viewHolder.textViewFirstProCandidate.setText(
                tf.getPrettyFormattedTextFromNumberWithDecimal(tf.getPercent(history.getFirstProCandidate(),history.getPopulation())) + " %");
        viewHolder.imageViewFirstProOpponent.setImageResource(R.drawable.dislike);
        viewHolder.textViewFirstProOpponent.setText(
                tf.getPrettyFormattedTextFromNumberWithDecimal(tf.getPercent(history.getFirstProOpponent(),history.getPopulation())) + " %");
        viewHolder.imageViewLine2.setBackgroundColor(getContext().getResources().getColor(R.color.color_grey_dark));
        if (history.getSecondLastAction().length() != 0 && history.getSecondProCandidate() != -1 && history.getSecondProOpponent() != -1) {
            viewHolder.textViewSecondNameAndSurname.setText(history.getSecondNameAndSurname());
            viewHolder.textViewSecondLastAction.setText(history.getSecondLastAction());
            viewHolder.imageViewSecondProCandidate.setImageResource(R.drawable.like);
            viewHolder.textViewSecondProCandidate.setText(
                    tf.getPrettyFormattedTextFromNumberWithDecimal(tf.getPercent(history.getSecondProCandidate(), history.getPopulation())) + " %");
            viewHolder.imageViewSecondProOpponent.setImageResource(R.drawable.dislike);
            viewHolder.textViewSecondProOpponent.setText(
                    tf.getPrettyFormattedTextFromNumberWithDecimal(tf.getPercent(history.getSecondProOpponent(), history.getPopulation())) + " %");
        }
        else {
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(0,0);
            params.setMargins(0,0,0,0);
            viewHolder.textViewSecondNameAndSurname.setLayoutParams(params);
            viewHolder.textViewSecondLastAction.setLayoutParams(params);

            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(0,0);
            params.setMargins(0,0,0,0);
            viewHolder.imageViewSecondProCandidate.setLayoutParams(params1);
            viewHolder.textViewSecondProCandidate.setLayoutParams(params1);
            viewHolder.imageViewSecondProOpponent.setLayoutParams(params1);
            viewHolder.textViewSecondProOpponent.setLayoutParams(params1);
        }
        if (!history.isFirstOnStep()) { //prvy ide oponent, musim prehodit smery pisma
            viewHolder.textViewFirstNameAndSurname.setGravity(Gravity.END);
            viewHolder.textViewFirstLastAction.setGravity(Gravity.END);
            viewHolder.textViewSecondNameAndSurname.setGravity(Gravity.START);
            viewHolder.textViewSecondProCandidate.setGravity(Gravity.START);
        }

        //nastavenie typu pisma
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),"fonts/firasans_regular.ttf");
        viewHolder.textViewWeek.setTypeface(typeface);
        viewHolder.textViewFirstNameAndSurname.setTypeface(typeface);
        viewHolder.textViewFirstLastAction.setTypeface(typeface);
        viewHolder.textViewFirstProCandidate.setTypeface(typeface);
        viewHolder.textViewFirstProOpponent.setTypeface(typeface);
        viewHolder.textViewSecondNameAndSurname.setTypeface(typeface);
        viewHolder.textViewSecondLastAction.setTypeface(typeface);
        viewHolder.textViewSecondProCandidate.setTypeface(typeface);
        viewHolder.textViewSecondProOpponent.setTypeface(typeface);

        return convertView;
    }
}