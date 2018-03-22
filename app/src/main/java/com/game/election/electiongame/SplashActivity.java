package com.game.election.electiongame;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.game.election.electiongame.classes.TogetherFunctions;
import com.game.election.electiongame.service.ClickMusicService;

public class SplashActivity extends AppCompatActivity {

    private boolean isBound = false;
    private ClickMusicService clickMusicService;
    TogetherFunctions tf;
    private float startX, startY;//premenne, ktore oznacuju bod pri dotyku pouzivatela s obrazovkou

    /*
    *
    * funkcia onCreate sa zapne ako prva v tejto aktivite
    * nastavuje sa tu hlavne to, aby bola aktivita v mode "fullscreen"
    * potom sa zapne funkcia setLayout
    *
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        tf = new TogetherFunctions();
        setLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        doBindService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        doUnbindService();
    }
    /*
        *
        * kontroluje kazdy dotyk pouzivatela s obrazovkou, ak ide o kliknutie, ozve sa zvucka, ak nie, tak sa neozve
        *
        * */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                startX = ev.getX();
                startY = ev.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                float endX = ev.getX();
                float endY = ev.getY();
                tf.checkIfIsClick(getApplicationContext(), startX, endX, startY, endY);
                break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void setLayout() {
        (findViewById(R.id.constraintLayout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this,MainMenuActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        });
    }

    private ServiceConnection serviceConnection = new ServiceConnection(){
        public void onServiceConnected(ComponentName name, IBinder binder) {
            clickMusicService = new ClickMusicService();
        }
        public void onServiceDisconnected(ComponentName name) {
            clickMusicService = null;
        }
    };

    void doBindService() {
        bindService(new Intent(this, ClickMusicService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        isBound = true;

    }

    void doUnbindService() {
        if(isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }
}
