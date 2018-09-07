package com.vilic.bojan.textbook;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private static final int CHAT_END = 1;
    private static final int CHAT_START = 2;

    private DatabaseReference databaseReference, referenceForChatterImage;
    private String currentUserId, username, profileImage, imageUrl;
    private int finding = 0;
    private String imgUrl;

    private List<ChatGettersAndSetters> mDataSet;
    private String timestamp;

    public ChatAdapter(List<ChatGettersAndSetters> dataSet, String ts, String url) {
        imgUrl = url;
        mDataSet = dataSet;
        timestamp = ts;

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = firebaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        referenceForChatterImage = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;

        if (viewType == CHAT_END) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_single_layout_end, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_single_layout, parent, false);
        }

        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataSet.get(position).getSender().equals(currentUserId)) {
            return CHAT_START;
        }
        return CHAT_END;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        ChatGettersAndSetters chat = mDataSet.get(position);

        if(finding == 0 && !mDataSet.get(position).getSender().equals(currentUserId)){
            username = mDataSet.get(position).getSender();
            finding = 1;
        }

        if(finding == 1) {
            referenceForChatterImage.child(username).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    imageUrl = dataSnapshot.child("image").getValue().toString();

                    if(mDataSet.get(position).getSender().equals(username)){
                        holder.setImage(imageUrl);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if(mDataSet.get(position).getSender().equals(currentUserId)) {
            holder.setImage(imgUrl);
        }
        holder.mMessage.setText(chat.getMessage());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mMessage;
        private CircleImageView mProfileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mProfileImage = itemView.findViewById(R.id.profileImage);
            mMessage = itemView.findViewById(R.id.textMessage);
        }

        public void setImage(String image){
            Picasso.get().load(image).into(mProfileImage);
        }
    }
}
