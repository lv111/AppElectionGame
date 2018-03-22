package com.game.election.electiongame.classes;

public class Problem {

    /*
    *
    * trieda, ktora oznacuje oblast investicie alebo temu interview
    *
    * */
//TODO: tu som skoncila s komentarmi
    private int id;             //identifikator problemu
    private double effect,             //hodnota
            solvability;
    int idRegion;
    private String problemName;
    String actionType;
    long changed;


    //konstruktor
    public Problem(int id, String problemName, int idRegion, String actionType, double effect, double solvability, long changed) {
        this.id = id;
        this.problemName = problemName;
        this.idRegion = idRegion;
        this.actionType = actionType;
        this.effect = effect;
        this.solvability = solvability;
        this.changed = changed;
    }

    public int getId() {
        return id;
    }

    public String getProblemName() {
        return problemName;
    }

    public double getEffect() {
        return effect;
    }

    public double getSolvability() {
        return solvability;
    }
}
