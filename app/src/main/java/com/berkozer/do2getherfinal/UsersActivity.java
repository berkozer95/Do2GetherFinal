package com.berkozer.do2getherfinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity {

    String userPlaceId, mUserId;

    //Realtime database
    private DatabaseReference mDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseAuth firebaseAuth;

    private FirebaseUser user;

    ListView usersListView;

    private ArrayList<String> usersArrayList = new ArrayList<String>();
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        // get database
        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mDatabase = mFirebaseInstance.getReference("users");

        // Get Firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        usersListView = (ListView) findViewById(R.id.mUsersListView);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, usersArrayList);
        usersListView.setAdapter(arrayAdapter);

        Intent intent = getIntent();
        userPlaceId = intent.getStringExtra("currentPlaceId");
        mUserId = intent.getStringExtra("userId");

        mDatabase.child(mUserId).child("currentplaceid").setValue(userPlaceId);

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                if (!dataSnapshot.getKey().equals(mUserId)) {
                    User user = new User();
                    user = dataSnapshot.getValue(User.class);
                    String currentPlace = user.getCurrentplaceid();
                    if (currentPlace != null) {
                        if (currentPlace.equals(userPlaceId)) {
                            Toast.makeText(UsersActivity.this, "User found here see who is there!", Toast.LENGTH_LONG).show();

                            usersArrayList.add(user.getFullname() + "\n" + user.getAge() + "   -   " + user.getGender());
                            arrayAdapter.notifyDataSetChanged();

                            usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                    usersArrayList.get(position);
                                    Intent intent = new Intent(UsersActivity.this, SelectedUserActivity.class);
                                    intent.putExtra("selecteduserid", dataSnapshot.getKey());
                                    Log.i("Clicked user id ", dataSnapshot.getKey());
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Toast.makeText(UsersActivity.this, "No one is here :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                    Log.i("user id", dataSnapshot.getKey() + " with place id " + currentPlace + " current users place id " + userPlaceId);
                }

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

            }
        });

    }
}
