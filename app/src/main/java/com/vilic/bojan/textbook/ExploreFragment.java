package com.vilic.bojan.textbook;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabaseReference;
    //private FirebaseRecyclerOptions<ExploreGettersAndSetters> options;
    public ExploreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.exploreRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        /*options = new FirebaseRecyclerOptions.Builder<ExploreGettersAndSetters>().setQuery(mDatabaseReference, ExploreGettersAndSetters.class).build();

        FirebaseRecyclerAdapter<ExploreGettersAndSetters, ExploreViewHolder> adapter = new FirebaseRecyclerAdapter<ExploreGettersAndSetters, ExploreViewHolder>(options) {
            @NonNull
            @Override
            public ExploreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            protected void onBindViewHolder(@NonNull ExploreViewHolder holder, int position, @NonNull ExploreGettersAndSetters model) {

                holder.setName(model.getDisplay_name());
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

        }*/
    }
}
