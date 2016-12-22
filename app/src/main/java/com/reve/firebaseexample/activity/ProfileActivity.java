package com.reve.firebaseexample.activity;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.reve.firebaseexample.R;

import java.io.File;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private ImageView imageViewProfilePic;
    private TextView textViewProfileName,textViewProfileEmail;
    private Button buttonUpdateProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeViews();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            if (firebaseUser.getEmail() != null) {
                textViewProfileEmail.setText(firebaseUser.getEmail());
            }
            if (firebaseUser.getDisplayName() != null) {
                textViewProfileName.setText(firebaseUser.getDisplayName());
            }
            if (firebaseUser.getPhotoUrl() != null) {
                Glide.with(ProfileActivity.this)
                        .load(firebaseUser.getPhotoUrl())
                        .into(imageViewProfilePic);
            }
        }
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTask();
            }
        });
    }

    private void updateTask() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Uri uriFilePath = Uri.fromFile(new File("/storage/emulated/0/DCIM/Camera/1457426734732.jpg"));
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName("TestGuy")
                .setPhotoUri(uriFilePath)
                .build();
        if (firebaseUser != null) {
            firebaseUser.updateProfile(profileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: Updated Successfully");
                            }
                        }
                    });
        }

    }

    private void initializeViews() {
        imageViewProfilePic     = (ImageView) this.findViewById(R.id.image_view_profile_pic);
        textViewProfileName     = (TextView) this.findViewById(R.id.text_view_profile_name);
        textViewProfileEmail    = (TextView) this.findViewById(R.id.text_view_profile_email);
        buttonUpdateProfile     = (Button) this.findViewById(R.id.button_update_profile);
    }
}
