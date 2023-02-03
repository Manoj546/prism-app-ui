package com.example.myapplication;

import static android.app.Service.START_NOT_STICKY;
import static android.view.View.GONE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private TextView textView2;
    private ListView listView;
    private WifiManager wifiManager;
    private Button buttonScan;
    private int size=0;
    private List<ScanResult> results;
    private List<ScanResult> resultscnnect;

    private ArrayList<String> arrayList=new ArrayList<>();
    private ArrayAdapter adapter;
    CSVFileWriter csv;
    StringBuffer filePath;
    File file;


    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                PackageManager.PERMISSION_GRANTED);
        //textView = findViewById(R.id.textView);
        //buttonScan=findViewById(R.id.scanBtn);
        filePath = new StringBuffer();
        filePath.append("/Device storage/abc.csv");
        file = new File(filePath.toString());

        csv = new CSVFileWriter(file);



        new Thread(new Runnable() {
            @Override
            public void run() {

                Looper.prepare();

                while (true) {
                    Log.d("Test","00000000000000");

                    scanWifi();

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


        //listView=findViewById(R.id.wifiList);
        wifiManager=(WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,arrayList);
        //listView.setAdapter(adapter);
    }

    private void scanWifi(){
        arrayList.clear();
        registerReceiver(wifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this,"Scanning wi-fi...", Toast.LENGTH_SHORT).show();
    }


    BroadcastReceiver wifiReceiver=new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
            List<String> value = new ArrayList<String>();
            results=wifiManager.getScanResults();
             WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            FileWriter sb= null;




//            System.out.print("AJIT  " + results);
//            arrayList.add(" dd  " + connectivityManager);
//            textView.add(results)
                unregisterReceiver(this);

                for (ScanResult scanResult : results) {
//                 int level=wifiManager.calculateSignalLevel(scanResult.level,10);
                    int rssi = scanResult.level;
                    int level = wifiManager.calculateSignalLevel(rssi, 5);
                    String ssid = scanResult.SSID;

//                 int rssi = wifiManager.getConnectionInfo().getRssi();
//                 int level = wifiManager.calculateSignalLevel(rssi, 5);
                    arrayList.add("\n SSID: " + ssid);
//                 int percentage = (int) ((level/10.0)*100);
//                 int freq=scanResult.frequency;
                    String bssid = scanResult.BSSID;
                    int freq = scanResult.frequency;
                    int freq_bonus = 0;
                    if ((freq >= 2412 && freq <= 2423) || (freq >= 2437 && freq <= 2448) || (freq >= 2462 && freq <= 2473)) {
                        freq_bonus = 2;
                    }
                    String capabilities = scanResult.capabilities;
                    int channel_bandwidth = scanResult.channelWidth;
                    int link_speed = wifiInfo.getLinkSpeed();
                    int secured = 0;
                    boolean metered = true; // not implemented
                    if (capabilities.contains("WPA") || capabilities.contains("WEP") || capabilities.contains("WPS")) {
                        secured = 2;
                    }
                    double ScoreBoost = (freq_bonus + channel_bandwidth + secured) + ((double) link_speed / 10.0);


                    arrayList.add("\n rssi: " + rssi + "\n Base Score :  " + level + " out of 5 \n"+"\n freq : " + freq + "\n linkSpeed : " + link_speed +
                            "\n secured : " + secured + "\n ChannelWidth : " + channel_bandwidth +
                            "\n Score Boost: " + ScoreBoost + "Out of 15");

                    expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
                    value.add("Rssi: "+String.valueOf(rssi));
                    value.add("Base score: "+String.valueOf(level));
                    value.add("Frequency: "+String.valueOf(freq));
                    value.add("LinkSpeed: "+String.valueOf(link_speed));
                    value.add("Secured: "+String.valueOf(secured));
                    value.add("Channel Bandwidth: "+String.valueOf(channel_bandwidth));
                    value.add("Score Boost: "+String.valueOf(ScoreBoost));


                    expandableListDetail.put(ssid, value);


//                 sb.append(ssid.toString() );
//                 sb.append(rssi.toString());
//                 sb.append(freq.toString());
//                 sb.append(link_speed.toString());
//                 sb.append(secured.toString());
//                 sb.append(channel_bandwidth.toString());
//                 sb.append(level.toString());
//                 sb.append(ScoreBoost.toString());
//                 sb.append("\n");


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        int centerfreq = scanResult.centerFreq0;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            int channelWidth = scanResult.channelWidth;
//                     arrayList.add(scanResult.SSID + "\t - \t" + percentage + "% \t\t\t" + level2 + "/10" + "\n frequency=" + freq + "\nchannel width=" + channelWidth+ "\n center freq"+centerfreq + "AJIT \n");
                            arrayList.add("********************************");
                            adapter.notifyDataSetChanged();
                        }
                    }
                    //
                    String FILENAME="wifi_details9.csv";
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        String entry=ssid+ "," + rssi+ ","+freq+ ","+ link_speed+","+secured+","+channel_bandwidth+","+level+","+ScoreBoost+"\n";

                        try {
                            FileOutputStream out= openFileOutput(FILENAME,Context.MODE_APPEND);
                            out.write(entry.getBytes());

                            out.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        csv.writeHeader("hi");
                    }
                }
            expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
            expandableListAdapter = new CustomExpandableListAdapter(context, expandableListTitle, expandableListDetail);
            expandableListView.setAdapter(expandableListAdapter);
}
        };


    public void buttonGetSSID(View view) {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo;

        wifiInfo = wifiManager.getConnectionInfo();
//        String ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        int linkSpeed = wifiManager.getConnectionInfo().getRssi();
        int rssi = wifiManager.getConnectionInfo().getRssi();
        int level = WifiManager.calculateSignalLevel(rssi, 10);
        int level2 = WifiManager.calculateSignalLevel(rssi, 5);

        int percentage = (int) ((level / 10.0) * 100);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                textView.setText("SSID is:"+wifiInfo.getSSID() + "\n IP Address:" + wifiInfo.getIpAddress() + "\n MAC Address is:" + wifiInfo.getMacAddress()+"\n The tx link speed is :"+wifiInfo.getTxLinkSpeedMbps()
//                +"\n The rx link speed is : "+wifiInfo.getRxLinkSpeedMbps()
//                          );
                //textView.setText("HOLA");
            }

        }

    };