package com.berkozer.do2getherfinal;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.berkozer.do2getherfinal.Fragments.DrivingFragment;
import com.berkozer.do2getherfinal.Fragments.TransportFragment;
import com.berkozer.do2getherfinal.Fragments.WalkingFragment;
import com.berkozer.do2getherfinal.Models.DirectionsModel;
import com.berkozer.do2getherfinal.Models.StepsModel;
import com.berkozer.do2getherfinal.Models.TransitModel;
import com.berkozer.do2getherfinal.Utils.QueryBuilder;
import com.berkozer.do2getherfinal.View.DirectionsViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.location.LocationManager.GPS_PROVIDER;

public class DirectionsActivity extends AppCompatActivity {

    String json;

    int selectedTabId = 0;

    String placeLat, placeLng, travelMode;

    LocationManager locationManager;
    LocationListener locationListener;
    Location userLocation;

    double userLat, userLng;

    private TabLayout tabLayout;
    private ViewPager viewPager;


    Boolean page1NotVisited = true;
    Boolean page2NotVisited = true;
    Boolean page3NotVisited = true;


    DirectionsViewAdapter adapter;

    DirectionsModel directionsModel = new DirectionsModel();

    StepsModel stepsModel = new StepsModel();
    StepsModel detailedStepsModel = new StepsModel();

    TransitModel transitModel = new TransitModel();

    TextView drivingTab, walkingTab, transportTab;

    ArrayList<String> drivingArraylist;
    ArrayList<String> walkingArraylist;
    ArrayList<String> transportArraylist;
    ArrayList<String> transportStepsArrayList;

    DrivingFragment drivingFragment;
    WalkingFragment walkingFragment;
    TransportFragment transportFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        DrivingFragment.drivingSteps.clear();
        WalkingFragment.walkingSteps.clear();
        TransportFragment.transportSteps.clear();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        Intent intent = getIntent();
        placeLat = intent.getStringExtra("Place lat");
        placeLng = intent.getStringExtra("Place lng");
        selectedTabId = viewPager.getCurrentItem();

        getLocation();

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                Log.w("position", String.valueOf(position));

