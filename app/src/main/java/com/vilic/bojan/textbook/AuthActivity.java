package com.vilic.bojan.textbook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

import static java.security.AccessController.getContext;

public class AuthActivity extends AppCompatActivity {

    private CountryCodePicker mCodePicker;
    private EditText mNumber, mCountryCode, mVerificationCode;
    private Button mVerifyButton;
    private String mPhoneNumber;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog progressDialog;
    int btnType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();

        mCodePicker = (CountryCodePicker) findViewById(R.id.ccp);
        mNumber = (EditText) findViewById(R.id.number);
        mCountryCode = (EditText) findViewById(R.id.countryCode);
        mVerifyButton = (Button) findViewById(R.id.verifyButton);
        mVerificationCode = (EditText) findViewById(R.id.code) ;

        progressDialog = new ProgressDialog(this);

        mCodePicker.getSelectedCountryCode();
        mCountryCode.setText("+ " + mCodePicker.getSelectedCountryCode());

        mCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                mCountryCode.setText("+ " + mCodePicker.getSelectedCountryCode());
                mCountryCode.setVisibility(View.VISIBLE);
                }
        });

        mVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnType == 0) {

                    progressDialog.setMessage("Verifying, please wait.");
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    btnType = 1;

                    mVerifyButton.setEnabled(false);
                    mPhoneNumber = "+" + mCodePicker.getSelectedCountryCode() + mNumber.getText().toString();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(mPhoneNumber, 60, TimeUnit.SECONDS, AuthActivity.this, mCallbacks);
                }
                else {
                    mVerifyButton.setEnabled(false);

                    String verificationCode = mVerificationCode.getText().toString();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(AuthActivity.this, "There was some error. Please try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                mVerificationId = s;
                mResendToken = forceResendingToken;

                mCountryCode.setVisibility(View.INVISIBLE);
                mNumber.setVisibility(View.INVISIBLE);
                mVerificationCode.setVisibility(View.VISIBLE);
                mVerificationCode.animate().translationY(-70).start();
                mVerifyButton.setEnabled(true);
                mVerifyButton.setText("Verify code");
                if(progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = task.getResult().getUser();
                    Intent mainIntent = new Intent(AuthActivity.this, IntroProfileSetup.class);
                    startActivity(mainIntent);
                    finish();
                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    }
                }
            }
        });
    }
}
