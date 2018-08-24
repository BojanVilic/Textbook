package com.vilic.bojan.textbook;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ExploreFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabaseReference;
    public ExploreFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.exploreRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        /*0+7FirebaseRecyclerAdapter<ExploreGettersAndSetters, ExploreViewHolder> adapterOdd = new FirebaseRecyclerAdapter<ExploreGettersAndSetters, ExploreViewHolder>(
                ExploreGettersAndSetters.class,
                R.layout.explore_single_flipped_layout,
                ExploreViewHolder.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(ExploreViewHolder viewHolder, ExploreGettersAndSetters model, int position) {
                viewHolder.setName(model.getDisplay_name());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setImage(model.getImage());
            }
        };*/

        FirebaseRecyclerAdapter<ExploreGettersAndSetters, ExploreViewHolder> adapter = new FirebaseRecyclerAdapter<ExploreGettersAndSetters, ExploreViewHolder>(
                ExploreGettersAndSetters.class,
                R.layout.explore_single_layout,
                ExploreViewHolder.class,
                mDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(ExploreViewHolder viewHolder, ExploreGettersAndSetters model, int position) {
                Log.i("POSITION", String.valueOf(position));
                viewHolder.setName(model.getDisplay_name());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setImage(model.getImage());
            }
        };

        mRecyclerView.setAdapter(adapter);
    }

    public static class ExploreViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ExploreViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
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
