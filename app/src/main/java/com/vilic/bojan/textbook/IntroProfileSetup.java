package com.vilic.bojan.textbook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.futuremind.recyclerviewfastscroll.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class IntroProfileSetup extends AppCompatActivity {

    private CircleImageView mProfileImage;
    private EditText mUsersName;
    private Button mNextButton;
    private TextView mAppearingDescription;
    private static final int GALLERY_CODE = 1;
    private String uid;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseRoot;
    private Uri mainUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_profile_setup);

        mProfileImage = (CircleImageView) findViewById(R.id.profilImage);
        mUsersName = (EditText) findViewById(R.id.usersName);
        mAppearingDescription = (TextView) findViewById(R.id.textView4);
        mNextButton = (Button) findViewById(R.id.nextButton);
        mStorageReference = FirebaseStorage.getInstance().getReference();


        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        uid = current_user.getUid();

        mDatabaseRoot = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Select Image"), GALLERY_CODE);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = mUsersName.getText().toString();
                if(!TextUtils.isEmpty(userName)){
                    mDatabaseRoot.child("display_name").setValue(userName);
                    mDatabaseRoot.child("show_on_explore").setValue("true");
                    mDatabaseRoot.child("message_notification").setValue("true");
                    mDatabaseRoot.child("request_notification").setValue("true");
                    mNextButton.setEnabled(false);
                    Intent intent = new Intent(IntroProfileSetup.this, UsernameActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(IntroProfileSetup.this, "Enter your name.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            Uri imgUri = data.getData();
            CropImage.activity(imgUri).setAspectRatio(1, 1).start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final ProgressDialog progressDialog = new ProgressDialog(IntroProfileSetup.this);
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
                                            Picasso.get().load(mainUri).into(mProfileImage);
                                        }
                                    }
                                });
                                progressDialog.dismiss();
                                mUsersName.setEnabled(true);
                                mNextButton.setEnabled(true);
                                mAppearingDescription.animate().alpha(1f).setDuration(500).start();
                                mUsersName.animate().alpha(1f).setDuration(500).start();
                                mNextButton.animate().alpha(1f).setDuration(500).start();
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
