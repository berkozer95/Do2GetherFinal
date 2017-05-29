package com.berkozer.do2getherfinal;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.berkozer.do2getherfinal.Utils.AlertDialogManager;
import com.berkozer.do2getherfinal.Utils.ConnectionDetector;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button signOut, pickPlace;

    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseAuth firebaseAuth;

    //private Snackbar snackbar;

    //Realtime database
    private DatabaseReference mDatabase;
    private FirebaseDatabase mFirebaseInstance;

    //Storage
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    // Logged in user id
    private String mUserId;

    private TextView userNameTxtView, userFullName, userEmail, userAge, userGender, userOccupation, userUniversity, hobbiesTextView;
    private ImageView profilePicture;

    private ListView userHobbies;
    private ArrayList<String> userHobbiesArrayList = new ArrayList<String>();

    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector connectionDetector;

    AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userHobbiesArrayList);
        userHobbies.setAdapter(arrayAdapter);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        isInternetPresent = connectionDetector.isConnectingToInternet();
        if (!isInternetPresent) {
            // Internet Connection is not present
            alert.showAlertDialog(MainActivity.this, "Internet Connection Error",
                    "Fingo needs Internet connection to use", false);
            // stop executing code by return
            return;
        }

        // get database
        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mDatabase = mFirebaseInstance.getReference("users");

        // store app title to 'app_title' node
        mFirebaseInstance.getReference("app_title").setValue("Fingo");

        // storage reference

        // get reference
        mFirebaseStorage  = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReferenceFromUrl("gs://do2gether-8d14a.appspot.com");

        // get Auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        // get current user
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                } else {
                    //get user id if not null
                    mUserId = user.getUid();
                    // Toast.makeText(MainActivity.this, mUserId, Toast.LENGTH_SHORT).show();

                    String username = user.getDisplayName();
                    final String useremail = user.getEmail();

                    mDatabase.child(mUserId).child("uid").setValue(mUserId);
                    mDatabase.child(mUserId).child("username").setValue(username);
                    mDatabase.child(mUserId).child("email").setValue(useremail);

                    final StorageReference profilePictureRef = mStorageReference.child("Photos").child(mUserId).child("profile_picture");


                    String userNameStr = String.format("Username: %s", username);
                    userNameTxtView.setText(userNameStr);

                    mDatabase.child(mUserId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User currentUser = new User();
                            currentUser = dataSnapshot.getValue(User.class);

                            //userFullName.setText(mDatabase.child(mUserId).child("fullname").);

                            if (currentUser.getFbPhotoURL() != null){
                                profilePicture.setVisibility(View.VISIBLE);
                                Picasso.with(MainActivity.this).load(currentUser.getFbPhotoURL()).into(profilePicture);
                            }else {
                                profilePictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Got the download URL for 'users/me/profile.png'
                                        profilePicture.setVisibility(View.VISIBLE);

                                        Picasso.with(MainActivity.this).load(uri).into(profilePicture);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(MainActivity.this, "You have to upload a photo", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            if (currentUser.getAge() != null) {
                                String ageFormat = String.format("Age: %s", currentUser.getAge());
                                userAge.setText(ageFormat);
                            } else {
                                userAge.setHint("Please enter your age to get verified by users");
                            }

                            if (currentUser.getEmail() != null) {
                                String emailFormat = String.format("Email: %s", currentUser.getEmail());
                                userEmail.setText(emailFormat);
                            } else {
                                userEmail.setHint("Please enter your email to get verified by users");
                            }

                            if (currentUser.getGender() != null) {
                                String genderFormat = String.format("Gender: %s", currentUser.getGender());
                                userGender.setText(genderFormat);
                            } else {
                                userGender.setHint("Gender must be specified");
                            }

                            if (currentUser.getUniversity() != null) {
                                String universityFormat = String.format("University: %s", currentUser.getUniversity());
                                userUniversity.setText(universityFormat);
                            } else {
                                userUniversity.setHint("Please enter your university if you're a student ");
                            }

                            if (currentUser.getOccupation() != null) {
                                String occupationFormat = String.format("Occupation: %s", currentUser.getOccupation());
                                userOccupation.setText(occupationFormat);
                            } else {
                                userOccupation.setHint("Please enter your occupation to get verified by users");
                            }

                            if (currentUser.getHobbies().size() > 0) {
                                for (int i = 0; i < currentUser.getHobbies().size(); i++) {
                                    userHobbiesArrayList.add(currentUser.getHobbies().get(i));
                                    Log.i("hobbies", currentUser.getHobbies().get(i));
                                }
                                arrayAdapter.notifyDataSetChanged();

                            } else {
                                hobbiesTextView.setText(R.string.no_hobbies);
                                userHobbiesArrayList.add("Add some hobbies in editing profile.");
                            }
                            arrayAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }


        pickPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Name
                String userDisplayName = user.getDisplayName();
                if (userDisplayName == null) {
                    Toast.makeText(MainActivity.this, "You have to fill information to find a place", Toast.LENGTH_SHORT).show();
                } else {

                    Intent intent = new Intent(MainActivity.this, PlaceActivity.class);
                    startActivity(intent);
                }
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }

    public void bindViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.profile));
        setSupportActionBar(toolbar);

        signOut = (Button) findViewById(R.id.sign_out);
        pickPlace = (Button) findViewById(R.id.pick_place);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        profilePicture = (ImageView) findViewById(R.id.profilePictureImageView);
        profilePicture.setVisibility(View.GONE);

        userNameTxtView = (TextView) findViewById(R.id.userUserNameTextView);
        //userFullName = (TextView) findViewById(R.id.userFullNameTextView);
        userEmail = (TextView) findViewById(R.id.userEmailTextView);
        userAge = (TextView) findViewById(R.id.userAgeTextView);
        userGender = (TextView) findViewById(R.id.userGenderTextView);
        userOccupation = (TextView) findViewById(R.id.userOccupationTextView);
        userUniversity = (TextView) findViewById(R.id.userUniversityTextView);
        hobbiesTextView = (TextView) findViewById(R.id.userHobbiesTextView);

        userHobbies = (ListView) findViewById(R.id.userHobbiesListView);
    }

    //sign out method
    public void signOut() {
        firebaseAuth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.edit_profile:
                Intent intent1 = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}