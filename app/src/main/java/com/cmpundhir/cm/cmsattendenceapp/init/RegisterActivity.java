package com.cmpundhir.cm.cmsattendenceapp.init;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;
    @BindView(R.id.e1)
    EditText e1;
    @BindView(R.id.e2)
    EditText e2;
    @BindView(R.id.e3)
    EditText e3;
    @BindView(R.id.s1)
    Spinner s1;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    List<String> courseList = new ArrayList<>();
    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference(Constants.STUDENT);
    String name,email,pass,course="";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences("prefs",MODE_PRIVATE);
        editor = preferences.edit();
        courseList.add("-----Select Course-----");
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity.this,android.R.layout.simple_list_item_1,courseList);
        s1.setAdapter(adapter);
        s1.setOnItemSelectedListener(this);

        DatabaseReference courseref = database.getReference(Constants.COURSES);
        progressBar.setVisibility(View.VISIBLE);
        courseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG,dataSnapshot.toString());
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    Log.d(TAG,d.toString());
                    courseList.add((String) d.getValue());
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Error in courses", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onSignUpCLicked(View view){
        email = e1.getText().toString();
        pass = e2.getText().toString();
        name = e3.getText().toString();
        firSignUp(name,email,pass,course);
    }

    private void firSignUp(final String name,final String email,final String pass,final String course){
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Student s = new Student(name,email,course);
                            myRef.child(user.getUid()).setValue(s);
                            editor.putString(Constants.NAME,name);
                            editor.putString(Constants.EMAIL,email);
                            editor.putString(Constants.COURSE,course);
                            editor.commit();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        progressBar.setVisibility(View.GONE);
                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(this, SelfyActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(i>0){
            course = courseList.get(i);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
