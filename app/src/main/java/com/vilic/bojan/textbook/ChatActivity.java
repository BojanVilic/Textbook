package com.vilic.bojan.textbook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Button mPlusButton, mSendButton;
    private EditText mTextMessage;
    private DatabaseReference mMessageReference, mRootRef, mMessageReferenceForFriend, mDatabaseRoot;
    private String currentUserId, chatterId;
    private List<ChatGettersAndSetters> mChats;
    private StorageReference mStorageReference;
    private ChatAdapter mAdapter;
    private String mId, url, ts;
    private static final int GALLERY_CODE = 1;
    private Uri mainUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mRecyclerView = findViewById(R.id.recyclerView);
        mPlusButton = findViewById(R.id.plusImage);
        mSendButton = findViewById(R.id.sendImage);
        mTextMessage = findViewById(R.id.textInput);
        mPlusButton = findViewById(R.id.plusImage);

        mChats = new ArrayList<>();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        chatterId = getIntent().getStringExtra("userId");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = firebaseUser.getUid();

        mDatabaseRoot = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        mPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Select Image"), GALLERY_CODE);
            }
        });

        mRootRef.child("Users").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                url = dataSnapshot.child("image").getValue().toString();
                mAdapter = new ChatAdapter(mChats, ts, url, ChatActivity.this);
                mRecyclerView.setAdapter(mAdapter);
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
                String pushid = mMessageReference.getKey();
                String anotherPushid= mMessageReferenceForFriend.getKey();
                String message = mTextMessage.getText().toString();
                if(!TextUtils.isEmpty(message)){
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy.");
                    mId = sdf.format(new Date());
                    mMessageReference.setValue(new ChatGettersAndSetters(message, mId, currentUserId));
                    mMessageReferenceForFriend.setValue(new ChatGettersAndSetters(message, mId, currentUserId));
                    mRootRef.child("Chats").child(currentUserId).child(chatterId).child("timestamp").setValue(ServerValue.TIMESTAMP);
                    mRootRef.child("Chats").child(chatterId).child(currentUserId).child("timestamp").setValue(ServerValue.TIMESTAMP);
                    mRootRef.child("Message_Notifications").child(currentUserId).push().child("receiver").setValue(chatterId);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            Uri resultUri = data.getData();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading image...");
            progressDialog.show();

            mMessageReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("messages").child(chatterId).push();
            mMessageReferenceForFriend = FirebaseDatabase.getInstance().getReference().child("Users").child(chatterId).child("messages").child(currentUserId).push();
            String pushid = mMessageReference.getKey();
            String anotherPushid= mMessageReferenceForFriend.getKey();

            final StorageReference filepath = mStorageReference.child("chats").child(currentUserId).child(chatterId).child(pushid + ".jpg");
            final StorageReference filepathFriend = mStorageReference.child("chats").child(chatterId).child(currentUserId).child(anotherPushid + ".jpg");
            filepathFriend.putFile(resultUri);
            filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            mainUri = uri;
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy.");
                            mId = sdf.format(new Date());
                            mMessageReference.setValue(new ChatGettersAndSetters(mainUri.toString(), mId, currentUserId));
                            mMessageReferenceForFriend.setValue(new ChatGettersAndSetters(mainUri.toString(), mId, currentUserId));
                            mRootRef.child("Chats").child(currentUserId).child(chatterId).child("timestamp").setValue(ServerValue.TIMESTAMP);
                            mRootRef.child("Chats").child(chatterId).child(currentUserId).child("timestamp").setValue(ServerValue.TIMESTAMP);
                            mRootRef.child("Message_Notifications").child(currentUserId).push().child("receiver").setValue(chatterId);
                        }
                    });
                }
            });
        }
    }
}
