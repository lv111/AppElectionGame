package com.game.election.electiongame.classes;


public class Region {

    private int id, character, idProblem, week;
    private String regionName, abbreviation, lastAction, actionType;
    private double beforeProCharacter, beforeUndecided, beforeProOpponent,
            afterProCharacter, afterUndecided, afterProOpponent;
    private long population;
    private boolean regionOfAction;


    public Region(int id, String regionName, String abbreviation, long population,
                  int character, int idProblem, boolean regionOfAction, int week,
                  String lastAction, String actionType,
                  double beforeProCharacter, double beforeUndecided, double beforeProOpponent,
                  double afterProCharacter, double afterUndecided, double afterProOpponent) {
        this.id = id;
        this.regionName = regionName;
        this.abbreviation = abbreviation;
        this.population = population;
        this.character = character;
        this.idProblem = idProblem;
        this.regionOfAction = regionOfAction;
        this.week = week;
        this.lastAction = lastAction;
        this.actionType = actionType;
        this.beforeProCharacter = beforeProCharacter;
        this.beforeUndecided = beforeUndecided;
        this.beforeProOpponent = beforeProOpponent;
        this.afterProCharacter = afterProCharacter;
        this.afterUndecided = afterUndecided;
        this.afterProOpponent = afterProOpponent;
    }


    public int getId() {return id;}

    public String getRegionName() {return regionName;}

    public String getAbbreviation() {return abbreviation;}

    public long getPopulation() {return population;}

    public int getCharacter() { return character; }

    public int getIdProblem() { return idProblem; }

    public boolean getRegionOfAction() { return regionOfAction; }

    public int getWeek() {return week; }

    public String getLastAction() { return lastAction; }

    public String getActionType() { return actionType; }

    public double getBeforeProCharacter() { return beforeProCharacter; }

    public double getBeforeUndecided() { return beforeUndecided; }

    public double getBeforeProOpponent() { return beforeProOpponent; }

    public double getAfterProCharacter() { return afterProCharacter; }

    public double getAfterUndecided() { return afterUndecided; }

    public double getAfterProOpponent() { return afterProOpponent; }




    public void setCharacter (int character) { this.character = character; }

    public void setIdProblem (int id_problem) { this.idProblem = id_problem; }

    public void setRegionOfAction (boolean region_of_action) {this.regionOfAction = region_of_action; }

    public void setWeek (int week) {this.week = week;}

    public void setLastAction (String last_action) {this.lastAction = last_action;}

    public void setActionType (String action_type) { this.actionType = action_type;}

    public void setBeforeProCharacter (double before_pro_character) {this.beforeProCharacter = before_pro_character;}

    public void setBeforeUndecided (double before_undecided) {this.beforeUndecided = before_undecided;}

    public void setBeforeProOpponent (double before_pro_opponent) {this.beforeProOpponent = before_pro_opponent;}

    public void setAfterProCharacter (double after_pro_character) {this.afterProCharacter = after_pro_character;}

    public void setAfterUndecided (double after_undecided) {this.afterUndecided = after_undecided;}

    public void setAfterProOpponent (double after_pro_opponent) {this.afterProOpponent = after_pro_opponent;}
}
