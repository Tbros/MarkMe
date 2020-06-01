package com.rna.markme.teacher;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rna.markme.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GetAttendance extends AppCompatActivity {
    Map<String, Integer> userSorted;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<DataModel> presentStudents = new ArrayList<>();
    DataAdapter adapter;
    DataModel dataModel;
    ListView lv;
    TextView count;
    WifiManager wifiManager;
    String lectureTag, email, teacherID, attString = "";
    boolean connected = false;
    Button saveAtt;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_attendance);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        saveAtt = findViewById(R.id.saveAtt);
        count = findViewById(R.id.count);
        count.setVisibility(View.GONE);
        email = user.getEmail();
        teacherID = email.substring(0, email.length() - 10);
        Intent intent = getIntent();
        lectureTag = intent.getStringExtra("subTag");
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        adapter = new DataAdapter(this, presentStudents, R.layout.listview_item);
        lv = (ListView) findViewById(R.id.data);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String reg = presentStudents.get(position).getRegno();

            }
        });
        getData(teacherID, lectureTag);
        saveAtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!attString.equals("")) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(GetAttendance.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
                    } else {
                        saveAttendance(lectureTag, attString);
                    }

                } else {
                    Toast.makeText(GetAttendance.this, "No Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void refresh(View view) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        count.setVisibility(View.GONE);
        presentStudents.clear();
        adapter.clear();
        getData(teacherID, lectureTag);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, TeacherAccount.class));
        finishAffinity();
    }

    public void getData(String teacherID, String lectureTag) {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else {

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            connected = false;
        }
        if (connected == true) {
            db.collection(teacherID).document(lectureTag).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        mLoadingIndicator.setVisibility(View.GONE);
                        count.setVisibility(View.VISIBLE);
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> user = new HashMap<>();
                            HashMap<String, Integer> userInt = new HashMap<>();
                            user = document.getData();
                            for (Map.Entry<String, Object> entry : user.entrySet()) {
                                if (entry.getValue() instanceof String) {
                                    if (!entry.getKey().equals("Bssid")) {
                                        userInt.put(entry.getKey(), Integer.parseInt(entry.getValue().toString()));
                                    }
                                }
                            }

                            addDummyData(userInt);
                            userSorted = sortByValue(userInt);
                            attString = "";
                            int studentCount = 0;
                            for (String s : userSorted.keySet()) {
                                if (userSorted.get(s) > 36)
                                    dataModel = new DataModel(s.toUpperCase(), userSorted.get(s), true);
                                else
                                    dataModel = new DataModel(s.toUpperCase(), userSorted.get(s), false);
                                //presentStudents.add(s + "--" + user.get(s));
                                presentStudents.add(dataModel);
                                attString += s.toUpperCase() + "\n";
                                studentCount++;

                            }
                            String text="Student Count: " + studentCount;
                            ForegroundColorSpan cl=new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary));
                            SpannableString ss=new SpannableString(text);
                            ss.setSpan(cl,15,text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            count.setText(ss);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(GetAttendance.this, "No such document", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(GetAttendance.this, "Failed : Try Again", Toast.LENGTH_SHORT).show();

                    }

                }
            });
        } else {
            Toast.makeText(this, "Please Turn on your Internet", Toast.LENGTH_SHORT).show();
        }
    }


    public void saveAttendance(String filename, String content) {
        String fileName = filename + ".txt";

        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "MarkMe");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            File file = new File(folder, fileName);

            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(content.getBytes());
                fos.close();
                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "FileNotFound", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error saving!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error saving!", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveAttendance(lectureTag, attString);// <-- Start Beemray here
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
// Permission was denied or request was cancelled
            }
        }
    }

    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
        Collections.reverse(list);
        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public void addDummyData(Map<String,Integer> user){
        user.put("RA1611003030124",34);
        user.put("RA1611003030125",27);
        user.put("RA1611003030126",20);
        user.put("RA1611003030127",36);
        user.put("RA1611003030128",45);
        user.put("RA1611003030129",41);
        user.put("RA1611003030130",38);
        user.put("RA1611003030131",48);
        user.put("RA1611003030132",5);
        user.put("RA1611003030133",33);
        user.put("RA1611003030135",2);
        user.put("RA1611003030136",9);
        user.put("RA1611003030137",29);
        user.put("RA1611003030138",28);
        user.put("RA1611003030139",10);
        user.put("RA1611003030140",12);
        user.put("RA1611003030141",32);
        user.put("RA1611003030142",30);
        user.put("RA1611003030143",29);
        user.put("RA1611003030144",1);
        user.put("RA1611003030145",18);
        user.put("RA1611003030146",30);
        user.put("RA1611003030147",17);
        user.put("RA1611003030148",25);
        user.put("RA1611003030149",22);
        user.put("RA1611003030150",16);
        user.put("RA1611003030151",19);


    }

}
