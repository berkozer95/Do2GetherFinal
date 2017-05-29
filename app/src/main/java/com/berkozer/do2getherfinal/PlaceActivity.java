package com.berkozer.do2getherfinal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.berkozer.do2getherfinal.Chat.UI.Activities.UserListingActivity;
import com.berkozer.do2getherfinal.Chat.UI.Fragments.UsersFragment;
import com.berkozer.do2getherfinal.Models.PlaceModel;
import com.berkozer.do2getherfinal.Models.Review;
import com.berkozer.do2getherfinal.Utils.QueryBuilder;
import com.berkozer.do2getherfinal.View.AdapterReview;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PlaceActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    TextView selectPlace, placeStatus, placeRating, placeAddress, placePriceLevel, placePhone, placeWebURI, placeType, mReviewTextView, mUsersTextView;

    Button getDirections, seeUsers;

    CheckBox checkInBox;

    ListView reviewsListView;

    ImageView placePhoto;

    int PLACE_PICKER_REQUEST = 1;

    private String json;

    String name = "";
    String iconUrl = "";
    String googleUrl = "";
    String vicinity = "";
    double rating = -1;
    String status = "OPEN";

    PlaceModel placeModel = new PlaceModel();

    DirectionsActivity checkLocation;

    AdapterReview adapterReview;

    private ListView usersListView;
    private ArrayList<String> usersArrayList = new ArrayList<String>();
    private ArrayAdapter arrayAdapter;

    //Realtime database
    private DatabaseReference mDatabase;
    private FirebaseDatabase mFirebaseInstance;

    String mUserId;

    //check display name
    private String uid;

    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseAuth firebaseAuth;

    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.place_activity));
        setSupportActionBar(toolbar);

        selectPlace = (TextView) findViewById(R.id.textView);
        placeRating = (TextView) findViewById(R.id.placeRating);
        placeAddress = (TextView) findViewById(R.id.placeAddress);
        placePriceLevel = (TextView) findViewById(R.id.placePriceLevel);
        placePhone = (TextView) findViewById(R.id.placePhone);
        placeWebURI = (TextView) findViewById(R.id.placeURI);
        placeType = (TextView) findViewById(R.id.placeType);
        placeStatus = (TextView) findViewById(R.id.placeStatus);
        reviewsListView = (ListView) findViewById(R.id.reviewListView);
        placePhoto = (ImageView) findViewById(R.id.placeImage);
        mReviewTextView = (TextView) findViewById(R.id.placeReviewsTxt);
        checkInBox = (CheckBox) findViewById(R.id.checkInBox);
        mUsersTextView = (TextView) findViewById(R.id.usersTextView);
        seeUsers = (Button) findViewById(R.id.seeUsersBtn);
        getDirections = (Button) findViewById(R.id.get_direction);
        usersListView = (ListView) findViewById(R.id.usersListView);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, usersArrayList);
        usersListView.setAdapter(arrayAdapter);

        placeRating.setVisibility(View.INVISIBLE);
        placeAddress.setVisibility(View.INVISIBLE);
        placePriceLevel.setVisibility(View.INVISIBLE);
        placePhone.setVisibility(View.INVISIBLE);
        placeWebURI.setVisibility(View.INVISIBLE);
        placeType.setVisibility(View.INVISIBLE);
        placeStatus.setVisibility(View.INVISIBLE);
        reviewsListView.setVisibility(View.INVISIBLE);
        placePhoto.setVisibility(View.INVISIBLE);
        mReviewTextView.setVisibility(View.INVISIBLE);
        checkInBox.setVisibility(View.INVISIBLE);
        mUsersTextView.setVisibility(View.INVISIBLE);
        usersListView.setVisibility(View.INVISIBLE);
        getDirections.setVisibility(View.INVISIBLE);
        seeUsers.setVisibility(View.INVISIBLE);


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        // get database
        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mDatabase = mFirebaseInstance.getReference("users");

        // Get Firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        // get current user
        user = firebaseAuth.getCurrentUser();
        mUserId = user.getUid();


        Toast.makeText(this, "Click Find a Place to go and select a Place", Toast.LENGTH_LONG).show();


        selectPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    Intent intent = builder.build(PlaceActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && data != null) {
            if (resultCode == RESULT_OK) {
                final Place place = PlacePicker.getPlace(data, this);
                final String placeID = place.getId();
                new AsyncPlaceDetails().execute(place.getId());
                placePhotosTask(placeID);
                placeRating.setVisibility(View.VISIBLE);
                placeAddress.setVisibility(View.VISIBLE);
                placePriceLevel.setVisibility(View.VISIBLE);
                placePhone.setVisibility(View.VISIBLE);
                placeWebURI.setVisibility(View.VISIBLE);
                placeType.setVisibility(View.VISIBLE);
                placeStatus.setVisibility(View.VISIBLE);
                reviewsListView.setVisibility(View.VISIBLE);
                placePhoto.setVisibility(View.VISIBLE);
                mReviewTextView.setVisibility(View.VISIBLE);
                checkInBox.setVisibility(View.VISIBLE);
                getDirections.setVisibility(View.VISIBLE);
                seeUsers.setVisibility(View.VISIBLE);


                Toast.makeText(this, "Click on Place Name to choose a new Place", Toast.LENGTH_LONG).show();


                checkInBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checkInBox.isChecked()) {
                            Toast.makeText(PlaceActivity.this, "Hey, I'm at " + place.getName() + "!", Toast.LENGTH_SHORT).show();
                            mDatabase.child(mUserId).child("currentplaceid").setValue(place.getId());
                        } else {
                            Toast.makeText(PlaceActivity.this, "Not at " + place.getName() + " anymore :(", Toast.LENGTH_LONG).show();
                            mDatabase.child(mUserId).child("currentplaceid").removeValue();
                        }
                    }
                });

                seeUsers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PlaceActivity.this, UserListingActivity.class);
                        intent.putExtra("currentPlaceId", String.valueOf(place.getId()));
                        intent.putExtra("userId", String.valueOf(mUserId));
                        startActivity(intent);
                    }
                });
            }
        }
    }

    private void placePhotosTask(String placeId) {

        // Create a new AsyncTask that displays the bitmap and attribution once loaded.
        new PhotoTask(placePhoto.getWidth(), placePhoto.getHeight()) {

            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    // Photo has been loaded, display it.
                    placePhoto.setImageBitmap(attributedPhoto.bitmap);
                }
            }
        }.execute(placeId);
    }

    PlaceModel parseDetails(String rawJson) {

        try {
            JSONObject json = new JSONObject(rawJson);
            JSONObject jsonResult = json.getJSONObject("result");

            //Name
            name = jsonResult.getString("name");
            placeModel.setName(name);
            selectPlace.setText(placeModel.getName());
            setTitle(name);

            //Address
            String address = jsonResult.getString("formatted_address");
            String addressStr = String.format("Address %s", address);
            placeAddress.setText(addressStr);
            placeModel.setAddress(address);

            //Icon url
            iconUrl = jsonResult.optString("icon", null);
            placeModel.setIconUrl(iconUrl);

            //Vicinity
            vicinity = jsonResult.optString("vicinity", null);
            placeModel.setVicinity(vicinity);

            //Phone Number
            if (jsonResult.has("international_phone_number")) {
                String phoneNumber = jsonResult.getString("international_phone_number");
                placePhone.setText("Call " + phoneNumber);
                if (placePhone != null) {
                    placePhone.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", placePhone.getText().toString().substring(4, placeModel.getPhoneNumber().length() + 5), null));
                            startActivity(intent);

                            return true;
                        }
                    });
                }
                placeModel.setPhoneNumber(phoneNumber);
            }

            //Website of place
            if (jsonResult.has("website")) {
                String website = jsonResult.getString("website");
                String URI = String.format("Website: %s", website);

                placeWebURI.setText(URI);
                if (placeWebURI != null) {
                    placeWebURI.setMovementMethod(LinkMovementMethod.getInstance());
                }
                placeModel.setWebsite(website);
            }

            //PriceLevel
            if (jsonResult.has("price_level")) {
                String priceDescription = "";
                int priceLevel = jsonResult.optInt("price_level");
                if (priceLevel == 0) {
                    priceDescription = "Very Cheap";
                } else if (priceLevel == 1) {
                    priceDescription = "Cheap";
                } else if (priceLevel == 2) {
                    priceDescription = "Medium";
                } else if (priceLevel == 3) {
                    priceDescription = "Expensive";
                } else if (priceLevel == 4) {
                    priceDescription = "Very Expensive";
                } else {
                    priceDescription = "Unknown";
                }
                placePriceLevel.setText(String.format("Price Level: %s", priceDescription));
                placeModel.setPriceLevel(priceLevel);

            } else {
                placePriceLevel.setText(R.string.undefinedPrice);
            }

            //Rating
            if (jsonResult.has("rating")) {
                rating = jsonResult.optDouble("rating", -1);
                String venueRating = String.format("Rating : %s / 5", rating);
                placeRating.setText(venueRating);
                //set rating
                placeModel.setRating(rating);
            }

            //Place Lat lng
            JSONObject geometry = jsonResult.optJSONObject("geometry");
            JSONObject LatLng = geometry.optJSONObject("location");
            placeModel.setLatitude(LatLng.getDouble("lat"));
            placeModel.setLongitude(LatLng.getDouble("lng"));

            getDirections.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PlaceActivity.this, DirectionsActivity.class);
                    intent.putExtra("Place lat", String.valueOf(placeModel.getLatitude()));
                    intent.putExtra("Place lng", String.valueOf(placeModel.getLongitude()));
                    startActivity(intent);
                }
            });


            //Status
            JSONObject hours = jsonResult.optJSONObject("opening_hours");
            if (hours != null) {
                boolean statusDefined = hours.has("open_now");

                if (statusDefined && hours.getBoolean("open_now")) {
                    status = "Open";
                } else {
                    status = "Closed";
                }
                String statusNow = String.format("Status: %s now", status);
                placeStatus.setText(statusNow);
                placeModel.setStatus(statusNow);
            }

            //Types
            JSONArray jsonTypes = jsonResult.optJSONArray("types");
            List<String> types = new ArrayList<>();
            String placeTypes = "";
            if (jsonTypes != null) {

                try {
                    for (int i = 0; i < jsonTypes.length(); i++) {
                        types.add(jsonTypes.getString(i).replace("_", " "));
                        Log.e("Types", jsonTypes.getString(i));
                        if (i < jsonTypes.length() - 1) {
                            placeTypes += types.get(i) + ", ";
                        } else {
                            placeTypes += types.get(i) + ".";
                        }
                    }
                    String placeTypesFormat = String.format("Type: %s", placeTypes);
                    placeType.setText(placeTypesFormat);
                    placeModel.addTypes(types);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Reviews
            JSONArray jsonReviews = jsonResult.optJSONArray("reviews");
            List<Review> reviews = new ArrayList<>();
            adapterReview = new AdapterReview(PlaceActivity.this, 0, reviews);

            if (jsonReviews != null) {
                for (int i = 0; i < jsonReviews.length(); i++) {
                    JSONObject jsonReview = jsonReviews.getJSONObject(i);

                    String author = jsonReview.optString("author_name", null);
                    String authorUrl = jsonReview.optString("author_url", null);
                    String lang = jsonReview.optString("language", null);
                    String dateInfo = jsonReview.optString("relative_time_description", null);
                    int reviewRating = jsonReview.optInt("rating", -1);
                    String text = jsonReview.optString("text", null);

                    Log.i("REVIEW Details: ", "Author: " + author + " Author url: " + authorUrl + " Laguage : " + lang + " Review Rating: " + reviewRating + " Review: " + text + " Date Info " + dateInfo);
                    if (!text.equals("")) {
                        reviews.add(new Review().setAuthor(author).setLanguage(lang).setRating(reviewRating).setText(text).setDateInfo(dateInfo));
                        placeModel.addReviews(reviews);
                    }
                }
                //Display reviews
                reviewsListView.setAdapter(adapterReview);

            }

            // For now getting 1 photo of the place
            JSONArray jsonPhotos = jsonResult.optJSONArray("photos");
            if (jsonPhotos != null) {
                try {
                    //for (int i = 0; i < jsonPhotos.length(); i++) {
                    JSONObject jsonPhoto = jsonPhotos.getJSONObject(0);
                    String photoReference = jsonPhoto.getString("photo_reference");
                    int width = jsonPhoto.getInt("width");
                    int height = jsonPhoto.getInt("height");
                    Log.e("photo details: ", photoReference + " " + width + " " + height);

                    //}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            Log.e("JSON name url iconUrl", "NAME: " + name + " ICON URL: " + placeModel.getIconUrl() + " lat lng" + placeModel.getLongitude() + " " + placeModel.getLatitude() +
                    " GOOGLE URL: " + googleUrl + " Vicinity: " + placeModel.getVicinity() + " price level " + placeModel.getPriceLevel() + " status " + placeModel.getStatus());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return placeModel;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed. Try again.", Toast.LENGTH_SHORT).show();
    }

    private class AsyncPlaceDetails extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(QueryBuilder.createURL(params[0]));
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                parseDetails(result);
                json = result;
            }
        }
    }

    //Download Image
    abstract class PhotoTask extends AsyncTask<String, Void, PhotoTask.AttributedPhoto> {

        private int mHeight;

        private int mWidth;

        public PhotoTask(int width, int height) {
            mHeight = height;
            mWidth = width;
        }

        /**
         * Loads the first photo for a place id from the Geo Data API.
         */
        @Override
        protected AttributedPhoto doInBackground(String... params) {
            if (params.length != 1) {
                return null;
            }
            final String placeId = params[0];
            AttributedPhoto attributedPhoto = null;

            PlacePhotoMetadataResult result = Places.GeoDataApi
                    .getPlacePhotos(mGoogleApiClient, placeId).await();

            if (result.getStatus().isSuccess()) {
                PlacePhotoMetadataBuffer photoMetadata = result.getPhotoMetadata();
                if (photoMetadata.getCount() > 0 && !isCancelled()) {
                    // Get the first bitmap and its attributions.
                    PlacePhotoMetadata photo = photoMetadata.get(0);
                    CharSequence attribution = photo.getAttributions();
                    // Load a scaled bitmap for this photo.
                    Bitmap image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                            .getBitmap();

                    attributedPhoto = new AttributedPhoto(attribution, image);
                }
                // Release the PlacePhotoMetadataBuffer.
                photoMetadata.release();
            }
            return attributedPhoto;
        }

        /**
         * Holder for an image and its attribution.
         */
        class AttributedPhoto {

            public final CharSequence attribution;

            public final Bitmap bitmap;

            public AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
                this.attribution = attribution;
                this.bitmap = bitmap;
            }
        }
    }
}
