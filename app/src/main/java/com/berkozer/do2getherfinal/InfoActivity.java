package com.berkozer.do2getherfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class InfoActivity extends AppCompatActivity implements Serializable {

    private static final int GALLERY_INTENT = 2;

    Button sendUniversity, sendUserName, sendName, sendAge, sendGender, sendOccupation, sendHobby,
            changeUniversity, changeUserName, changeName, changeAge, changeGender, changeOccupation, addHobby, addPhoto, choosePhoto,profileType;
    private EditText inputUserName, inputName, inputAge, inputGender, inputOccupation, inputUniversity, inputHobby;

    ImageView profilePicture;

    //Realtime database
    private DatabaseReference mDatabase;
    private FirebaseDatabase mFirebaseInstance;

    //Storage reference
    private StorageReference mStorageReference;

    String mUserId;
    //check display name
    private String uid;

    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseAuth firebaseAuth;

    private ArrayList<String> hobbies = new ArrayList<String>();

    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        bindViews();

        // get database
        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mDatabase = mFirebaseInstance.getReference("users");

        // get reference
        mStorageReference = FirebaseStorage.getInstance().getReference();

        // Get Firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        // get current user
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        mUserId = user.getUid();
        final User mUser = new User();

        mDatabase.child(mUserId).child("profiletype").setValue("PUBLIC");
        mUser.setProfiletype("PUBLIC");


        /**User DisplayName**/
        sendUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!inputUserName.getText().toString().equals("")) {
                    for (UserInfo profile : user.getProviderData()) {
                        // UID specific to the provider
                        uid = profile.getUid();

                        // Name
                        String name = profile.getDisplayName();

                        if (mUserId != uid && inputUserName.getText().toString().equals(name)) {
                            Toast.makeText(InfoActivity.this, "The user name you selected has already in use", Toast.LENGTH_SHORT).show();
                            break;
                        } else {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(inputUserName.getText().toString())
                                    .build();

                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Log", "User profile updated. " + user.getDisplayName());
                                        Toast.makeText(InfoActivity.this, "Your username is set to " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                                        mUser.setDisplayName(user.getDisplayName());
                                    } else {
                                        Toast.makeText(InfoActivity.this, "An error occurred.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                } else {
                    inputUserName.setError("Check your input!");
                }
            }
        });

        /**User FullName**/
        sendName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!inputName.getText().toString().equals("")) {
                    mDatabase.child(mUserId).child("fullname").setValue(inputName.getText().toString());
                    Toast.makeText(InfoActivity.this, "Name saved!", Toast.LENGTH_SHORT).show();
                    mUser.setFullname(inputUserName.getText().toString());
                } else {
                    inputName.setError("Check your input!");
                }
            }
        });

        sendAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!inputAge.getText().toString().equals("")) {
                    mDatabase.child(mUserId).child("age").setValue(inputAge.getText().toString());
                    Toast.makeText(InfoActivity.this, "Age saved!", Toast.LENGTH_SHORT).show();
                    mUser.setAge(inputAge.getText().toString());
                } else {
                    inputAge.setError("Check your input!");
                }
            }
        });

        sendGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!inputGender.getText().toString().equals("")) {
                    mDatabase.child(mUserId).child("gender").setValue(inputGender.getText().toString());
                    Toast.makeText(InfoActivity.this, "Gender saved!", Toast.LENGTH_SHORT).show();
                    mUser.setGender(inputGender.getText().toString());
                } else {
                    inputGender.setError("Check your input!");
                }
            }
        });

        sendOccupation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!inputOccupation.getText().toString().equals("")) {
                    mDatabase.child(mUserId).child("occupation").setValue(inputOccupation.getText().toString());
                    Toast.makeText(InfoActivity.this, "Occupation saved!", Toast.LENGTH_SHORT).show();
                    mUser.setOccupation(inputOccupation.getText().toString());
                } else {
                    inputOccupation.setError("Check your input!");
                }
            }
        });

        sendUniversity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!inputUniversity.getText().toString().equals("")) {
                    mDatabase.child(mUserId).child("university").setValue(inputUniversity.getText().toString());
                    Toast.makeText(InfoActivity.this, "University saved!", Toast.LENGTH_SHORT).show();
                    mUser.setUniversity(inputUniversity.getText().toString());
                } else {
                    inputUniversity.setError("Check your input!");
                }
            }
        });

        sendHobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * FIX THE PROBLEM WHILE ADDING NEW HOBBY
                 * */
                if (inputHobby.getText().toString().equals("")) {
                    inputHobby.setError("Enter a hobby!");

                } else {
                    hobbies.add(inputHobby.getText().toString());
                    inputHobby.setText("");

                    mDatabase.child(mUserId).child("hobbies").setValue(hobbies);

                    Toast.makeText(InfoActivity.this, inputHobby.getText().toString() + " is added to your hobbies list.", Toast.LENGTH_SHORT).show();
                    mUser.setHobbies(hobbies);
                }
            }
        });

        profileType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUser.getProfiletype().equals("PUBLIC")){
                    mDatabase.child(mUserId).child("profiletype").setValue("PRIVATE");
                    mUser.setProfiletype("PRIVATE");
                    profileType.setText("MAKE PUBLIC PROFILE");

                }else {
                    mDatabase.child(mUserId).child("profiletype").setValue("PUBLIC");
                    mUser.setProfiletype("PUBLIC");
                    profileType.setText("MAKE PRIVATE PROFILE");
                }
            }
        });

        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            mProgressDialog.setMessage("Uploading Profile Picture");
            mProgressDialog.show();

            final Uri uri = data.getData();

            StorageReference filePath = mStorageReference.child("Photos").child(mUserId).child("profile_picture");

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgressDialog.dismiss();
                    Toast.makeText(InfoActivity.this, "Photo Uploaded", Toast.LENGTH_SHORT).show();
                    profilePicture.setVisibility(View.VISIBLE);
                    profilePicture.setImageURI(uri);
                }
            });

        }
    }

    public void bindViews(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.edit_profile));
        setSupportActionBar(toolbar);

        changeUserName = (Button) findViewById(R.id.set_user_name);
        changeName = (Button) findViewById(R.id.set_name_button);
        changeAge = (Button) findViewById(R.id.set_age_button);
        changeGender = (Button) findViewById(R.id.set_gender_button);
        changeOccupation = (Button) findViewById(R.id.set_occupation_button);
        changeUniversity = (Button) findViewById(R.id.set_university_button);
        addHobby = (Button) findViewById(R.id.set_hobby_button);
        addPhoto = (Button) findViewById(R.id.set_profile_picture);

        sendUserName = (Button) findViewById(R.id.setUserName);
        sendName = (Button) findViewById(R.id.setName);
        sendAge = (Button) findViewById(R.id.setAge);
        sendGender = (Button) findViewById(R.id.setGender);
        sendOccupation = (Button) findViewById(R.id.setOccupation);
        sendUniversity = (Button) findViewById(R.id.setUniversity);
        sendHobby = (Button) findViewById(R.id.setHobbies);
        choosePhoto = (Button) findViewById(R.id.setProfilePicture);
        profileType = (Button) findViewById(R.id.set_profile_type);

        inputUserName = (EditText) findViewById(R.id.userNameEditText);
        inputName = (EditText) findViewById(R.id.nameEditText);
        inputAge = (EditText) findViewById(R.id.ageEditText);
        inputGender = (EditText) findViewById(R.id.genderEditText);
        inputOccupation = (EditText) findViewById(R.id.occupationEditText);
        inputUniversity = (EditText) findViewById(R.id.universityEditText);
        inputHobby = (EditText) findViewById(R.id.hobbiesEditText);
        profilePicture = (ImageView) findViewById(R.id.profilePictureImageView);

        mProgressDialog = new ProgressDialog(this);

        inputUserName.setVisibility(View.GONE);
        inputName.setVisibility(View.GONE);
        inputAge.setVisibility(View.GONE);
        inputGender.setVisibility(View.GONE);
        inputOccupation.setVisibility(View.GONE);
        inputUniversity.setVisibility(View.GONE);
        inputHobby.setVisibility(View.GONE);
        profilePicture.setVisibility(View.GONE);

        sendUserName.setVisibility(View.GONE);
        sendName.setVisibility(View.GONE);
        sendAge.setVisibility(View.GONE);
        sendGender.setVisibility(View.GONE);
        sendOccupation.setVisibility(View.GONE);
        sendUniversity.setVisibility(View.GONE);
        sendHobby.setVisibility(View.GONE);
        choosePhoto.setVisibility(View.GONE);


        changeUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUserName.setVisibility(View.GONE);
                changeName.setVisibility(View.VISIBLE);
                changeAge.setVisibility(View.VISIBLE);
                changeGender.setVisibility(View.VISIBLE);
                changeOccupation.setVisibility(View.VISIBLE);
                changeUniversity.setVisibility(View.VISIBLE);
                addHobby.setVisibility(View.VISIBLE);
                addPhoto.setVisibility(View.VISIBLE);

                inputUserName.setVisibility(View.VISIBLE);
                inputName.setVisibility(View.GONE);
                inputAge.setVisibility(View.GONE);
                inputGender.setVisibility(View.GONE);
                inputOccupation.setVisibility(View.GONE);
                inputUniversity.setVisibility(View.GONE);
                inputHobby.setVisibility(View.GONE);
                profilePicture.setVisibility(View.GONE);


                sendUserName.setVisibility(View.VISIBLE);
                sendName.setVisibility(View.GONE);
                sendAge.setVisibility(View.GONE);
                sendGender.setVisibility(View.GONE);
                sendOccupation.setVisibility(View.GONE);
                sendUniversity.setVisibility(View.GONE);
                sendHobby.setVisibility(View.GONE);
                choosePhoto.setVisibility(View.GONE);

            }
        });

        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUserName.setVisibility(View.VISIBLE);
                changeName.setVisibility(View.GONE);
                changeAge.setVisibility(View.VISIBLE);
                changeGender.setVisibility(View.VISIBLE);
                changeOccupation.setVisibility(View.VISIBLE);
                changeUniversity.setVisibility(View.VISIBLE);
                addHobby.setVisibility(View.VISIBLE);
                addPhoto.setVisibility(View.VISIBLE);

                changeName.setVisibility(View.GONE);
                inputUserName.setVisibility(View.GONE);
                inputName.setVisibility(View.VISIBLE);
                inputAge.setVisibility(View.GONE);
                inputGender.setVisibility(View.GONE);
                inputOccupation.setVisibility(View.GONE);
                inputUniversity.setVisibility(View.GONE);
                inputHobby.setVisibility(View.GONE);
                profilePicture.setVisibility(View.GONE);

                sendUserName.setVisibility(View.GONE);
                sendName.setVisibility(View.VISIBLE);
                sendAge.setVisibility(View.GONE);
                sendGender.setVisibility(View.GONE);
                sendOccupation.setVisibility(View.GONE);
                sendUniversity.setVisibility(View.GONE);
                sendHobby.setVisibility(View.GONE);
                choosePhoto.setVisibility(View.GONE);

            }
        });

        changeAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUserName.setVisibility(View.VISIBLE);
                changeName.setVisibility(View.VISIBLE);
                changeAge.setVisibility(View.GONE);
                changeGender.setVisibility(View.VISIBLE);
                changeOccupation.setVisibility(View.VISIBLE);
                changeUniversity.setVisibility(View.VISIBLE);
                addHobby.setVisibility(View.VISIBLE);
                addPhoto.setVisibility(View.VISIBLE);

                inputUserName.setVisibility(View.GONE);
                inputName.setVisibility(View.GONE);
                inputAge.setVisibility(View.VISIBLE);
                inputGender.setVisibility(View.GONE);
                inputOccupation.setVisibility(View.GONE);
                inputUniversity.setVisibility(View.GONE);
                inputHobby.setVisibility(View.GONE);
                profilePicture.setVisibility(View.GONE);

                sendUserName.setVisibility(View.GONE);
                sendName.setVisibility(View.GONE);
                sendAge.setVisibility(View.VISIBLE);
                sendGender.setVisibility(View.GONE);
                sendOccupation.setVisibility(View.GONE);
                sendUniversity.setVisibility(View.GONE);
                sendHobby.setVisibility(View.GONE);
                choosePhoto.setVisibility(View.GONE);

            }
        });

        changeGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUserName.setVisibility(View.VISIBLE);
                changeName.setVisibility(View.VISIBLE);
                changeAge.setVisibility(View.VISIBLE);
                changeGender.setVisibility(View.GONE);
                changeOccupation.setVisibility(View.VISIBLE);
                changeUniversity.setVisibility(View.VISIBLE);
                addHobby.setVisibility(View.VISIBLE);
                addPhoto.setVisibility(View.VISIBLE);

                inputUserName.setVisibility(View.GONE);
                inputName.setVisibility(View.GONE);
                inputAge.setVisibility(View.GONE);
                inputGender.setVisibility(View.VISIBLE);
                inputOccupation.setVisibility(View.GONE);
                inputUniversity.setVisibility(View.GONE);
                inputHobby.setVisibility(View.GONE);
                profilePicture.setVisibility(View.GONE);

                sendUserName.setVisibility(View.GONE);
                sendName.setVisibility(View.GONE);
                sendAge.setVisibility(View.GONE);
                sendGender.setVisibility(View.VISIBLE);
                sendOccupation.setVisibility(View.GONE);
                sendUniversity.setVisibility(View.GONE);
                sendHobby.setVisibility(View.GONE);
                choosePhoto.setVisibility(View.GONE);

            }
        });

        changeOccupation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUserName.setVisibility(View.VISIBLE);
                changeName.setVisibility(View.VISIBLE);
                changeAge.setVisibility(View.VISIBLE);
                changeGender.setVisibility(View.VISIBLE);
                changeOccupation.setVisibility(View.GONE);
                changeUniversity.setVisibility(View.VISIBLE);
                addHobby.setVisibility(View.VISIBLE);
                addPhoto.setVisibility(View.VISIBLE);

                inputUserName.setVisibility(View.GONE);
                inputName.setVisibility(View.GONE);
                inputAge.setVisibility(View.GONE);
                inputGender.setVisibility(View.GONE);
                inputOccupation.setVisibility(View.VISIBLE);
                inputUniversity.setVisibility(View.GONE);
                inputHobby.setVisibility(View.GONE);
                profilePicture.setVisibility(View.GONE);

                sendUserName.setVisibility(View.GONE);
                sendName.setVisibility(View.GONE);
                sendAge.setVisibility(View.GONE);
                sendGender.setVisibility(View.GONE);
                sendOccupation.setVisibility(View.VISIBLE);
                sendUniversity.setVisibility(View.GONE);
                sendHobby.setVisibility(View.GONE);
                choosePhoto.setVisibility(View.GONE);

            }
        });

        changeUniversity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUserName.setVisibility(View.VISIBLE);
                changeName.setVisibility(View.VISIBLE);
                changeAge.setVisibility(View.VISIBLE);
                changeGender.setVisibility(View.VISIBLE);
                changeOccupation.setVisibility(View.VISIBLE);
                changeUniversity.setVisibility(View.GONE);
                addHobby.setVisibility(View.VISIBLE);
                addPhoto.setVisibility(View.VISIBLE);

                inputUserName.setVisibility(View.GONE);
                inputName.setVisibility(View.GONE);
                inputAge.setVisibility(View.GONE);
                inputGender.setVisibility(View.GONE);
                inputOccupation.setVisibility(View.GONE);
                inputUniversity.setVisibility(View.VISIBLE);
                inputHobby.setVisibility(View.GONE);
                profilePicture.setVisibility(View.GONE);

                sendUserName.setVisibility(View.GONE);
                sendName.setVisibility(View.GONE);
                sendAge.setVisibility(View.GONE);
                sendGender.setVisibility(View.GONE);
                sendOccupation.setVisibility(View.GONE);
                sendUniversity.setVisibility(View.VISIBLE);
                sendHobby.setVisibility(View.GONE);
                choosePhoto.setVisibility(View.GONE);

            }
        });

        addHobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUserName.setVisibility(View.VISIBLE);
                changeName.setVisibility(View.VISIBLE);
                changeAge.setVisibility(View.VISIBLE);
                changeGender.setVisibility(View.VISIBLE);
                changeOccupation.setVisibility(View.VISIBLE);
                changeUniversity.setVisibility(View.VISIBLE);
                addHobby.setVisibility(View.GONE);
                addPhoto.setVisibility(View.VISIBLE);

                inputUserName.setVisibility(View.GONE);
                inputName.setVisibility(View.GONE);
                inputAge.setVisibility(View.GONE);
                inputGender.setVisibility(View.GONE);
                inputOccupation.setVisibility(View.GONE);
                inputUniversity.setVisibility(View.GONE);
                inputHobby.setVisibility(View.VISIBLE);
                profilePicture.setVisibility(View.GONE);

                sendUserName.setVisibility(View.GONE);
                sendName.setVisibility(View.GONE);
                sendAge.setVisibility(View.GONE);
                sendGender.setVisibility(View.GONE);
                sendOccupation.setVisibility(View.GONE);
                sendUniversity.setVisibility(View.GONE);
                sendHobby.setVisibility(View.VISIBLE);
                choosePhoto.setVisibility(View.GONE);

            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserName.setVisibility(View.VISIBLE);
                changeName.setVisibility(View.VISIBLE);
                changeAge.setVisibility(View.VISIBLE);
                changeGender.setVisibility(View.VISIBLE);
                changeOccupation.setVisibility(View.VISIBLE);
                changeUniversity.setVisibility(View.VISIBLE);
                addHobby.setVisibility(View.VISIBLE);
                addPhoto.setVisibility(View.GONE);

                inputUserName.setVisibility(View.GONE);
                inputName.setVisibility(View.GONE);
                inputAge.setVisibility(View.GONE);
                inputGender.setVisibility(View.GONE);
                inputOccupation.setVisibility(View.GONE);
                inputUniversity.setVisibility(View.GONE);
                inputHobby.setVisibility(View.GONE);
                profilePicture.setVisibility(View.GONE);

                sendUserName.setVisibility(View.GONE);
                sendName.setVisibility(View.GONE);
                sendAge.setVisibility(View.GONE);
                sendGender.setVisibility(View.GONE);
                sendOccupation.setVisibility(View.GONE);
                sendUniversity.setVisibility(View.GONE);
                sendHobby.setVisibility(View.GONE);
                choosePhoto.setVisibility(View.VISIBLE);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
