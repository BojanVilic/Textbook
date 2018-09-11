package com.vilic.bojan.textbook;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExploreFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabaseReference, mDatabaseRequests;
    private EditText searchBox;
    private TextView mResultNumber;
    private Button searchButton;
    private String uID;
    private long count = 0;
    private int notfound = 0;
    private LinearLayout layout;

    public ExploreFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.exploreRecyclerView);
        mResultNumber = view.findViewById(R.id.results);
        searchBox = (EditText) view.findViewById(R.id.searchBox);
        layout = view.findViewById(R.id.header);
        searchButton = (Button) view.findViewById(R.id.searchButton);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseRequests = FirebaseDatabase.getInstance().getReference().child("Friend_requests");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uID = user.getUid();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchBox.getText().toString();
                if(TextUtils.isEmpty(searchText)){
                    Toast.makeText(getActivity(), "Search can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    notfound = 0;
                    searchPeople(searchText);
                }
            }
        });

        return view;
    }

    private void searchPeople(String searchText){

        final Query searchQuery = mDatabaseReference.orderByChild("display_name").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<ExploreGettersAndSetters, ExploreViewHolder> adapter = new FirebaseRecyclerAdapter<ExploreGettersAndSetters, ExploreViewHolder>(
                ExploreGettersAndSetters.class,
                R.layout.explore_single_layout,
                ExploreViewHolder.class,
                searchQuery
        ) {
            @Override
            protected void populateViewHolder(final ExploreViewHolder viewHolder, final ExploreGettersAndSetters model, final int position) {
                final String userKey = getRef(position).getKey();
                
                mDatabaseReference.child(userKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String showOnExplore = dataSnapshot.child("show_on_explore").getValue().toString();
                        if(showOnExplore.equals("true")){
                            viewHolder.mView.setVisibility(View.VISIBLE);

                            viewHolder.setName(model.getDisplay_name());
                            viewHolder.setUsername(model.getUsername());
                            viewHolder.setImage(model.getImage());
                        } else {
                            viewHolder.layout.setVisibility(View.GONE);
                            notfound--;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                searchQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        count = dataSnapshot.getChildrenCount() + notfound;

                        layout.setVisibility(View.VISIBLE);

                        String rslt = "We found " + count + " result for your search";
                        mResultNumber.setText(rslt);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                viewHolder.mDetailsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), DetailsActivity.class);
                        i.putExtra("userId", userKey);
                        startActivity(i);
                    }
                });
            }
        };
        mRecyclerView.setAdapter(adapter);
    }

    public static class ExploreViewHolder extends RecyclerView.ViewHolder {

        View mView;
        private Button mDetailsButton;
        private ConstraintLayout layout;

        public ExploreViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            mDetailsButton = mView.findViewById(R.id.detailsButton);
            layout = (ConstraintLayout) mView.findViewById(R.id.layout);
        }

        public void setName(String display_name) {
            TextView mDisplayName = (TextView) mView.findViewById(R.id.singleDisplayName);
            mDisplayName.setText(display_name);
        }

        public void setUsername(String username) {
            TextView mUsername = (TextView) mView.findViewById(R.id.singleUsername);
            mUsername.setText(username);
        }

        public void setImage(String image) {
            ImageView mImage = (ImageView) mView.findViewById(R.id.singleImageView);
            Picasso.get().load(image).into(mImage);
        }
    }
}
