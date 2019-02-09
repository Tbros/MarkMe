package com.rna.markme.student;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.rna.markme.R;
import com.rna.markme.TouchIdAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentInterfaceActivity extends AppCompatActivity implements View.OnClickListener {
    WifiManager wifi;
    ListView lv;
    TextView textStatus;
    EditText teachid,subjectTag;
    Button buttonScan;
    String id,subTag,s;
    List<ScanResult> results;
    String email;
    WifiInfo info;
    ArrayList<String> arraylist = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayAdapter adapter;
    boolean b = false;
    boolean connected = false;
    static public final int REQUEST_LOCATION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_interface);
        teachid = (EditText) findViewById(R.id.teachid);
        subjectTag = (EditText) findViewById(R.id.subjectTag);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();
        email = email.substring(0, email.length() - 10);
        buttonScan = (Button) findViewById(R.id.scan);
        buttonScan.setOnClickListener(this);
        lv = (ListView) findViewById(R.id.wifilist);
        textStatus = (TextView) findViewById(R.id.textStatus);
        lv.setVisibility(View.GONE);


        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false) {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
        info = wifi.getConnectionInfo();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arraylist);
        lv.setAdapter(adapter);
        //scanWifiNetworks();


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, StudentMainActivity.class));
        finishAffinity();
    }

    public String check(String bssid) {
        for (String string : arraylist) {
            if (string.equals(bssid)) {
                b = true;
                //mark(findViewById(R.id.MARK));
                return "Found in Class \uD83D\uDC4D\uD83C\uDFFB";
            }
        }
        b=false;
        return "Not Found in Class";
    }

    public void mark(View view) {
        id = teachid.getText().toString();
        subTag=subjectTag.getText().toString();
        if (TextUtils.isEmpty(id)||TextUtils.isEmpty(subTag)) {
            Toast.makeText(this, "Enter teacher id && Lecture-TAG", Toast.LENGTH_SHORT).show();
        } else {
            if ( b==true) {
                Intent intent = new Intent(StudentInterfaceActivity.this,TouchIdAuth.class);
                intent.putExtra("emailp",email);
                intent.putExtra("idp",id);
                intent.putExtra("subTagp",subTag);
                startActivity(intent);
            }
// {
//                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
//                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
//                    //we are connected to a network
//                    connected = true;
//                } else
//                    connected = false;
//                if (connected == true) {
//                    Toast.makeText(this, "Wait your Attendance is being marked", Toast.LENGTH_SHORT).show();
//                    final Map<String, Object> user = new HashMap<>();
//                    user.put(email, true);
//                    db.collection(id).document(subTag).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            if (task.isSuccessful()) {
//                                DocumentSnapshot document = task.getResult();
//                                if (document.exists()) {
//                                    db.collection(id).document(subTag).set(user,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            Toast.makeText(StudentInterfaceActivity.this, "Attendance Marked", Toast.LENGTH_SHORT).show();
//
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Toast.makeText(StudentInterfaceActivity.this, "Faliure: Try Again", Toast.LENGTH_SHORT).show();
//
//                                        }
//                                    });
//                                } else {
//                                    Toast.makeText(StudentInterfaceActivity.this, "No such document", Toast.LENGTH_SHORT).show();
//
//                                }
//                            }
//                            else{
//                                Toast.makeText(StudentInterfaceActivity.this, "Faliure: Try Again", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                } else {
//                    Toast.makeText(this, "Please Turn on your Internet", Toast.LENGTH_SHORT).show();
//                }
//            }
                else {
                Toast.makeText(this, "You are not inside class", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void onClick(View view) {
        textStatus.setText("wait...");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
        }else{
            teachid.setFocusable(false);
            subjectTag.setFocusable(false);
            scanWifiNetworks();
        }

    }

    private void scanWifiNetworks() {

        arraylist.clear();
        registerReceiver(wifi_receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifi.startScan();

        Log.d("WifScanner", "scanWifiNetworks");

        Toast.makeText(this, "Scanning....", Toast.LENGTH_SHORT).show();

    }

    BroadcastReceiver wifi_receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context c, Intent intent) {
            Log.d("WifScanner", "onReceive");
            results = wifi.getScanResults();
            unregisterReceiver(this);

            for (ScanResult scanResult : results) {
                arraylist.add(scanResult.BSSID);
                adapter.notifyDataSetChanged();
            }

            textStatus.setText(check(getBssid()));


        }
    };

    public String getBssid(){

        id = teachid.getText().toString();
        subTag=subjectTag.getText().toString();
        if (TextUtils.isEmpty(id)||TextUtils.isEmpty(subTag)) {
            Toast.makeText(this, "Enter teacher id && Lecture-TAG", Toast.LENGTH_SHORT).show();
        } else {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                } else
                    connected = false;
                if (connected == true) {
                    db.collection(id).document(subTag).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    s=document.getString("Bssid");

                                } else {
                                    Toast.makeText(StudentInterfaceActivity.this, "No such document", Toast.LENGTH_SHORT).show();

                                }
                            }
                            else{
                                Toast.makeText(StudentInterfaceActivity.this, "Faliure: Try Again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Please Turn on your Internet", Toast.LENGTH_SHORT).show();
                }


        }
        return s;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scanWifiNetworks();// <-- Start Beemray here
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
// Permission was denied or request was cancelled
            }
        }
    }
}
