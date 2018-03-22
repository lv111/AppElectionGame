package com.game.election.electiongame.classes;


public class SavedGame {

    private int id;
    private String stateName, characterNameSurname, opponentNameSurname;
    private double proCharacter, undecided, proOpponent;
    private long changed;

    public SavedGame(int id, String stateName, String characterNameSurname,
                     String opponentNameSurname, double proCharacter,
                     double undecided, double proOpponent, long changed) {
        this.id = id;
        this.stateName = stateName;
        this.characterNameSurname = characterNameSurname;
        this.opponentNameSurname = opponentNameSurname;
        this.proCharacter = proCharacter;
        this.undecided = undecided;
        this.proOpponent = proOpponent;
        this.changed = changed;
    }

    public int getId() { return id; }

    public String getStateName() { return stateName; }

    public String getCharacterNameSurname() { return characterNameSurname; }

    public String getOpponentNameSurname() { return opponentNameSurname; }

    public double getProCharacter() { return proCharacter; }

    public double getUndecided() { return undecided; }

    public double getProOpponent() { return proOpponent; }

    public long getChanged() { return changed; }

}
