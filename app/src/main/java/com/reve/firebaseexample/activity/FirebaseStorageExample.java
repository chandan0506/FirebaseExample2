package com.reve.firebaseexample.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.reve.firebaseexample.R;
import com.reve.firebaseexample.file_explorer.FileChooser;

import java.io.File;

public class FirebaseStorageExample extends AppCompatActivity {
    private static final String TAG = FirebaseStorageExample.class.getSimpleName();
    private static final int REQUEST_PATH = 1;
    private ImageView imageViewBrowsePhoto;
    private TextView textViewShowingPath;
    private Button buttonBrowse,buttonUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_storage_example);
        initializeViews();
        imageViewBrowsePhoto.setImageResource(R.drawable.cheetah);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        final StorageReference storageReference = firebaseStorage.getReferenceFromUrl("gs://fir-example-3fedc.appspot.com/");
        buttonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(FirebaseStorageExample.this, FileChooser.class);
                startActivityForResult(intent1,REQUEST_PATH);
            }
        });
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadContent(storageReference);
            }
        });
    }

    private void uploadContent(StorageReference storageReference) {

        if (!textViewShowingPath.getText().toString().isEmpty()) {
            final ProgressDialog progressDialog = new ProgressDialog(FirebaseStorageExample.this);
            progressDialog.setTitle("Uploading...");
            Uri uriFilePath = Uri.fromFile(new File(textViewShowingPath.getText().toString()));
            final UploadTask uploadTask = storageReference.child("others/"+uriFilePath.getLastPathSegment()).putFile(uriFilePath);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(FirebaseStorageExample.this, "Upload Error!", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    textViewShowingPath.setText("");
                    Toast.makeText(FirebaseStorageExample.this, "Upload Success!", Toast.LENGTH_SHORT).show();
                }
            });

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    Log.d(TAG, "onProgress: "+progress);
                    int progressRound = (int) Math.round(progress);
                    progressDialog.setMessage("Uploaded "+progressRound+"%");
                    progressDialog.show();
                }
            });

        } else {
            Toast.makeText(this, "No File Selected!", Toast.LENGTH_SHORT).show();
        }

    }

    private void initializeViews() {
        imageViewBrowsePhoto    = (ImageView) this.findViewById(R.id.image_view_browse);
        textViewShowingPath     = (TextView) this.findViewById(R.id.text_view_showing_path);
        buttonBrowse            = (Button) this.findViewById(R.id.button_browse);
        buttonUpload            = (Button) this.findViewById(R.id.button_upload);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PATH){
            if (resultCode == RESULT_OK) {
                String curFileName = data.getStringExtra("GetFileName");
                textViewShowingPath.setText(curFileName);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
