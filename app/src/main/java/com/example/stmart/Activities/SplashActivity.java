package com.example.stmart.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.stmart.HomeActivity;
import com.example.stmart.LoginActivity;
import com.example.stmart.MainActivity;
import com.example.stmart.R;
import com.example.stmart.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user !=null){
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
                finish();
            }
        },2000);
    }
}