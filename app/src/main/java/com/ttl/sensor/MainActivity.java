package com.ttl.sensor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private Sensor sensor;
    TextView text1, text2, text3;
    Button btnStart, btnStop, btnLean;
    DatabaseReference db;

    private double pervAccValue;
    private double curAccValue;

    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];

    long time1,time2;


    private static final String TAG = "MainActivity";

    DatabaseHelper mDatabaseHelper;

    boolean a = true;

    private SensorEventListener sensorEventListener = new SensorEventListener(){

        @Override
        public void onSensorChanged(SensorEvent event) {
            final  float alpha = 0.8f;
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            curAccValue = Math.sqrt((x*x + y*y + z*z));

            pervAccValue = curAccValue;
            double change = Math.abs((curAccValue-pervAccValue));
//
//            if (curAccValue>=11){
//                text3.setText("Z is " + change);
//            }
//
//            text1.setText("X is " + curAccValue);
//            text2.setText("Y is " + y);
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];


            text1.setText("X is " + linear_acceleration[0]);
            text3.setText("Z is  :  "+linear_acceleration[2]);
            if (linear_acceleration[0] >= 2) {
//                text1.setText("X is " + linear_acceleration[0]);
            }
            if (linear_acceleration[1] >= 1 ) {
//y axis

                text2.setText("Y is " + linear_acceleration[1]);
                // orientation of phone should while using accelerometer sensor

//                do {
//
//                    i++;
//                    long time = System.currentTimeMillis();
//                    String st = Long.toString(time);
//                    Toast.makeText(MainActivity.this, ""+st, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(MainActivity.this, ""+i, Toast.LENGTH_SHORT).show();
//                }
//                while (linear_acceleration[1] <=1);
                AddData();
//                onPause();
//                long time = System.currentTimeMillis();
//                String st = String.valueOf(time);
//                Toast.makeText(MainActivity.this, ""+st, Toast.LENGTH_SHORT).show();
//                AddData(st);
//                Calendar dateTimeInUtc = new Ca( "2011-07-19T18:23:20+0000", DateTimeZone.UTC );
//                long secondsSinceUnixEpoch = ( dateTimeInUtc.getMillis() / 1000 );
//                time1 = System.currentTimeMillis();
//                postTime(time);


            }
            if (linear_acceleration[1] >= 2){
//                time2 = System.currentTimeMillis();
//                long time = (time2 - time1);
//                text3.setText("Z is  :  "+linear_acceleration[2]);
            }



        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

//    private void call() {
//
//        AddData(st);
//    }

//    private void postTime(long time) {
//        db.push().setValue(time);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnLean = findViewById(R.id.btnLean);

//        FirebaseApp.initializeApp(this);
//
//
//        db = FirebaseDatabase.getInstance().getReference().child("Sensor");

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, proximateActivity.class);
//                startActivity(intent);
                onResume();
                //sensorManager.registerListener(sensorEventListener , sensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        });
        btnLean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LeanActivity.class);
                startActivity(intent);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
            }
        });

    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener , sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
    }

    ////////////////////DATABASE**************-------------/////////////-------------***************
// in form of table
    public void AddData() {

//        GPSTracker gpsTracker = new GPSTracker(this);
//
//        if (gpsTracker.getIsGPSTrackingEnabled()) {
//            String stringLatitude = String.valueOf(gpsTracker.latitude);
////            textview = (TextView) findViewById(R.id.fieldLatitude);
////            textview.setText(stringLatitude);
//
//            String stringLongitude = String.valueOf(gpsTracker.longitude);
////            textview = (TextView) findViewById(R.id.fieldLongitude);
////            textview.setText(stringLongitude);
//        }else {
//            Toast.makeText(this, "Error, check Location Services", Toast.LENGTH_SHORT).show();
//        }
        long time = System.currentTimeMillis();
        String st = Long.toString(time);
        final DatabaseHelper helper = new DatabaseHelper(this);


        if (!st.isEmpty()) {
            if (a) {
                if (helper.insert(time)) {
                    a = false;
                    Toast.makeText(MainActivity.this, "Inserted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "NOT Inserted", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}