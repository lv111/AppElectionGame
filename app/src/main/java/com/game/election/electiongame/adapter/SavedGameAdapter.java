package com.game.election.electiongame.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.game.election.electiongame.R;
import com.game.election.electiongame.classes.SavedGame;
import com.game.election.electiongame.classes.TogetherFunctions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class SavedGameAdapter extends ArrayAdapter<SavedGame> {

    ArrayList<SavedGame> data;
    TogetherFunctions tf;
    Context context;

    //trieda so vsetkymi objektmi, s ktorymi sa v adapteri pracuje
    private class ViewHolder {
        TextView textViewCandidateNameSurname;
        TextView textViewProCandidate;
        TextView textViewOpponentNameSurname;
        TextView textViewProOpponent;
        TextView textViewWhere;
        TextView textViewWhen;
    }

    //konstruktor
    public SavedGameAdapter(ArrayList<SavedGame> data, Context context) {
        super(context, R.layout.item_of_listview_saved_game, data);
        tf = new TogetherFunctions();
        this.context = context;
        this.data = data;
    }

    /*
    *
    * funkcia pre vytvorenie polozky
    *
    * */
    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        SavedGame game = getItem(position);
        ViewHolder viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.item_of_listview_saved_game, parent, false);

        //inicializacia objektov z triedy ViewHolder
        viewHolder.textViewCandidateNameSurname = convertView.findViewById(R.id.textViewCharacterNameSurname);
        viewHolder.textViewProCandidate = convertView.findViewById(R.id.textViewProCharacter);
        viewHolder.textViewOpponentNameSurname = convertView.findViewById(R.id.textViewOpponentNameSurname);
        viewHolder.textViewProOpponent = convertView.findViewById(R.id.textViewProOpponent);
        viewHolder.textViewWhere = convertView.findViewById(R.id.textViewWhere);
        viewHolder.textViewWhen = convertView.findViewById(R.id.textViewWhen);

        //nastavenie objektov z triedy ViewHolder
        long population = (long)(game.getProCharacter() + game.getUndecided() + game.getProOpponent());
        viewHolder.textViewCandidateNameSurname.setText(game.getCharacterNameSurname());
        viewHolder.textViewProCandidate.setText(
                String.valueOf(tf.getPrettyFormattedTextFromNumberWithDecimal(tf.getPercent(game.getProCharacter(),population))) + " %");
        viewHolder.textViewOpponentNameSurname.setText(game.getOpponentNameSurname());
        viewHolder.textViewProOpponent.setText(
                String.valueOf(tf.getPrettyFormattedTextFromNumberWithDecimal(tf.getPercent(game.getProOpponent(),population))) + " %");
        viewHolder.textViewWhere.setText(game.getStateName());
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(game.getChanged());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("d MM yyyy, HH:mm:ss");
        String date = format.format(new Date(game.getChanged()));
        viewHolder.textViewWhen.setText(String.format(context.getResources().getString(R.string.saved_at_date),date));

        //nastavenie typu pisma
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/luckiestguy_regular.ttf");
        viewHolder.textViewCandidateNameSurname.setTypeface(typeface);
        viewHolder.textViewProCandidate.setTypeface(typeface);
        viewHolder.textViewOpponentNameSurname.setTypeface(typeface);
        viewHolder.textViewProOpponent.setTypeface(typeface);


        convertView.setTag(game.getId());
        return convertView;
    }

    /*
    *
    * funkcia pre aktualizaciu ArrayListu data
    *
    * */
    public void updateData(ArrayList<SavedGame> data){
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

}