package com.cmpundhir.cm.cmsattendenceapp.init;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cmpundhir.cm.cmsattendenceapp.MainActivity;
import com.cmpundhir.cm.cmsattendenceapp.R;
import com.cmpundhir.cm.cmsattendenceapp.model.Student;
import com.cmpundhir.cm.cmsattendenceapp.util.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    @BindView(R.id.e1)
    EditText e1;
    @BindView(R.id.e2)
    EditText e2;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(Constants.STUDENT);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        pref = getSharedPreferences("prefs",MODE_PRIVATE);
        editor = pref.edit();
        mAuth = FirebaseAuth.getInstance();
    }

    public void onLoginCLicked(View view){
        String email,pass;
        email = e1.getText().toString();
        pass = e2.getText().toString();
        firLogin(email,pass);

    }
    public void onSingupCLicked(View view){
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    private void firLogin(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if(user!=null) {
           getUserData(user);
        }
    }

    private void getUserData(FirebaseUser user){
        myRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Student s = dataSnapshot.getValue(Student.class);
                editor.putString(Constants.NAME,s.getName());
                editor.putString(Constants.EMAIL,s.getEmail());
                editor.putString(Constants.COURSE,s.getCourse());
                editor.commit();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Student details error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
