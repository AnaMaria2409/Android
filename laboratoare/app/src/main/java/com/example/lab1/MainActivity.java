package com.example.lab1;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    String DESCRIPTION;
    String desc;
    ListView simpleList;
    TextView simpleText;
    String items[] = {"Primer", "Fond de ten", "Concealer", "Paleta contur", "Iluminator", "Blush"};

    Map<String, String> description = new HashMap<String, String>() {{
        put("Primer", "Hidreateaza tenul");
        put("Fond de ten", "Ascunde imperfectiuni");
        put("Concealer", "Pentru cearcane");
        put("Paleta contur", "Adauga umbre");
        put("Iluminator", "Stralucire");
        put("Blush", "Imbujorare :))");
    }};

    private FusedLocationProviderClient client;
    private LocationRequest mLocationRequest;
    private LocationCallback locationCallback;
    private Location mCurrentLocation;
    private SettingsClient settingsClient;
    private LocationSettingsRequest locationSettingsRequest;
    private boolean mRequestingLocationUpdate;
    private double latitudine = 0.0, longitudine = 0.0;

    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simpleList = (ListView) findViewById(R.id.simpleListView);
        simpleText = (TextView) findViewById(R.id.mytextView);


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        simpleList.setAdapter(arrayAdapter);

        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = (String) parent.getItemAtPosition(position);
                desc = description.get(selectedItem);
                simpleText.setText(desc);
            }
        });

        SaveFileToInternalStorage();

        //lab 6
        mRequestingLocationUpdate = false;
        updateValuesFromBundle(savedInstanceState);

        client = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(3600);
        mLocationRequest.setFastestInterval(1200);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        locationSettingsRequest = builder.build();
        settingsClient = LocationServices.getSettingsClient(this);
        if (checkPermissions())
            getLastLocation();

    }


    // lab 5 internal storage pentru produse si descrierea lor
    protected void SaveFileToInternalStorage() {
        FileOutputStream fos;
        try {
            fos = openFileOutput("saveinfo.txt", Context.MODE_PRIVATE);
            byte[] content = description.toString().getBytes();
            fos.write(content);
            Log.i("Save", "File saved!");
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("lifecycle", "sunt in onStart");
    }

    @Override
    public void onStop() {
        Log.d("lifecycle", "sunt in onStop");
        super.onStop();
    }

    protected void onResume() {
        if (mRequestingLocationUpdate && checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }
        super.onResume();
        Log.d("lifecycle", "sunt in onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        Log.d("lifecycle", "sunt in onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("lifecycle", "sunt in onRestart");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("lifecycle", "sunt in onDestoy");

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        simpleText = (TextView) findViewById(R.id.mytextView);
        simpleText.setText(savedInstanceState.getString(DESCRIPTION));
        mRequestingLocationUpdate = savedInstanceState.getBoolean(KEY_REQUESTING_LOCATION_UPDATES);
        mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        simpleText = (TextView) findViewById(R.id.mytextView);
        outState.putString(DESCRIPTION, (String) simpleText.getText());
        outState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdate);
        outState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(outState);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        return true;
    }

    //lab 4
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sms:
                Intent myIntent = new Intent();
                myIntent.setAction(Intent.ACTION_SEND);
                myIntent.putExtra(Intent.EXTRA_TEXT, "Hi!");
                myIntent.setType("text/plain");
                startActivity(myIntent);
                return true;

            case R.id.rate:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Cum am fost astazi?").setTitle("Rate us");

                builder.setPositiveButton(R.string.happy, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.setNegativeButton(R.string.sad, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            case R.id.settings:
                //lab 5
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            //lab 6
            case R.id.sensors:
                Intent sensorIntent = new Intent(this, SensorActivity.class);
                startActivity(sensorIntent);
                return true;

            case R.id.location:
                if (checkPermissions()) {
                    getLastLocation();
                    mRequestingLocationUpdate = true;
                    if (mCurrentLocation != null) {
                        latitudine = mCurrentLocation.getLatitude();
                        longitudine = mCurrentLocation.getLongitude();
                    }
                    String msg = "Latitudine: " +
                            Double.toString(latitudine) + " Longitutinde: " +
                            Double.toString(longitudine);
                    AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                    b.setMessage(msg).setTitle("Location");
                    AlertDialog newdialog = b.create();
                    newdialog.show();
                } else
                    requestPermissions();
                return true;
            case R.id.camera:
                //lab 7
                Intent cameraIntent = new Intent(this, CameraActivity.class);
                startActivity(cameraIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void onLocationChanged(Location location) {
        if (location == null) {
            Log.e("onLocationChanged", "location is null");
            return;
        }
        mCurrentLocation = location;
    }

    public void getLastLocation() {
        client.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("getLastLocation", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });

    }

    protected void startLocationUpdates() {
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        client.requestLocationUpdates(mLocationRequest, locationCallback, Looper.getMainLooper());
                        if (mCurrentLocation == null) {
                            Log.e("StartLocationUpdate", "mLocation WAS NULL");
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("StartLocationUpdate", "Error trying to update GPS location");
                        mRequestingLocationUpdate = false;
                        e.printStackTrace();
                    }
                });

    }

    private void stopLocationUpdates() {
        if (mRequestingLocationUpdate)
            client.removeLocationUpdates(locationCallback)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mRequestingLocationUpdate = false;

                        }
                    });
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdate = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }
        }
    }


    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (!shouldProvideRationale) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);

        }
    }

}