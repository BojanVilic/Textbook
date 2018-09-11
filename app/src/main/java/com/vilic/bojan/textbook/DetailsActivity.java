package com.vilic.bojan.textbook;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsActivity extends AppCompatActivity {

    private CircleImageView mProfileImage;
    private TextView mDisplayName, mUsername, mFriendsSince, mMessagesExchanged;
    private Button mAddButton, mTextButton;
    private DatabaseReference mDatabase, mDatabaseRequests, mRootDatabase, mNotificationRef;
    private String uID, id, req_type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mProfileImage = (CircleImageView) findViewById(R.id.profileImage);
        mDisplayName = (TextView) findViewById(R.id.displayName);
        mUsername = (TextView) findViewById(R.id.usernameText) ;
        mFriendsSince = (TextView) findViewById(R.id.friendsText);
        mMessagesExchanged = (TextView) findViewById(R.id.messagesExchanged);
        mAddButton = (Button) findViewById(R.id.addButton);
        mTextButton = (Button) findViewById(R.id.textButton);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uID = user.getUid();

        mDatabaseRequests = FirebaseDatabase.getInstance().getReference().child("Friend_requests");
        mNotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");

        id = getIntent().getStringExtra("userId");
        Log.i("TEST", id);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        mRootDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailsActivity.this, ChatActivity.class);
                i.putExtra("userId", id);
                startActivity(i);
            }
        });

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String displayName = dataSnapshot.child("display_name").getValue().toString();
                String username = dataSnapshot.child("username").getValue().toString();
                String profileImageUrl = dataSnapshot.child("image").getValue().toString();

                Picasso.get().load(profileImageUrl).into(mProfileImage);
                mDisplayName.setText(displayName);
                mUsername.setText(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseRequests.child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(id)){
                    String request_type = dataSnapshot.child(id).child("request_type").getValue().toString();
                    if(request_type.equals("sent")){
                        mAddButton.setBackgroundColor(Color.parseColor("#dddddd"));
                        mAddButton.setText("Cancel");
                        mAddButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.grayx, 0, 0, 0);
                        req_type = "sent";
                    }
                    else if(request_type.equals("received")){
                        mAddButton.setBackgroundColor(Color.parseColor("#6fed90"));
                        mAddButton.setText("Accept");
                        mAddButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.accept, 0, 0, 0);
                        req_type = "received";
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRootDatabase.child(uID).child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(id)){
                    req_type = "friends";
                    mAddButton.setText("Unfriend");
                    mAddButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    mAddButton.setBackgroundColor(Color.parseColor("#dddddd"));
                    mFriendsSince.setVisibility(View.VISIBLE);
                    mMessagesExchanged.setVisibility(View.VISIBLE);
                    String date = dataSnapshot.child(id).child("friends_since").getValue().toString();
                    mFriendsSince.setText("Friends since:\n" + date);
                }
                else {
                    mFriendsSince.setVisibility(View.INVISIBLE);
                    mMessagesExchanged.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRootDatabase.child(uID).child("messages").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String messagesCount = String.valueOf(dataSnapshot.getChildrenCount());
                mMessagesExchanged.setText("Messages exchanged:\n" + messagesCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(req_type.equals("")) {
                    mDatabaseRequests.child(uID).child(id).child("request_type").setValue("sent").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            HashMap<String, String> notificationData = new HashMap<>();
                            notificationData.put("from", uID);
                            notificationData.put("type", "request");

                            mNotificationRef.child(id).push().setValue(notificationData);
                            Toast.makeText(DetailsActivity.this, "Request sent", Toast.LENGTH_SHORT).show();
                            mDatabaseRequests.child(id).child(uID).child("request_type").setValue("received");
                            mAddButton.setBackgroundColor(Color.parseColor("#dddddd"));
                            mAddButton.setText("Cancel");
                            mAddButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.grayx, 0, 0, 0);
                        }
                    });
                }
                else if(req_type.equals("received")){
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
                    String currentDateandTime = sdf.format(new Date());
                    mDatabaseRequests.child(id).child(uID).removeValue();
                    mDatabaseRequests.child(uID).child(id).removeValue();
                    mRootDatabase.child(id).child("friends").child(uID).child("friends_since").setValue(currentDateandTime);
                    mRootDatabase.child(uID).child("friends").child(id).child("friends_since").setValue(currentDateandTime).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            req_type = "friends";
                            mAddButton.setText("Unfriend");
                            mAddButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            mAddButton.setBackgroundColor(Color.parseColor("#dddddd"));
                            Toast.makeText(DetailsActivity.this, "You are now friends", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else if(req_type.equals("sent")){
                    mDatabaseRequests.child(id).child(uID).removeValue();
                    mDatabaseRequests.child(uID).child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            req_type = "";
                            mAddButton.setText("Add");
                            mAddButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            mAddButton.setBackgroundColor(Color.parseColor("#5975db"));
                            Toast.makeText(DetailsActivity.this, "Friend request canceled", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else if(req_type.equals("friends")){
                    mRootDatabase.child(id).child("friends").child(uID).removeValue();
                    mRootDatabase.child(uID).child("friends").child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            req_type = "";
                            mAddButton.setText("Add");
                            mAddButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            mAddButton.setBackgroundColor(Color.parseColor("#5975db"));
                            Toast.makeText(DetailsActivity.this, "Friend deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
