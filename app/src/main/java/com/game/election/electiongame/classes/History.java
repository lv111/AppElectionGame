package com.game.election.electiongame.classes;


public class History {

    /*
    *
    * trieda, ktora oznacuje jeden tyzden v ramci volieb
    *
    * */


    private int week;                   //tyzden, ktoreho sa tato trieda tyka
    private String firstNameAndSurname, //meno a priezvisko kandidata, ktory bol prvy na rade
            secondNameAndSurname,       //meno a priezvisko kandidata, ktory bol druhy na rade
            firstLastAction,            //akcia, ktoru vykonal kandidat, ktory bol prvy na rade
            secondLastAction;           //akcia, ktoru vykonal kandidat, ktory bol druhy na rade
    private boolean firstOnStep;        //oznacuje, ci bol prvy na rade pouzivatel alebo oponent
    private double firstProCandidate,   //pocet ludi, ktori boli po vykonani prvej akcie v tyzdni rozhodnuti volit kandidata
            firstProOpponent,           //pocet ludi, ktori boli po vykonani prvej akcie v tyzdni rozhodnuti volit oponenta
            secondProCandidate,         //pocet ludi, ktori boli po vykonani druhej akcie v tyzdni rozhodnuti volit kandidata
            secondProOpponent;          //pocet ludi, ktori boli po vykonani druhej akcie v tyzdni rozhodnuti volit oponenta
    private long population;            //celkova populacia statu


    //konstruktor
    public History(int week, String firstNameAndSurname, String secondNameAndSurname, String firstLastAction, String secondLastAction, boolean firstOnStep, double firstProCandidate, double firstProOpponent, double secondProCandidate, double secondProOpponent, long population) {
        this.week = week;
        this.firstNameAndSurname = firstNameAndSurname;
        this.secondNameAndSurname = secondNameAndSurname;
        this.firstLastAction = firstLastAction;
        this.secondLastAction = secondLastAction;
        this.firstOnStep = firstOnStep;
        this.firstProCandidate = firstProCandidate;
        this.firstProOpponent = firstProOpponent;
        this.secondProCandidate = secondProCandidate;
        this.secondProOpponent = secondProOpponent;
        this.population = population;
    }


    /*
    *
    * metoda pre vratenie tyzdna
    *
    * */
    public int getWeek() {
        return week;
    }


    /*
    *
    * metoda pre vratenie mena a priezviska kandidata, ktory bol prvy na rade
    *
    * */
    public String getFirstNameAndSurname() {
        return firstNameAndSurname;
    }


    /*
    *
    * metoda pre vratenie mena a priezviska kandidata, ktory bol druhy na rade
    *
    * */
    public String getSecondNameAndSurname() {
        return secondNameAndSurname;
    }


    /*
    *
    * metoda pre vratenie akcie, ktoru vykonal ten kandidat, ktory bol prvy na rade
    *
    * */
    public String getFirstLastAction() {
        return firstLastAction;
    }


    /*
    *
    * metoda pre vratenie akcie, ktoru vykonal ten kandidat, ktory bol druhy na rade
    *
    * */
    public String getSecondLastAction() {
        return secondLastAction;
    }


    /*
    *
    * metoda pre vratenie, kto bol prvy na rade, ci kandidat alebo oponent
    *
    * */
    public boolean isFirstOnStep() {
        return firstOnStep;
    }


    /*
    *
    * metoda pre vratenie poctu ludi, ktori boli po vykonani prvej akcie rozhodnuti volit kandidata
    *
    * */
    public double getFirstProCandidate() {
        return firstProCandidate;
    }


    /*
    *
    * metoda pre vratenie poctu ludi, ktori boli po vykonani prvej akcie rozhodnuti volit oponenta
    *
    * */
    public double getFirstProOpponent() {
        return firstProOpponent;
    }


    /*
    *
    * metoda pre vratenie poctu ludi, ktori boli po vykonani druhej akcie rozhodnuti volit kandidata
    *
    * */
    public double getSecondProCandidate() {
        return secondProCandidate;
    }


    /*
    *
    * metoda pre vratenie poctu ludi, ktori boli po vykonani druhej akcie rozhodnuti volit oponenta
    *
    * */
    public double getSecondProOpponent() {
        return secondProOpponent;
    }


    /*
    *
    * metoda pre vratenie celkovej populacie statu
    *
    * */
    public long getPopulation() {
        return population;
    }
}