                selectedTabId = position;
                if (selectedTabId == 0 && page1NotVisited && userLng != 0.0) {
                    travelMode = "driving";
                    new AsyncDirectionDetails().execute(String.valueOf(userLat), String.valueOf(userLng), placeLat, placeLng, travelMode);
                    page1NotVisited = false;
                }
                if (selectedTabId == 1 && page2NotVisited && userLng != 0.0) {
                    travelMode = "walking";
                    new AsyncDirectionDetails().execute(String.valueOf(userLat), String.valueOf(userLng), placeLat, placeLng, travelMode);
                    page2NotVisited = false;
                }
                if (selectedTabId == 2 && page3NotVisited && userLng != 0.0) {
                    travelMode = "transit";
                    new AsyncDirectionDetails().execute(String.valueOf(userLat), String.valueOf(userLng), placeLat, placeLng, travelMode);
                    page3NotVisited = false;
                }

            }
        });

        drivingArraylist = new ArrayList<>();
        walkingArraylist = new ArrayList<>();
        transportArraylist = new ArrayList<>();
        transportStepsArrayList = new ArrayList<>();

        drivingArraylist.clear();
        walkingArraylist.clear();
        transportArraylist.clear();
        transportStepsArrayList.clear();

        drivingFragment = new DrivingFragment();
        walkingFragment = new WalkingFragment();
        transportFragment = new TransportFragment();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    public void getLocation() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                userLocation = location;
                userLat = userLocation.getLatitude();
                userLng = userLocation.getLongitude();

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }

        };

        if (!checkLocation())
            return;

        // If device is running SDK < 23
        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(GPS_PROVIDER, 1000, 0, locationListener);
            Location loc = locationManager.getLastKnownLocation(GPS_PROVIDER);
            if (loc != null) {
                userLat = loc.getLatitude();
                userLng = loc.getLongitude();
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                // we have permission!
                locationManager.requestLocationUpdates(GPS_PROVIDER, 1000, 0, locationListener);
            }
        }
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    public boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to use Fingo Directions")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new DirectionsViewAdapter(getSupportFragmentManager());
        adapter.addFragment(new DrivingFragment(), "Driving");
        adapter.addFragment(new WalkingFragment(), "Walking");
        adapter.addFragment(new TransportFragment(), "Transit");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        drivingTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        drivingTab.setText("Driving");
        drivingTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_driving, 0, 0);
        tabLayout.getTabAt(0).setCustomView(drivingTab);

        walkingTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        walkingTab.setText("Walking");
        walkingTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_walking, 0, 0);
        tabLayout.getTabAt(1).setCustomView(walkingTab);

        transportTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        transportTab.setText("Transit");
        transportTab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_transport, 0, 0);
        tabLayout.getTabAt(2).setCustomView(transportTab);
    }

    DirectionsModel parseDirections(String rawJson) {
        try {
            JSONObject json = new JSONObject(rawJson);
            String responseString = json.getString("status");
            Log.e("responseString", responseString);

            JSONArray routesArray = json.getJSONArray("routes");
            JSONObject route = routesArray.getJSONObject(0);
            JSONArray legs = route.getJSONArray("legs");
            JSONObject legsObject = legs.getJSONObject(0);

            String startAddress = legsObject.getString("start_address");
            String endAddress = legsObject.getString("end_address");
            directionsModel.setStartAddress(startAddress);
            directionsModel.setEndAddress(endAddress);

            String totalDistance = legsObject.getJSONObject("distance").getString("text");
            String totalDuration = legsObject.getJSONObject("duration").getString("text");
            directionsModel.setTotalDistance(totalDistance);
            directionsModel.setTotalDuration(totalDuration);

            JSONArray stepsArray = legsObject.getJSONArray("steps");


            if (travelMode.equals("driving")) {
                drivingTab.setText("Driving \n" + directionsModel.getTotalDistance());

                DrivingFragment.duration.setVisibility(View.VISIBLE);
                DrivingFragment.startingAddress.setVisibility(View.VISIBLE);
                DrivingFragment.destinationAddress.setVisibility(View.VISIBLE);
                DrivingFragment.duration.setText("Total duration: " + directionsModel.getTotalDuration());
                DrivingFragment.startingAddress.setText("Starting address: " + directionsModel.getStartAddress());
                DrivingFragment.destinationAddress.setText("Destination address:" + directionsModel.getEndAddress());

                for (int i = 0; i < stepsArray.length(); i++) {
                    JSONObject stepsObject = stepsArray.getJSONObject(i);
                    String stepsDistance = stepsObject.getJSONObject("distance").getString("text");
                    String stepsDuration = stepsObject.getJSONObject("duration").getString("text");
                    String stepsInstructions = stepsObject.getString("html_instructions").replaceAll("<(.*?)*>", "");

                    stepsModel.setDistance(stepsDistance);
                    stepsModel.setDuration(stepsDuration);
                    stepsModel.setInstructions(stepsInstructions);
                    drivingArraylist.add(stepsModel.getInstructions() + " and go for " + stepsModel.getDistance() + " apprx " + stepsModel.getDuration());
                }

                DrivingFragment.drivingSteps.addAll(drivingArraylist);
                DrivingFragment.arrayAdapter.notifyDataSetChanged();


            } else if (travelMode.equals("walking")) {
                walkingTab.setText("Walking \n" + directionsModel.getTotalDistance());

                WalkingFragment.duration.setVisibility(View.VISIBLE);
                WalkingFragment.startingAddress.setVisibility(View.VISIBLE);
                WalkingFragment.destinationAddress.setVisibility(View.VISIBLE);
                WalkingFragment.duration.setText("Total duration: " + directionsModel.getTotalDuration());
                WalkingFragment.startingAddress.setText("Starting address: " + directionsModel.getStartAddress());
                WalkingFragment.destinationAddress.setText("Destination address:" + directionsModel.getEndAddress());

                for (int i = 0; i < stepsArray.length(); i++) {
                    JSONObject stepsObject = stepsArray.getJSONObject(i);
                    String stepsDistance = stepsObject.getJSONObject("distance").getString("text");
                    String stepsDuration = stepsObject.getJSONObject("duration").getString("text");
                    String stepsInstructions = stepsObject.getString("html_instructions").replaceAll("<(.*?)*>", "");

                    stepsModel.setDistance(stepsDistance);
                    stepsModel.setDuration(stepsDuration);
                    stepsModel.setInstructions(stepsInstructions);
                    walkingArraylist.add(stepsModel.getInstructions() + " and go for " + stepsModel.getDistance() + " apprx " + stepsModel.getDuration());
                }

                WalkingFragment.walkingSteps.addAll(walkingArraylist);
                WalkingFragment.arrayAdapter.notifyDataSetChanged();

            } else if (travelMode.equals("transit")) {
                if (responseString.equals("ZERO_RESULTS")) {
                    transportArraylist.add("No transit to this address");
                }
                transportTab.setText("Transit " + directionsModel.getTotalDistance());

                TransportFragment.duration.setVisibility(View.VISIBLE);
                TransportFragment.startingAddress.setVisibility(View.VISIBLE);
                TransportFragment.destinationAddress.setVisibility(View.VISIBLE);
                TransportFragment.duration.setText("Total duration: " + directionsModel.getTotalDuration());
                TransportFragment.startingAddress.setText("Starting address: " + directionsModel.getStartAddress());
                TransportFragment.destinationAddress.setText("Destination address:" + directionsModel.getEndAddress());

                for (int i = 0; i < stepsArray.length(); i++) {
                    JSONObject stepsObject = stepsArray.getJSONObject(i);
                    String stepsDistance = stepsObject.getJSONObject("distance").getString("text");
                    String stepsDuration = stepsObject.getJSONObject("duration").getString("text");
                    String stepsInstructions = stepsObject.getString("html_instructions").replaceAll("<(.*?)*>", "");

                    String travel_mode = stepsObject.getString("travel_mode");

                    stepsModel.setDistance(stepsDistance);
                    stepsModel.setDuration(stepsDuration);
                    stepsModel.setInstructions(stepsInstructions);

                    transportArraylist.add(stepsModel.getInstructions()+ " " + travel_mode);

                    if (stepsObject.has("steps")) {
                        JSONArray detailedStepsArray = stepsObject.getJSONArray("steps");
                        transportArraylist.add("Instructions for " + stepsModel.getInstructions() + ":\nTotal " + stepsModel.getDistance() + " approx " + stepsModel.getDuration());

                        if (detailedStepsArray.length() > 0) {
                            for (int j = 0; j < detailedStepsArray.length(); j++) {
                                JSONObject detailedStepsObject = detailedStepsArray.getJSONObject(j);
                                String detailedStepsDistance = detailedStepsObject.getJSONObject("distance").getString("text");
                                String detailedStepsDuration = detailedStepsObject.getJSONObject("duration").getString("text");
                                String detailedStepsInstructions;

                                detailedStepsModel.setDistance(detailedStepsDistance);
                                detailedStepsModel.setDuration(detailedStepsDuration);

                                if (detailedStepsObject.has("html_instructions")) {
                                    detailedStepsInstructions = detailedStepsObject.getString("html_instructions").replaceAll("<(.*?)*>", "");
                                    detailedStepsModel.setInstructions(detailedStepsInstructions);
                                } else {
                                    detailedStepsModel.setInstructions("No instructions available");
                                }

                                if (!detailedStepsModel.getInstructions().equals("No instructions available")) {
                                    transportArraylist.add(detailedStepsModel.getInstructions() + " and go for " + detailedStepsModel.getDistance() + " apprx " + detailedStepsModel.getDuration());
                                } else {
                                    transportArraylist.add("Go " + detailedStepsModel.getDistance() + " apprx " + detailedStepsModel.getDuration());
                                }

                            }
                        }
                    }
                    if (stepsObject.has("transit_details")) {
                        JSONObject transitDetails = stepsObject.getJSONObject("transit_details");
                        String arrivalStop = transitDetails.getJSONObject("arrival_stop").getString("name");
                        String arrivalTime = transitDetails.getJSONObject("arrival_time").getString("text");
                        String departureStop = transitDetails.getJSONObject("departure_stop").getString("name");
                        String departureTime = transitDetails.getJSONObject("departure_time").getString("text");

                        transitModel.setArrivalStop(arrivalStop);
                        transitModel.setArrivalTime(arrivalTime);
                        transitModel.setDepartureStop(departureStop);
                        transitModel.setDepartureTime(departureTime);

                        transportArraylist.add("Transit details for " + stepsModel.getInstructions());

                        if (transitDetails.has("vehicle")) {
                            String vehicleType = transitDetails.getJSONObject("vehicle").getString("type");
                            transitModel.setVechileType(vehicleType);
                            transportArraylist.add("Vehicle type: " + transitModel.getVechileType());
                        }

                        if (transitDetails.has("line")) {
                            JSONObject line = transitDetails.getJSONObject("line");
                            String transitName = line.getString("name");
                            transitModel.setTransitName(transitName);
                            transportArraylist.add("Name: " + transitModel.getTransitName());

                            if (line.has("short_name")) {
                                String shortName = line.getString("short_name");
                                transitModel.setTransitShortName(shortName);
                                transportArraylist.add("Short name: " + transitModel.getTransitShortName());
                            }
                        }

                        transportArraylist.add("Departure time is " + transitModel.getDepartureTime() + " at " + transitModel.getDepartureStop());
                        transportArraylist.add("Arrival time is " + transitModel.getArrivalTime() + " to " + transitModel.getArrivalStop());


                        if (transitDetails.has("headsign")) {
                            String headsign = transitDetails.getString("headsign");
                            transitModel.setHeadsign(headsign);
                            transportArraylist.add("Headsign: " + transitModel.getHeadsign());
                        }

                        if (transitDetails.has("num_stops")) {
                            int numOfStops = transitDetails.getInt("num_stops");
                            transitModel.setNumOfStops(numOfStops);
                            transportArraylist.add("Number of stops: " + transitModel.getNumOfStops());
                        }
                    }
                }

                TransportFragment.transportSteps.addAll(transportArraylist);
                TransportFragment.arrayAdapter.notifyDataSetChanged();

            }


            Toast.makeText(this, legsObject.getJSONObject("distance").getString("text"), Toast.LENGTH_SHORT).show();


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class AsyncDirectionDetails extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(QueryBuilder.createDirectionsURL(Double.parseDouble(placeLat), Double.parseDouble(placeLng), userLat, userLng, travelMode));
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
                parseDirections(result);
                json = result;
                Log.i("RESULT OF JSON", json);
            }
        }
    }
}