package com.cmpundhir.cm.cmsattendenceapp.init;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.cmpundhir.cm.cmsattendenceapp.MainActivity;
import com.cmpundhir.cm.cmsattendenceapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        Intent intent = null;
        if(currentUser==null){
            intent = new Intent(this,LoginActivity.class);
        }else{
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
