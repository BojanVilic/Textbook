package com.vilic.bojan.textbook;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsFragment extends Fragment {

    private String dispalyName, username, profileImage, uId;
    private DatabaseReference mFriendsDatabase, mRootRef;
    private RecyclerView mRecyclerView;

    public FriendsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uId = firebaseUser.getUid();

        mRootRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uId).child("friends");

        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query conversationQuery = mFriendsDatabase;

        FirebaseRecyclerAdapter<ConversationGettersAndSetters, FriendsFragment.ConversationViewHolder> recyclerAdapter = new FirebaseRecyclerAdapter<ConversationGettersAndSetters, FriendsFragment.ConversationViewHolder>(
                ConversationGettersAndSetters.class,
                R.layout.chat_fragment_single,
                FriendsFragment.ConversationViewHolder.class,
                conversationQuery
        ) {
            @Override
            protected void populateViewHolder(final FriendsFragment.ConversationViewHolder viewHolder, final ConversationGettersAndSetters model, int position) {
                final String userKey = getRef(position).getKey();

                mRootRef.child(userKey).addValueEventListener(new ValueEventListener() {
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

                mRootRef.child(userKey).child("friends").child(uId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String friendsSince = dataSnapshot.child("friends_since").getValue().toString();
                        viewHolder.mLastText.setText("Friends since: " + friendsSince);
                        viewHolder.mTimestampText.setText("");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), DetailsActivity.class);
                        i.putExtra("userId", userKey);
                        startActivity(i);
                    }
                });
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
