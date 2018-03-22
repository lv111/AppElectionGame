package com.game.election.electiongame.classes;

import java.util.ArrayList;

public class ActionOfGame {

    /*
    *
    * trieda, ktora oznacuje akciu vykonanu pocas volebnej kampani
    *
    * */


    private int week,           //premenna, ktora oznacuje tyzden, kedy bola akcia vykonana
            character;          //premenna, ktora oznacuje, kto akciu vykonal, ak je 1, akciu vykonal pouzivatel, inak oponent
    private String actionType;  //premenna, ktora oznacuje typ akcie, moze nadobudat hodnotu "investment" alebo "interview"
    private ArrayList<Region> regionsOfGames;   //zoznam regionov, ktorych sa vykonanie akcie nejako dotklo, cize akcia bola vykonana v tomto regione alebo v susednom


    //konstruktor
    public ActionOfGame(int week, int character, String actionType, ArrayList<Region> regionsOfGames) {
        this.week = week;
        this.character = character;
        this.actionType = actionType;
        this.regionsOfGames = regionsOfGames;
    }


    /*
    *
    * funkcia, ktora vracia hodnotu tyzdna, resp. premennu week
    *
    * */
    public int getWeek() {
        return week;
    }


    /*
    *
    * funkcia, ktora vracia hodnotu urcujucu kto akciu vykonal, resp. premennu character
    *
    * */
    public int getCharacter() {
        return character;
    }


    /*
    *
    * funkcia, ktora vracia typ akcie, resp. premennu actionType
    *
    * */
    public String getActionType() {
        return actionType;
    }


    /*
    *
    * funkcia, ktora vracia zoznam regionov, resp. premennu regionsOfGames
    *
    * */
    public ArrayList<Region> getRegionsOfGames() { return regionsOfGames; }
}
