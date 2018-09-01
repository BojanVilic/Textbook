package com.vilic.bojan.textbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Button mPlusButton, mSendButton;
    private EditText mTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mRecyclerView = findViewById(R.id.recyclerView);
        mPlusButton = findViewById(R.id.plusImage);
        mSendButton = findViewById(R.id.sendImage);
        mTextMessage = findViewById(R.id.textInput);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mTextMessage.getText().toString();
                if(!TextUtils.isEmpty(message)){
                    
                }
            }
        });
    }
}
