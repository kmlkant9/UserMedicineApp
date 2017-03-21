package com.example.user.medicobuddy;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.example.user.medicobuddy.R;
import com.google.firebase.FirebaseApp;


public class home extends Fragment implements AdapterView.OnItemSelectedListener {

    public home() {
        // Required empty public constructor
    }

    TextView med_text;
    private static final String TAG = MainActivity.class.getSimpleName();
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    MapView mMapView;
    private GoogleMap googleMap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        med_text = (TextView) view.findViewById(R.id.medicine);

        mFirebaseInstance = FirebaseDatabase.getInstance();

        final Spinner spinner = (Spinner) view.findViewById(R.id.spinner);

        mMapView = (MapView) view.findViewById(R.id.mapView);

        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mFirebaseDatabase = mFirebaseInstance.getReference("Medicine");
                final String med = spinner.getSelectedItem().toString();



                mFirebaseInstance.getReference(med).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Query query=mFirebaseDatabase.child(med).orderByChild("Price");
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for(DataSnapshot med : dataSnapshot.getChildren()){
                                        mMapView.getMapAsync(new OnMapReadyCallback() {
                                            @Override
                                            public void onMapReady(GoogleMap mMap) {
                                                googleMap = mMap;

                                                // For showing a move to my location button
                                                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                                                        != PackageManager.PERMISSION_GRANTED
                                                        &&
                                                        ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                                                                != PackageManager.PERMISSION_GRANTED) {
                                                    // TODO: Consider calling
                                                    //    ActivityCompat#requestPermissions
                                                    // here to request the missing permissions, and then overriding
                                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                    //                                          int[] grantResults)
                                                    // to handle the case where the user grants the permission. See the documentation
                                                    // for ActivityCompat#requestPermissions for more details.
                                                    return;
                                                }
                                                googleMap.setMyLocationEnabled(true);

                                                // For dropping a marker at a point on the Map

                                                LatLng sydney = new LatLng(-34, 151);
                                                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

                                                // For zooming automatically to the location of the marker
                                                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                            }
                                        });
                                        Toast.makeText(getActivity(),med.getValue().toString(),Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.medicine_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}