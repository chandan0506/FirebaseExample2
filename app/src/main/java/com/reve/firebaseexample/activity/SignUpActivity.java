package com.reve.firebaseexample.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.reve.firebaseexample.R;

public class SignUpActivity extends AppCompatActivity {
    private EditText editTextSignUpEmail,editTextSignUpPassword;
    private Button buttonSignUp;
    private TextView textViewRedirectLogin;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firebaseAuth = FirebaseAuth.getInstance();
        initializeViews();
        
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTextSignUpEmail.getText().toString().isEmpty()) {
                    if (!editTextSignUpPassword.getText().toString().isEmpty()) {
                        String email = editTextSignUpEmail.getText().toString().trim();
                        String password = editTextSignUpPassword.getText().toString().trim();
                        if (password.length() >= 6) {
                            final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
                            progressDialog.setMessage("Please Wait");
                            progressDialog.show();
                            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(SignUpActivity.this, "Successfully Created New User!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(SignUpActivity.this, "Error Making User!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(SignUpActivity.this, "Password must be 6 characters!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        editTextSignUpPassword.setError("Password");
                    }
                } else {
                    editTextSignUpEmail.setError("Email");
                }
            }
        });
        textViewRedirectLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                finish();
            }
        });
    }

    private void initializeViews() {
        editTextSignUpEmail      = (EditText) this.findViewById(R.id.edit_text_signup_email);
        editTextSignUpPassword   = (EditText) this.findViewById(R.id.edit_text_signup_password);
        buttonSignUp             = (Button) this.findViewById(R.id.button_signup);
        textViewRedirectLogin    = (TextView) this.findViewById(R.id.text_view_redirect_login);
    }
}
