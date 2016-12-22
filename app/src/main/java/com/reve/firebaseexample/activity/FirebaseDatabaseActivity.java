package com.reve.firebaseexample.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reve.firebaseexample.R;
import com.reve.firebaseexample.model.User;

public class FirebaseDatabaseActivity extends AppCompatActivity {
    private static final String TAG = FirebaseDatabaseActivity.class.getSimpleName();
    private EditText editTextDatabaseName,editTextDatabaseAge;
    private Button buttonSaveToDatabase,buttonShowData;
    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_database);
        initializeViews();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
        firebaseDatabase.getReference("app_title").setValue("RealTime Database");
        firebaseDatabase.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue(String.class);
                Log.d(TAG, "onDataChange: "+text);
                Log.d(TAG, "Title Changed");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        buttonSaveToDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTextDatabaseName.getText().toString().trim().isEmpty()){
                    if (!editTextDatabaseAge.getText().toString().trim().isEmpty()) {
                        final ProgressDialog progressDialog = new ProgressDialog(FirebaseDatabaseActivity.this);
                        progressDialog.setMessage("Please Wait");
                        progressDialog.show();
                        final String name = editTextDatabaseName.getText().toString();
                        final String age  = editTextDatabaseAge.getText().toString();
                        createUser(name,age);
                        progressDialog.dismiss();
                    } else {
                        editTextDatabaseAge.setError("AGE");
                    }
                } else {
                    editTextDatabaseName.setError("Name");
                }
            }
        });
        buttonShowData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirebaseDatabaseActivity.this,RetrievingFirebaseData.class));
            }
        });

    }

    private void createUser(String name, String age) {
        userId = databaseReference.push().getKey();
        Log.d(TAG, "createUser: "+userId);
        //User user = new User(name,age);
        User user = new User();
        user.setAge(age);
        user.setName(name);
        databaseReference.child(userId).setValue(user);
        addUserChangeListener();
    }

    private void addUserChangeListener() {
        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    Log.d(TAG, "onDataChange: "+"User is null");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ",databaseError.toException() );
            }
        });
    }

    private void initializeViews() {
        editTextDatabaseName      = (EditText) this.findViewById(R.id.edit_text_database_name);
        editTextDatabaseAge       = (EditText) this.findViewById(R.id.edit_text_database_age);
        buttonSaveToDatabase      = (Button) this.findViewById(R.id.button_save_to_database);
        buttonShowData            = (Button) this.findViewById(R.id.button_show_data);
    }
}
