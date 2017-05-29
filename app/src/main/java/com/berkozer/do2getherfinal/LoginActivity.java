package com.berkozer.do2getherfinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.berkozer.do2getherfinal.Utils.AlertDialogManager;
import com.berkozer.do2getherfinal.Utils.ConnectionDetector;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;






public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private EditText userEmail, userPassword;

    private ProgressBar progressBar;
    private Button signUpButton, loginButton, resetButton;
    LoginButton facebookLoginButton;

    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector connectionDetector;

    AlertDialogManager alert = new AlertDialogManager();

    private DatabaseReference mDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        bindViews();
        checkConnection();

        // Get Auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        callbackManager = CallbackManager.Factory.create();

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // Authenticate user
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    if (password.length() < 6) {
                                        userPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });

        //Facebook login
        facebookLoginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // get database
                                mFirebaseInstance = FirebaseDatabase.getInstance();

                                // get reference to 'users' node
                                mDatabase = mFirebaseInstance.getReference("users");
                                //Parse response



                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,education,picture,likes");
                request.setParameters(parameters);
                request.executeAsync();


                Log.d("Log: ", "facebook:onSuccess:" + loginResult);

            }

            @Override
            public void onCancel() {
                Log.d("Log: ", "facebook:onCancel");


            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Log: ", "facebook:onError", error);


            }
        });

    }

    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d("Log: ", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Log: ", "signInWithCredential:success");
                            final FirebaseUser user = firebaseAuth.getCurrentUser();

                            AccessToken accessToken = AccessToken.getCurrentAccessToken();
                            GraphRequest request = GraphRequest.newMeRequest(
                                    accessToken,
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                            Log.v("LoginActivity", response.toString());

                                            // get database
                                            mFirebaseInstance = FirebaseDatabase.getInstance();

                                            // get reference to 'users' node
                                            mDatabase = mFirebaseInstance.getReference("users");
                                            //Parse response

                                            String mUserId = user.getUid();

                                            try{
                                                if(object.has("name")){
                                                    mDatabase.child(mUserId).child("fullname").setValue(object.getString("name"));
                                                }
                                                if (object.has("gender")) {
                                                    mDatabase.child(mUserId).child("gender").setValue(object.getString("gender"));
                                                }
                                                if (object.has("education")) {
                                                    JSONArray education = object.getJSONArray("education");
                                                    for (int i = 0; i<education.length();i++){
                                                        JSONObject school = (JSONObject) education.get(i);
                                                        String schoolType = school.getString("type");
                                                        if (schoolType.equals("College")){
                                                            String schoolName = school.getJSONObject("school").getString("name");
                                                            mDatabase.child(mUserId).child("university").setValue(schoolName);
                                                        }
                                                    }
                                                }

                                                if (object.has("age_range")){
                                                    JSONObject age = object.getJSONObject("age_range");
                                                    int userAge = age.getInt("min");
                                                    String ageStr = String.valueOf(userAge);
                                                    mDatabase.child(mUserId).child("age").setValue(ageStr);
                                                }

                                                if(object.has("picture")){
                                                    JSONObject picture = object.getJSONObject("picture");
                                                    JSONObject data = picture.getJSONObject("data");
                                                    String photoURL = data.getString("url");
                                                    mDatabase.child(mUserId).child("fbPhotoURL").setValue(photoURL);
                                                }

                                                if (object.has("likes")){
                                                    ArrayList<String> userLikesArrayList = new ArrayList<String>();
                                                    JSONObject userLikes = object.getJSONObject("likes");
                                                    JSONArray likesData = userLikes.getJSONArray("data");
                                                    for (int i =0; i< likesData.length(); i++){
                                                        JSONObject likes = likesData.getJSONObject(i);
                                                        userLikesArrayList.add(likes.getString("name"));
                                                    }
                                                    mDatabase.child(mUserId).child("hobbies").setValue(userLikesArrayList);

                                                }

                                            }catch (JSONException e){
                                                e.printStackTrace();
                                            }

                                            Toast.makeText(LoginActivity.this, user.getUid(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email,gender,age_range,education,picture,likes");
                            request.setParameters(parameters);
                            request.executeAsync();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Log: ", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void bindViews(){

        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userEmail = (EditText) findViewById(R.id.email);
        userPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        signUpButton = (Button) findViewById(R.id.sign_up);
        loginButton = (Button) findViewById(R.id.login);
        facebookLoginButton = (LoginButton) findViewById(R.id.facebook_login);
        resetButton = (Button) findViewById(R.id.reset_password);
    }

    private void checkConnection(){
        connectionDetector = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        isInternetPresent = connectionDetector.isConnectingToInternet();
        if (!isInternetPresent) {
            // Internet Connection is not present
            alert.showAlertDialog(LoginActivity.this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }
    }

}
