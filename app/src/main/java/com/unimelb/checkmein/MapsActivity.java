package com.unimelb.checkmein;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.unimelb.checkmein.bean.Building;
import com.unimelb.checkmein.bean.Session;
import com.unimelb.checkmein.bean.Subject;
import com.unimelb.checkmein.bean.User;
import com.unimelb.checkmein.ui.rank.RankViewHolder;
import com.unimelb.checkmein.ui.rank.ScrollingActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private LocationManager locationManager;
    private String provider;
    private MapView mMapView;
    private String MAPVIEW_BUNDLE_KEY = "AIzaSyA65laX25e6zcQ0H5RLPWRMqj7z0z07hEY";
    private GoogleMap mMap;
    private Button button;
    protected DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FloatingActionButton fab;
    private String TAG = MapsActivity.class.toString();
    public String subjectKey;
    private Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        subjectKey = getIntent().getStringExtra("dbkey");
        final DatabaseReference postRef = mDatabase.child("subject").child(subjectKey);
        setContentView(R.layout.activity_map);
        //获取定位服务
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = findViewById(R.id.mapView2);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
        fab = findViewById(R.id.fabRank);
        fab.setOnClickListener(view -> {
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.mapView2, RankFragment.newInstance(subjectKey));
//                fragmentTransaction.commit();
            Intent intent = new Intent();
            intent.setClass(MapsActivity.this, ScrollingActivity.class);
            intent.putExtra("subject", subjectKey);
            startActivity(intent);
            RankViewHolder.rank_increment = 1;
        });

        button = findViewById(R.id.checkInButton);
        button.setOnClickListener(view -> postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Subject subject = mutableData.getValue(Subject.class);
                if (subject == null) {
                    return Transaction.success(mutableData);
                }


//                        LatLng target = new LatLng(building.getLatitude(), building.getLongitude());
//                        mMap.addMarker(new MarkerOptions().position(target).title(subject.getName()));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(target));
                Map<String, User> students = subject.getStudents();
                Date currentTime = Calendar.getInstance().getTime();
                Log.d(TAG, "doTransaction: " + currentTime);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                String today = sdf.format(currentTime);
                User user = students.get(getUid());
                if (today.equals(user.date)) {
                    makeToast("Fail! Repetitive check in!");
                    return Transaction.abort();
                }

                double lat = 0;
                double log = 0;
                boolean rightTime = false;
                Map<String, Session> sessionMap = subject.getSessions();
                for (String key : sessionMap.keySet()) {
                    Session session = sessionMap.get(key);
                    Building building = session.building;
                    int start = session.start;
                    int end = session.end;
                    int day = session.day;
                    int currenthour = currentTime.getHours();
                    int currentday = currentTime.getDay();

                    LatLng target = new LatLng(building.getLatitude(), building.getLongitude());
//                            mMap.addMarker(new MarkerOptions().position(target).title(subject.getName()));
                    float[] distance = new float[3];
                    Location.distanceBetween(building.latitude, building.longitude, lastLocation.getLatitude(), lastLocation.getLongitude(), distance);
                    if (currentday == day) {
                        lat = building.latitude;
                        log = building.longitude;
                        if (currenthour >= start && currenthour <= end) {
                            rightTime = true;
                            if (distance[0] < 100) {
                                user.count += 1;
                                user.date = today;
                                students.put(getUid(), user);
                                mutableData.setValue(subject);
                                makeToast("Successfully checked in!");
                                return Transaction.success(mutableData);
                            }
                        }
                    }
                }
                if (lat == 0 && log == 0) {
                    makeToast("Fail! No class today!");
                    return Transaction.abort();
                }
                if (!rightTime) {
                    makeToast("Fail! Not in class time!");
                    return Transaction.abort();
                }
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?"
                                + "saddr=" + lastLocation.getLatitude() + "," + lastLocation.getLongitude()
                                + "&daddr=" + lat + "," + log
                                + "&avoid=highway"
                        )
                );

                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
                makeToast("Fail! Location too far away!");
                return Transaction.abort();
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);

            }
        }));
    }

    public void makeToast(final String msg) {
        MapsActivity.this.runOnUiThread(() -> Toast.makeText(MapsActivity.this, msg, Toast.LENGTH_SHORT).show());
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
//            mMarker = mMap.addMarker(new MarkerOptions().position(loc));
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            }
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                //                // this thread waiting for the user's response! After the user
                //                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        3);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取当前可用的位置控制器
        List<String> list = locationManager.getProviders(true);

        if (list.contains(LocationManager.GPS_PROVIDER)) {
            //是否为GPS位置控制器
            provider = LocationManager.GPS_PROVIDER;
        } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            //是否为网络位置控制器
            provider = LocationManager.NETWORK_PROVIDER;

        } else {
            Toast.makeText(this, "Network or GPS disabled",
                    Toast.LENGTH_LONG).show();
            return;
        }
        lastLocation = locationManager.getLastKnownLocation(provider);

//绑定定位事件，监听位置是否改变
//第一个参数为控制器类型第二个参数为监听位置变化的时间间隔（单位：毫秒）
//第三个参数为位置变化的间隔（单位：米）第四个参数为位置监听器
        locationManager.requestLocationUpdates(provider, 2000, 2,
                locationListener);
        LatLng sydney = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
//        mMap.addMarker(new MarkerOptions().position(sydney).title("current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }


    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}
