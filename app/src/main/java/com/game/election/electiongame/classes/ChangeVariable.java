package com.game.election.electiongame.classes;


public class ChangeVariable {

    /*
    *
    * trieda pre definovanie objektu, na ktory mozno nastavit ChangeListener
    *
    * */


    private Object variable;            //hodnota objektu
    private ChangeListener listener;    //ChangeListener, ktory bude nastaveny pre objekt


    //konstruktor
    public ChangeVariable(Object variable) {
        this.variable = variable;
    }


    /*
    *
    * funkcia, ktora vracia hodnotu objektu
    *
    * */
    private Object getValue() {
        return variable;
    }


    /*
    *
    * funkcia, ktora vracia hodnotu objektu ako cele cislo
    *
    * */
    public int getIntValue() {
        return (int)getValue();
    }


    /*
    *
    * funkcia, ktora nastavuje hodnotu objektu
    *
    * */
    public void setValue(Object variable) {
        this.variable = variable;
        if (listener != null) listener.onChange();
    }


    /*
    *
    * funkcia, ktora nastavuje ChangeListener na objekt
    *
    * */
    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }


    /*
    *
    * funkcia, ktorou sa zavola ChangeListener
    *
    * */
    public void callListener() { listener.onChange(); }


    /*
    *
    * definovanie rozhrania ChangeListener
    *
    * */
    public interface ChangeListener {
        //rozhranie obsahuje metodu onChange()
        void onChange();
    }
}


