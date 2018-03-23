package com.game.election.electiongame.classes;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import com.game.election.electiongame.R;


public class MyRadioButton extends LinearLayout {

    /*
    *
    * trieda, kde sa definuje RadioButton, ktory je pouzity pri dialogu, kde si pouzivatel vybera oblast investicie alebo temu na rozhovor
    *
    * */


    TogetherFunctions tf;
    AppCompatImageView imageView;       //objekt pre obrazok v RadioButtone - kruzok
    AppCompatTextView textView;         //objekt pre text v RadioButtone
    String text;                        //text v RadioButtone
    boolean checked;                    //identifikuje, ci je RadioButton oznaceny
    final String TYPEFACE_PATH = "fonts/firasans_regular.ttf";                          //typ pisma pre zobrazeny text
    final int TEXT_STYLE_NOT_CHECKED = R.style.regular_grey_textview_style_size_12sp;   //styl pisma, ak RadioButton nie je oznaceny
    final int TEXT_STYLE_CHECKED_BLUE = R.style.regular_blue_textview_style_size_15sp;  //styl pisma, ak RadioButton je oznaceny
    final int TEXT_STYLE_CHECKED_RED = R.style.regular_red_textview_style_size_15sp;    //styl pisma, ak RadioButton je oznaceny


    //konstruktor
    public MyRadioButton(Context context) {
        super(context);
        tf = new TogetherFunctions();
        initViews();
    }


    /*
    *
    * funkcia, ktora nastavuje objekty ImageView a TextView
    *
    * */
    private void initViews() {
        //nastavenie LinearLayoutu pre RadioButton
        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1) {{
            setPadding(0,getResources().getDimensionPixelOffset(R.dimen.space_5dp),0,getResources().getDimensionPixelOffset(R.dimen.space_5dp));
        }});
        setOrientation(HORIZONTAL);
        setGravity(Gravity.START);

        //nastavenie ImageViewu pre RadioButton
        imageView = new AppCompatImageView(getContext());
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) {{
            gravity = Gravity.CENTER_VERTICAL;
        }});
        imageView.setImageResource(R.drawable.not_checked);
        addView(imageView);

        //nastavenie TextViewu pre RadioButton
        textView = new AppCompatTextView(getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) {{
            gravity = Gravity.CENTER_VERTICAL;
            leftMargin = getResources().getDimensionPixelOffset(R.dimen.space_8dp);
        }});
        textView.setText(text);
        textView.setTypeface(Typeface.createFromAsset(getContext().getAssets(),TYPEFACE_PATH));
        textView.setTextAppearance(getContext(),TEXT_STYLE_NOT_CHECKED);
        checked = false;
        addView(textView);
    }


    /*
    *
    * funkcia pre nastavenie textu v RadioButtone
    *
    * */
    public void setText(String text) {
        this.text = text;
        textView.setText(text);
    }


    /*
    *
    * funkcia pre nastavenie oznacenia alebo odznacenia RadioButtona
    *
    * */
    public void setChecked(boolean checked, String color) {
        this.checked = checked;
        if (this.checked) {
            //oznacenie RadioButtona
            switch (color) {
                case "red": {
                    imageView.setImageResource(R.drawable.checked_red);
                    textView.setTextAppearance(getContext(),TEXT_STYLE_CHECKED_RED);
                    break;
                }
                case "blue": {
                    imageView.setImageResource(R.drawable.checked_blue);
                    textView.setTextAppearance(getContext(),TEXT_STYLE_CHECKED_BLUE);
                    break;
                }
            }
        }
        else {
            //odznacenie RadioButtona
            textView.setTextAppearance(getContext(),TEXT_STYLE_NOT_CHECKED);
            imageView.setImageResource(R.drawable.not_checked);
        }
        textView.setTypeface(Typeface.createFromAsset(getContext().getAssets(),TYPEFACE_PATH));
    }


    /*
    *
    * funkcia pre vratenie, ci je RadioButton oznaceny
    *
    * */
    public boolean getChecked() {
        return checked;
    }



    public void setColor() {

    }
}
