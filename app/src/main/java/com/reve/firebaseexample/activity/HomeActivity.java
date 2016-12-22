package com.reve.firebaseexample.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.reve.firebaseexample.R;

public class HomeActivity extends AppCompatActivity {

    private Button buttonGoToDatabase,buttonGoToStorage;
    private boolean pressAgainToExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeViews();
        buttonGoToDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,FirebaseDatabaseActivity.class));
            }
        });
        buttonGoToStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,FirebaseStorageExample.class));
            }
        });
    }

    private void initializeViews() {
        buttonGoToDatabase     = (Button) this.findViewById(R.id.button_goto_database);
        buttonGoToStorage      = (Button) this.findViewById(R.id.button_goto_storage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.my_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this,MainActivity.class));
                finish();
                return true;
            case R.id.view_profile:
                startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(pressAgainToExit) {
            super.onBackPressed();
            return;
        }
        pressAgainToExit = true;
        Toast.makeText(this, "Please Click BACK Again To exit!", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pressAgainToExit = false;
            }
        },2000);
    }
}
