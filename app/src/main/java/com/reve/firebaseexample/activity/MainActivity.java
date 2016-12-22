package com.reve.firebaseexample.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.reve.firebaseexample.R;
import com.reve.firebaseexample.firebaseService.MyFirebaseInstanceIdService;

import org.json.JSONObject;

import java.util.Set;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;
    private EditText editTextLoginEmail,editTextLoginPassword;
    private Button buttonLogin;
    private TextView textViewRedirectSignUp;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private SignInButton signInButton;
    private GoogleApiClient googleApiClient;
    private LoginButton loginButtonFacebook;
    private CallbackManager callbackManager;
    private boolean pressAgainToExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        initializeViews();
        configuringGoogleSignInOption();
        initializeFacebookSdk();
        startService(new Intent(MainActivity.this, MyFirebaseInstanceIdService.class));
        //FirebaseCrash.report(new Exception("My Test Android Error"));
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "onCreate: "+token);
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    startActivity(new Intent(MainActivity.this,HomeActivity.class));
                    Log.d(TAG, "onAuthStateChanged: "+firebaseUser.getDisplayName());
                    Log.d(TAG, "onAuthStateChanged: "+firebaseUser.getEmail());
                    Log.d(TAG, "onAuthStateChanged: "+firebaseUser.getPhotoUrl());
                    Log.d(TAG, "onAuthStateChanged: SignedIn "+firebaseUser.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged: SignedOut");
                }
            }
        };
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        //signInButton.setBackgroundColor(SignInButton.COLOR_DARK);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSignIn = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intentSignIn,RC_SIGN_IN);
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTextLoginEmail.getText().toString().isEmpty()) {
                    if (!editTextLoginPassword.getText().toString().isEmpty()) {
                        String email = editTextLoginEmail.getText().toString().trim();
                        String password = editTextLoginPassword.getText().toString().trim();
                        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("Please Wait");
                        progressDialog.show();
                        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Successfully Login!", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(MainActivity.this, "Error in Login!", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    } else {
                        editTextLoginPassword.setError("Password");
                    }
                } else {
                    editTextLoginEmail.setError("Email");
                }
            }
        });
        textViewRedirectSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SignUpActivity.class));
                finish();
            }
        });
    }

    private void initializeFacebookSdk() {
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        if (loginButtonFacebook != null) {
            loginButtonFacebook.setReadPermissions("user_friends");
            loginButtonFacebook.setReadPermissions("email");
        }
        if (loginButtonFacebook != null) {
            loginButtonFacebook.registerCallback(callbackManager, callback);
        }

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };
        ProfileTracker profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                displayMessage(newProfile);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();
    }

    private void displayMessage(Profile newProfile) {
        if (newProfile != null) {
            Log.d(TAG, "displayMessage: "+newProfile.getFirstName());
        }
    }

    private synchronized void configuringGoogleSignInOption() {
        String googleClientId = "883003898060-5f4i59trqsnvgctbikkilvb8r0ngdvpf.apps.googleusercontent.com";
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(googleClientId)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions)
                .build();

    }

    private void initializeViews() {
        editTextLoginEmail       = (EditText) this.findViewById(R.id.edit_text_login_email);
        editTextLoginPassword    = (EditText) this.findViewById(R.id.edit_text_login_password);
        buttonLogin              = (Button) this.findViewById(R.id.button_login);
        textViewRedirectSignUp   = (TextView) this.findViewById(R.id.text_view_redirect_signup);
        signInButton             = (SignInButton) findViewById(R.id.sign_in_button);
        loginButtonFacebook      = (LoginButton) this.findViewById(R.id.login_button_facebook);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(signInResult);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(GoogleSignInResult signInResult) {
        if (signInResult.isSuccess()) {
            GoogleSignInAccount signInAccount = signInResult.getSignInAccount();
            Toast.makeText(this, "Name = " + (signInAccount != null ? signInAccount.getDisplayName() : null), Toast.LENGTH_SHORT).show();
            firebaseAuthWithGoogle(signInAccount);
        } else {
            Toast.makeText(this, "Error in Google Sign In!", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount signInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle: "+signInAccount.getId());
        AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(),null);
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Successfully getting signin!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this,HomeActivity.class));
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(final LoginResult loginResult) {
            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            String accessToken = loginResult.getAccessToken().getToken();
            Set<String> permission = loginResult.getAccessToken().getPermissions();
            if (permission.contains("email")) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        Log.i("LoginActivity", response.toString());
                        // Get facebook data from login
                        //Bundle bFacebookData = getFacebookData(object);
                        //Log.d(TAG, "onCompleted: "+ (bFacebookData != null ? bFacebookData.toString() : null));
                        handleFacebookAccessToken(loginResult.getAccessToken());
                        progressDialog.dismiss();
                    }
                });
                Bundle bundle = new Bundle();
                bundle.putString("fields", "id,first_name,last_name,email,gender,birthday,location");
                graphRequest.setParameters(bundle);
                graphRequest.executeAsync();
            } else {
                GraphRequest request = new GraphRequest(
                        AccessToken.getCurrentAccessToken(),"/me/permissions", null, HttpMethod.DELETE,
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse graphResponse) {
                                LoginManager.getInstance().logOut();
                                Toast.makeText(MainActivity.this, "Email permission is needed. Please try again.", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                );
                request.executeAsync();
            }
            Log.i("accessToken", accessToken);

            /*GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {

                    Log.i("LoginActivity", response.toString());
                    // Get facebook data from login
                    //Bundle bFacebookData = getFacebookData(object);
                    //Log.d(TAG, "onCompleted: "+ (bFacebookData != null ? bFacebookData.toString() : null));
                    progressDialog.dismiss();
                    handleFacbookAccessToken(loginResult.getAccessToken());
                }
            });
            Bundle bundle = new Bundle();
            bundle.putString("fields", "id,first_name,last_name,email,gender,birthday,location");
            graphRequest.setParameters(bundle);
            graphRequest.executeAsync();*/

            //AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            displayMessage(profile);
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {
            Log.e(TAG, "onError: ",error );
        }
    };

    private void handleFacebookAccessToken(final AccessToken accessToken) {
        Log.d(TAG, "handleFacebookAccessToken: "+accessToken);
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)  {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: "+task.isSuccessful());
                            startActivity(new Intent(MainActivity.this,HomeActivity.class));
                            finish();
                        } else {
                            Log.w(TAG, "onComplete: ",task.getException() );
                            Toast.makeText(MainActivity.this, "An Account exist with same email Address!", Toast.LENGTH_SHORT).show();
                            
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
                Log.d(TAG, "onFailure: "+accessToken.getToken());
            }
        });
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
