package com.reve.firebaseexample.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reve.firebaseexample.R;
import com.reve.firebaseexample.model.User;

public class RetrievingFirebaseData extends AppCompatActivity {
    private static final String TAG = RetrievingFirebaseData.class.getSimpleName();
    private TextView textViewDatabaseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieving_firebase_data);
        initializeViews();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "onDataChangee: "+user);
                textViewDatabaseData.setText(user.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelledd: "+databaseError.getDetails());
            }
        });
        /*databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "onDataChangee: "+user.toString());
                textViewDatabaseData.setText(user.name);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelledd: "+databaseError.getDetails());
            }
        });*/
    }

    private void initializeViews() {
        textViewDatabaseData   = (TextView) this.findViewById(R.id.text_view_database_data);
    }
}
