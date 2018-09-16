package com.vilic.bojan.textbook;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private List<ChatGettersAndSetters> mChats;
    private List<String> mChattersIds;
    private String timestamp;
    private DatabaseReference mRootRef, mFriendDatabase, mMessageDatabase, mChatReference;
    private String uID, lastID;
    private int i = 0;
    private LinearLayoutManager mLayoutManager;
    private long number = 0;
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayList<String> lastTimestamps = new ArrayList<>();
    private ArrayList<String> unixTimestamps = new ArrayList<>();

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uID = firebaseUser.getUid();

        ids.clear();
        lastTimestamps.clear();
        unixTimestamps.clear();

        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uID).child("messages");
        mChatReference = FirebaseDatabase.getInstance().getReference().child("Chats").child(uID);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        drawView();

        return view;
    }

    public void drawView(){
        Query conversationQuery = mChatReference.orderByChild("timestamp");

        FirebaseRecyclerAdapter<ChatFragmentModel, ConversationViewHolder> recyclerAdapter = new FirebaseRecyclerAdapter<ChatFragmentModel, ConversationViewHolder>(
                ChatFragmentModel.class,
                R.layout.chat_fragment_single,
                ConversationViewHolder.class,
                conversationQuery
        ) {
            @Override
            protected void populateViewHolder(final ConversationViewHolder viewHolder, final ChatFragmentModel model, int position) {

                final String userKey = getRef(position).getKey();

                mFriendDatabase.child(userKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String display_name = dataSnapshot.child("display_name").getValue().toString();
                        String image = dataSnapshot.child("image").getValue().toString();
                        viewHolder.setDisplayName(display_name);
                        viewHolder.setImage(image);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                mFriendDatabase.child(userKey).child("messages").child(uID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String lastMessage = ds.child("message").getValue().toString();
                            if(lastMessage.contains("https://firebasestorage.googleapis.com")){
                                lastMessage = "Image received";
                            }
                            String timestamp = ds.child("timestamp").getValue().toString();
                            String timestampFix = timestamp.substring(0, timestamp.length() - 12);
                            viewHolder.setMessage(lastMessage);
                            viewHolder.setTimestamp(timestampFix);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

                        dialog.setTitle("Delete chat");
                        dialog.setMessage("Are you sure you want to delete this chat?");
                        dialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mChatReference.child(userKey).removeValue();
                                mMessageDatabase.child(userKey).removeValue();
                            }
                        });
                        dialog.show();

                        return false;
                    }
                });

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), ChatActivity.class);
                        i.putExtra("userId", userKey);
                        startActivity(i);
                    }
                });

                number++;
            }
        };
        mRecyclerView.setAdapter(recyclerAdapter);
    }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder {

        private TextView mDisplayName, mLastText, mTimestampText;
        private CircleImageView mProfileImage;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);

            mDisplayName = itemView.findViewById(R.id.displayName);
            mLastText = itemView.findViewById(R.id.lastText);
            mTimestampText = itemView.findViewById(R.id.timestampText);
            mProfileImage = itemView.findViewById(R.id.profileImage);
        }

        public void setMessage(String text){
            mLastText.setText(text);
        }

        public void setDisplayName(String name){
            mDisplayName.setText(name);
        }

        public void setTimestamp(String timestamp){
            mTimestampText.setText(timestamp);
        }

        public void setImage(String image){
            Picasso.get().load(image).into(mProfileImage);
        }
    }
}
