package com.example.cris.easytourbrasil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

public class SplashActivity extends AppCompatActivity {
    //Constante de duração do tempo de apresentação.
    private final static int TIME_SPLASH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView( R.layout.activity_splash);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                //Código rodará depois do tempo definido.
                Intent dashboard = new Intent (SplashActivity.this, LoginActivity.class);
                startActivity(dashboard);
                finish();
            }
        },TIME_SPLASH);
    }
}
