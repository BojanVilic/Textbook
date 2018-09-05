package com.vilic.bojan.textbook;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Button mPlusButton, mSendButton;
    private EditText mTextMessage;
    private DatabaseReference mMessageReference, mRootRef, mMessageReferenceForFriend;
    private String currentUserId, chatterId;
    private List<ChatGettersAndSetters> mChats;
    private ChatAdapter mAdapter;
    private String mId, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mRecyclerView = findViewById(R.id.recyclerView);
        mPlusButton = findViewById(R.id.plusImage);
        mSendButton = findViewById(R.id.sendImage);
        mTextMessage = findViewById(R.id.textInput);

        mChats = new ArrayList<>();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ChatAdapter(mChats, mId);
        mRecyclerView.setAdapter(mAdapter);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        chatterId = getIntent().getStringExtra("userId");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = firebaseUser.getUid();

        mRootRef.child("Users").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = dataSnapshot.child("username").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMessageReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("messages").child(chatterId).push();
                mMessageReferenceForFriend = FirebaseDatabase.getInstance().getReference().child("Users").child(chatterId).child("messages").child(currentUserId).push();
                String message = mTextMessage.getText().toString();
                if(!TextUtils.isEmpty(message)){
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy. ");
                    mId = sdf.format(new Date());
                    mMessageReference.setValue(new ChatGettersAndSetters(message, mId, currentUserId));
                    mMessageReferenceForFriend.setValue(new ChatGettersAndSetters(message, mId, currentUserId));
                    mTextMessage.setText("");
                }
            }
        });


        mRootRef.child("Users").child(currentUserId).child("messages").child(chatterId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue() != null) {
                    try {
                        ChatGettersAndSetters model = dataSnapshot.getValue(ChatGettersAndSetters.class);
                        mChats.add(model);

                        mRecyclerView.scrollToPosition(mChats.size() - 1);
                        mAdapter.notifyItemInserted(mChats.size() - 1);
                    } catch (Exception ex) {
                        Log.i("TAG", ex.getMessage());
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
