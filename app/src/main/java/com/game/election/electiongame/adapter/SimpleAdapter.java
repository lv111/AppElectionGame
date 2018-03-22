package com.game.election.electiongame.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.game.election.electiongame.R;

import java.util.ArrayList;

public class SimpleAdapter extends ArrayAdapter<String> {

    ArrayList<String> data;
    Context context;

    //trieda so vsetkymi objektmi, s ktorymi sa v adapteri pracuje
    public class ViewHolder {
        TextView textView;
    }

    //konstruktor
    public SimpleAdapter(ArrayList<String> data, Context context) {
        super(context, R.layout.item_of_listview_simple_only_textview, data);
        this.data = data;
        this.context = context;
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
        ViewHolder viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.item_of_listview_simple_only_textview, parent, false);

        //inicializacia objektov z triedy ViewHolder
        viewHolder.textView = convertView.findViewById(R.id.textViewText);

        //nastavenie objektov z triedy ViewHolder
        viewHolder.textView.setText(getItem(position));

        //nastavenie typu pisma
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/luckiestguy_regular.ttf");
        viewHolder.textView.setTypeface(typeface);

        convertView.setTag(viewHolder);
        return convertView;
    }

    /*
    *
    * funkcia pre aktualizaciu ArrayListu data
    *
    * */
    public void updateData(ArrayList<String> data){
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }
}