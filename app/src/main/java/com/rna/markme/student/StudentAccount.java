package com.rna.markme.student;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rna.markme.User;
import com.rna.markme.R;
import com.rna.markme.teacher.TeacherAccount;

public class StudentAccount extends AppCompatActivity {

    TextView studentID;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_account);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        studentID=(TextView)findViewById(R.id.textView);
        studentID.setText(email.substring(0, email.length() - 10).toUpperCase());
        //studentID.setText(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID));
        mAuth= FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    //Do anything here which needs to be done after signout is complete
                    startActivity(new Intent(StudentAccount.this, User.class));
                    finish();
                }
                else {
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();

    }

    public void signOut(View view){

        mAuth.signOut();


    }
    public void markYourAttendance(View view){
        startActivity(new Intent(StudentAccount.this,MarkAttendance.class));
//        Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//        startActivity(myIntent);
//        if(getApplicationContext().getPackageManager().hasSystemFeature("android.hardware.wifi.rtt"))
//
//        else
//            Toast.makeText(StudentAccount.this,"RTT not supported",Toast.LENGTH_LONG).show();

    }
}
