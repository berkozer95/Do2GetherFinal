package com.berkozer.do2getherfinal;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.berkozer.do2getherfinal.Chat.UI.Activities.ChatActivity;
import com.berkozer.do2getherfinal.Chat.Utils.Constants;
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

public class SelectedUserActivity extends AppCompatActivity {

    private TextView userNameTxtView, userFullName, userEmail, userAge, userGender, userOccupation, userUniversity, hobbiesTextView;

    private ImageView selectedUserPhoto;

    Button chatBtn;

    private ListView userHobbies;
    private ArrayList<String> userHobbiesArrayList = new ArrayList<String>();

    User currentUser;

    public static String chatUserUid;

    private FirebaseAuth firebaseAuth;

    //Real-time database
    private DatabaseReference mDatabase;
    private FirebaseDatabase mFirebaseInstance;

    //Storage
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    //Chatting user id
    public static String mUserId;

    Toolbar toolbar;

    public static void startActivity(Context context, String receiver, String receiverUid, String firebaseToken) {
        Intent intent = new Intent(context, SelectedUserActivity.class);
        intent.putExtra(Constants.ARG_RECEIVER, receiver);
        intent.putExtra(Constants.ARG_RECEIVER_UID, receiverUid);
        intent.putExtra(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_user);

        bindViews();

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userHobbiesArrayList);
        userHobbies.setAdapter(arrayAdapter);

        // get database
        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mDatabase = mFirebaseInstance.getReference("users");

        // store app title to 'app_title' node
        mFirebaseInstance.getReference("app_title").setValue("Fingo");

        // get reference
        mFirebaseStorage  = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReferenceFromUrl("gs://do2gether-8d14a.appspot.com");

        // get firebase firebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance();

        // get current user
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        mUserId = getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID);
        chatUserUid = mUserId;


        mDatabase.child(mUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = new User();
                currentUser = dataSnapshot.getValue(User.class);
                if (currentUser.getFullname() != null){
                    toolbar.setTitle(currentUser.getFullname());
                }else{
                    toolbar.setTitle(currentUser.getDisplayName());
                }
                StorageReference profilePictureRef = mStorageReference.child("Photos").child(chatUserUid).child("profile_picture");

                chatBtn.setText("Chat with " + currentUser.getFullname());


                if (currentUser.getFbPhotoURL() != null){
                    selectedUserPhoto.setVisibility(View.VISIBLE);
                    Picasso.with(SelectedUserActivity.this).load(currentUser.getFbPhotoURL()).into(selectedUserPhoto);

                } else {

                    profilePictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            selectedUserPhoto.setVisibility(View.VISIBLE);

                            Picasso.with(SelectedUserActivity.this).load(uri).into(selectedUserPhoto);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.w("TAG", "NO PHOTO PROVIDED");
                        }
                    });
                }

                if (currentUser.getAge() != null) {
                    String ageFormat = String.format("Age: %s", currentUser.getAge());
                    userAge.setText(ageFormat);
                } else {
                    userAge.setHint("This user does not provided age");
                }

                if (currentUser.getProfiletype().equals("PUBLIC")){
                    if (currentUser.getEmail() != null) {
                        String emailFormat = String.format("Email: %s", currentUser.getEmail());
                        userEmail.setText(emailFormat);
                    }
                }else{
                    userEmail.setText("Private account. Mail can not be shown.");
                }

                if (currentUser.getGender() != null) {
                    String genderFormat = String.format("Gender: %s", currentUser.getGender());
                    userGender.setText(genderFormat);
                } else {
                    userGender.setHint("This user does not provided gender");
                }

                if (currentUser.getUniversity() != null) {
                    String universityFormat = String.format("University: %s", currentUser.getUniversity());
                    userUniversity.setText(universityFormat);
                } else {
                    userUniversity.setHint("This user does not provided university");
                }

                if (currentUser.getOccupation() != null) {
                    String occupationFormat = String.format("Occupation: %s", currentUser.getOccupation());
                    userOccupation.setText(occupationFormat);
                } else {
                    userOccupation.setHint("This user does not provided occupation");
                }

                if (currentUser.getHobbies().size() > 0) {

                    for (int i = 0; i < currentUser.getHobbies().size(); i++) {
                        userHobbiesArrayList.add(currentUser.getHobbies().get(i));
                        Log.i("hobbies", currentUser.getHobbies().get(i));
                    }
                    arrayAdapter.notifyDataSetChanged();
                } else {
                    hobbiesTextView.setText(R.string.user_no_hobbies);
                    userHobbiesArrayList.add("User doesn't have any hobbies :(.");
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser.getProfiletype().equals("PUBLIC")) {
                    Toast.makeText(SelectedUserActivity.this, "You are chatting with " + currentUser.getFullname(), Toast.LENGTH_SHORT).show();
                    ChatActivity.startActivity(SelectedUserActivity.this, currentUser.getEmail(), currentUser.getUserId(), currentUser.getUserId());
                }else {
                    Toast.makeText(SelectedUserActivity.this, "This account is private. You can not start a chat", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void bindViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //userNameTxtView = (TextView) findViewById(R.id.userUserNameTextView);
        //userFullName = (TextView) findViewById(R.id.userFullNameTextView);
        selectedUserPhoto = (ImageView) findViewById(R.id.selectedUserProfilePictureImageView);
        chatBtn = (Button) findViewById(R.id.chat);
        userEmail = (TextView) findViewById(R.id.userEmailTextView);
        userAge = (TextView) findViewById(R.id.userAgeTextView);
        userGender = (TextView) findViewById(R.id.userGenderTextView);
        userOccupation = (TextView) findViewById(R.id.userOccupationTextView);
        userUniversity = (TextView) findViewById(R.id.userUniversityTextView);
        hobbiesTextView = (TextView) findViewById(R.id.userHobbiesTextView);
        userHobbies = (ListView) findViewById(R.id.userHobbiesListView);

        selectedUserPhoto.setVisibility(View.GONE);
    }


}
