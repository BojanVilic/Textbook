package com.vilic.bojan.textbook;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private TextInputLayout displayName;
    private TextInputEditText editText;
    private TextInputLayout userName;
    private TextInputEditText usernameEditText;
    private TextView numberText;
    private Switch mExploreSwitch, mMessageSwitch, mRequestSwitch;
    private StorageReference mStorageReference;
    private Button mSaveButton;
    private CircleImageView mChangeImage, mProfileImage;
    private static final int GALLERY_CODE = 1;
    private String uid, number;
    private DatabaseReference mUsernameCheck;
    private View view;
    private DatabaseReference mDatabaseRoot;
    private DatabaseReference mDatabaseRootRoot;
    private ProgressDialog pD;
    private boolean pass = true;
    private Uri mainUri;
    private String display_name;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_settings, container, false);
        displayName = (TextInputLayout) view.findViewById(R.id.usersName);
        editText = (TextInputEditText) view.findViewById(R.id.displayNameEditText);
        userName = (TextInputLayout) view.findViewById(R.id.username);
        usernameEditText = (TextInputEditText) view.findViewById(R.id.usernameEditText);
        mExploreSwitch = (Switch) view.findViewById(R.id.exploreSwitch);
        mMessageSwitch = (Switch) view.findViewById(R.id.messageNotificationSwitch);
        mRequestSwitch = (Switch) view.findViewById(R.id.requestsNotificationSwitch);
        mSaveButton = (Button) view.findViewById(R.id.saveButton);
        numberText = (TextView) view.findViewById(R.id.numberText);
        mChangeImage = (CircleImageView) view.findViewById(R.id.changeImage);
        mProfileImage = (CircleImageView) view.findViewById(R.id.profileImage);
        mExploreSwitch = (Switch) view.findViewById(R.id.exploreSwitch);
        mMessageSwitch = (Switch) view.findViewById(R.id.messageNotificationSwitch);
        mRequestSwitch = (Switch) view.findViewById(R.id.requestsNotificationSwitch);

        pD = new ProgressDialog(getActivity());
        pD.setMessage("Loading...");
        pD.setCanceledOnTouchOutside(false);
        pD.setCancelable(false);
        pD.show();

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        number = current_user.getPhoneNumber();
        uid = current_user.getUid();

        numberText.setText(number);

        mDatabaseRoot = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mDatabaseRootRoot = FirebaseDatabase.getInstance().getReference();
        mUsernameCheck = FirebaseDatabase.getInstance().getReference().child("Usernames");

        mStorageReference = FirebaseStorage.getInstance().getReference();

        mDatabaseRoot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                display_name = dataSnapshot.child("display_name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String request_notification = dataSnapshot.child("request_notification").getValue().toString();
                String message_notification = dataSnapshot.child("message_notification").getValue().toString();
                String show_on_explore = dataSnapshot.child("show_on_explore").getValue().toString();
                String username = dataSnapshot.child("username").getValue().toString();

                usernameEditText.setText(username);

                if(request_notification.equals("true")) {
                    mRequestSwitch.setChecked(true);
                } else {
                    mRequestSwitch.setChecked(false);
                }

                if(message_notification.equals("true")) {
                    mMessageSwitch.setChecked(true);
                } else {
                    mMessageSwitch.setChecked(false);
                }

                if(show_on_explore.equals("true")) {
                    mExploreSwitch.setChecked(true);
                } else {
                    mExploreSwitch.setChecked(false);
                }

                editText.setText(display_name);
                Picasso.get().load(image).into(mProfileImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        pD.dismiss();
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

                mExploreSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b) {
                            mDatabaseRoot.child("show_on_explore").setValue("true");
                            Toast.makeText(getActivity(), "Preference saved", Toast.LENGTH_SHORT).show();
                        } else {
                            mDatabaseRoot.child("show_on_explore").setValue("false");
                            Toast.makeText(getActivity(), "Preference saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                mMessageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b) {
                            mDatabaseRoot.child("message_notification").setValue("true");
                            Toast.makeText(getActivity(), "Preference saved", Toast.LENGTH_SHORT).show();
                        } else {
                            mDatabaseRoot.child("message_notification").setValue("false");
                            Toast.makeText(getActivity(), "Preference saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                mRequestSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b) {
                            mDatabaseRoot.child("request_notification").setValue("true");
                            Toast.makeText(getActivity(), "Preference saved", Toast.LENGTH_SHORT).show();
                        } else {
                            mDatabaseRoot.child("request_notification").setValue("false");
                            Toast.makeText(getActivity(), "Preference saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    displayName.setHint("Display Name");
                }
            }
        });
        usernameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    userName.setHint("Username");
                }
            }
        });
        editText.addTextChangedListener(textWatcher);
        usernameEditText.addTextChangedListener(textWatcher);

        mChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Select Image"), GALLERY_CODE);
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String displayNameCheck = editText.getText().toString();
                if(!displayNameCheck.equals(display_name)){
                    mDatabaseRootRoot.child("Users").child(uid).child("display_name").setValue(editText.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Your profile's been updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                final String desiredUsername = usernameEditText.getText().toString();

                if(usernameEditText.length() < 4){
                    Toast.makeText(getActivity(), "Username must have at least 4 characters", Toast.LENGTH_SHORT).show();
                }
                else if(usernameEditText.length() > 20){
                    Toast.makeText(getActivity(), "Username must not contain more than 20 characters", Toast.LENGTH_SHORT).show();
                }
                else {
                    mUsernameCheck.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot data: dataSnapshot.getChildren()){
                                String currentUsername = data.getValue().toString();
                                if(desiredUsername.equals(currentUsername)){
                                    pass = false;
                                    break;
                                } else {
                                    pass = true;
                                }
                            }
                            updateProfile(pass);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i("TAG", "There's no username like this.");
                        }
                    });
                }
            }
        });

        return view;
    }

    void updateProfile(boolean pass){
        if(pass) {
            mDatabaseRootRoot.child("Usernames").child(number).setValue(usernameEditText.getText().toString());
            mDatabaseRootRoot.child("Users").child(uid).child("username").setValue(usernameEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Your profile's been updated", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "That username is already in use", Toast.LENGTH_SHORT).show();
        }
    }

    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            mSaveButton.animate().alpha(1f).setDuration(500).start();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            Uri imgUri = data.getData();
            CropImage.activity(imgUri).setAspectRatio(1, 1).start(getContext(), this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Uploading image...");
                progressDialog.show();
                Uri resultUri = result.getUri();
                final StorageReference filepath = mStorageReference.child("profile_images").child(uid + ".jpg");
                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                mainUri = uri;
                                mDatabaseRoot.child("image").setValue(mainUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Picasso.get().load(mainUri.toString()).into(mProfileImage, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    progressDialog.dismiss();
                                                }

                                                @Override
                                                public void onError(Exception e) {
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
