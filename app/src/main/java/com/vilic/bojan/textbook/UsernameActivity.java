package com.vilic.bojan.textbook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dx.dxloadingbutton.lib.LoadingButton;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.github.ybq.android.spinkit.style.Pulse;
import com.github.ybq.android.spinkit.style.RotatingPlane;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UsernameActivity extends AppCompatActivity {

    private Button mCheckButton;
    private ProgressBar mProgressBar;
    private EditText mUsernameEditText;
    private String uid;
    private DatabaseReference mDatabaseRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        final FirebaseUser curretUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = curretUser.getUid();
        mDatabaseRoot = FirebaseDatabase.getInstance().getReference();

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mCheckButton = (Button) findViewById(R.id.checkButton);
        mUsernameEditText = (EditText) findViewById(R.id.usernameEditText);
        Pulse pulse = new Pulse();
        mProgressBar.setIndeterminateDrawable(pulse);
        mCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Check if that username is already in use

                if(mUsernameEditText.length() < 4){
                    Toast.makeText(UsernameActivity.this, "Username must have at least 4 characters", Toast.LENGTH_SHORT).show();
                }
                else if(mUsernameEditText.length() > 20){
                    Toast.makeText(UsernameActivity.this, "Username must not contain more than 20 characters", Toast.LENGTH_SHORT).show();
                } else {

                    mCheckButton.animate().alpha(0f).setDuration(500).start();
                    mProgressBar.animate().alpha(1f).setDuration(500).start();
                    mDatabaseRoot.child("Usernames").child(curretUser.getPhoneNumber()).setValue(mUsernameEditText.getText().toString());
                    mDatabaseRoot.child("Users").child(uid).child("username").setValue(mUsernameEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mCheckButton.animate().alpha(1f).setDuration(500).start();
                                mProgressBar.animate().alpha(0f).setDuration(500).start();
                                Intent i = new Intent(UsernameActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }
}
