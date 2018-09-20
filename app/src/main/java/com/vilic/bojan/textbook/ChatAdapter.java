package com.vilic.bojan.textbook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private static final int CHAT_END = 1;
    private static final int CHAT_START = 2;

    private DatabaseReference databaseReference, referenceForChatterImage;
    private StorageReference storageReference;
    private String currentUserId, username, profileImage, imageUrl, chatterId;
    private int finding = 0;
    private String imgUrl;
    private Context ctx;

    private List<ChatGettersAndSetters> mDataSet;
    private String timestamp;

    public ChatAdapter(List<ChatGettersAndSetters> dataSet, String ts, String url, Context context, String chtrId) {
        chatterId = chtrId;
        ctx = context;
        imgUrl = url;
        mDataSet = dataSet;
        timestamp = ts;

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        referenceForChatterImage = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference().child(currentUserId);
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
        final ChatGettersAndSetters chat = mDataSet.get(position);

        holder.setIsRecyclable(false);

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
        if(!mDataSet.get(position).getMessage().contains("https://firebasestorage.googleapis.com")){
            holder.mMessage.setText(chat.getMessage());
        }
        else {
            holder.sendImage(mDataSet.get(position).getMessage());
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ctx, FullscreenImage.class);
                    i.putExtra("ImageUrl", mDataSet.get(position).getMessage());
                    ctx.startActivity(i);
                }
            });
        }

        holder.mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
                dialog.setTitle("Delete image");
                dialog.setMessage("Are you sure you want to delete this image?");
                dialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String test = mDataSet.get(position).getMessage();
                        databaseReference.child("messages").child(chatterId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren()){
                                    if(ds.child("message").getValue().toString().equals(test)){
                                        databaseReference.child("messages").child(chatterId).child(ds.getKey()).removeValue();
                                        Intent intent = new Intent("Added_something");
                                        intent.putExtra("position", String.valueOf(holder.getAdapterPosition()));
                                        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                dialog.show();
                return true;
            }
        });

        holder.mMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
                dialog.setTitle("Delete message");
                dialog.setMessage("Are you sure you want to delete this message?");
                dialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String test = mDataSet.get(holder.getAdapterPosition()).getMessage();
                        databaseReference.child("messages").child(chatterId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren()){
                                    if(ds.child("message").getValue().toString().equals(test)){
                                        databaseReference.child("messages").child(chatterId).child(ds.getKey()).removeValue();
                                        Intent intent = new Intent("Added_something");
                                        intent.putExtra("position", String.valueOf(holder.getAdapterPosition()));
                                        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                dialog.show();
                return true;
            }
        });

        holder.mMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.mTimestamp.getText().toString().matches("")){
                    String timestamp = chat.getTimestamp();
                    holder.mTimestamp.setText(timestamp);
                    holder.mTimestamp.animate().alpha(1).setDuration(300).start();
                }
                else {
                    holder.mTimestamp.animate().alpha(1).setDuration(300).start();
                    holder.mTimestamp.setText("");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mMessage, mTimestamp;
        private CircleImageView mProfileImage;
        private ImageView mImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mProfileImage = itemView.findViewById(R.id.profileImage);
            mMessage = itemView.findViewById(R.id.textMessage);
            mTimestamp = itemView.findViewById(R.id.timestampText);
            mImageView = itemView.findViewById(R.id.imageView);
        }

        public void setImage(String image){
            Picasso.get().load(image).into(mProfileImage);
        }

        public void sendImage(String image){
            Picasso.get().load(image).into(mImageView);
        }
    }
}
