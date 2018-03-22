package com.game.election.electiongame.classes;


import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class Animations {

    /*
    *
    * trieda, ktora definuje animacie
    *
    * */


    /*
    *
    * metoda pre "zatrasenie" objektom zlava doprava
    * pouziva sa pre objekt meno a priezvisko kandidata v hornych rohoch obrazovky pri hre
    *
    * */
    public void shake(int duration, long delay, View view) {
        YoYo.with(Techniques.Shake)
                .duration(duration)
                .delay(delay)
                .playOn(view);
    }
}
