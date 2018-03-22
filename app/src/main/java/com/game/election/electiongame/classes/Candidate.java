package com.game.election.electiongame.classes;


public class Candidate {

    /*
    *
    * trieda, ktora oznacuje kandidata, ktoreho si pouzivatel moze zvolit
    *
    * */


    private int id,             //identifikator kandidata
            idBirthRegion,      //identifikator rodneho regionu kandidata
            idBirthState,       //identifikator rodneho statu kandidata
            charisma,           //vlastnost charizma v intervale 0 az 100
            reputation,         //vlastnost reputacia v intervale 0 az 100
            money,              //vlastnost peniaze v intervale 0 az 100
            createdByUser;      //ak 1, tak kandidat bol vytvoreny pouzivatelom, ak 0, tak kandidat bol vytvoreny pri prvom otvoreni hry
    private String nameSurname, //meno a priezvisko kandidata
            birthRegionName,    //rodny region kandidata
            birthStateName;     //rodny stat kandidata
    private long changed;       //datum a cas, kedy bol kandidat naposledy aktualizovany


    //konstruktor
    public Candidate(int id, String nameSurname, int idBirthRegion, String birthRegionName, int idBirthState, String birthStateName, int charisma, int reputation, int money, int createdByUser, long changed) {
        this.id = id;
        this.nameSurname = nameSurname;
        this.idBirthRegion = idBirthRegion;
        this.birthRegionName = birthRegionName;
        this.idBirthState = idBirthState;
        this.birthStateName = birthStateName;
        this.charisma = charisma;
        this.reputation = reputation;
        this.money = money;
        this.createdByUser = createdByUser;
        this.changed = changed;
    }


    /*
    *
    * metoda pre vratenie identifikaotra kandidata
    *
    * */
    public int getId() {
        return id;
    }


    /*
    *
    * metoda pre vratenie mena a priezviska kandidata
    *
    * */
    public String getNameSurname() {
        return nameSurname;
    }


    /*
    *
    * metoda pre vratenie rodneho kraja kandidata
    *
    * */
    public String getBirthRegionName() {
        return birthRegionName;
    }


    /*
    *
    * metoda pre vratenie vlastnosti charizma
    *
    * */
    public int getCharisma() {
        return charisma;
    }


    /*
    *
    * metoda pre vratenie vlastnosti reputacia
    *
    * */
    public int getReputation() {
        return reputation;
    }


    /*
    *
    * metoda pre vratenie vlastnosti peniaze
    *
    * */
    public int getMoney() {
        return money;
    }


    /*
    *
    * metoda pre vratenie hodnoty, ci bol kandidat vytvoreny pouzivatelom alebo nie
    *
    * */
    public int getCreatedByUser() { return createdByUser; }

}
