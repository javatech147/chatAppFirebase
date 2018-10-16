package com.waytojava.ichatapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.waytojava.ichatapp.R;
import com.waytojava.ichatapp.sharedpreferences.MyPreferences;

public class SplashActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = this;


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MyPreferences myPreferences = new MyPreferences(context);
                boolean isUserAccessHomePage = myPreferences.getBoolean(MyPreferences.IS_USER_ACCESS_HOME_PAGE);
                if (isUserAccessHomePage) {
                    startActivity(new Intent(context, MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(context, LoginActivity.class));
                    finish();
                }
            }
        }, 2000);
    }
}
