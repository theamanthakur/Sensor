package com.ttl.sensor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LeanActivity extends AppCompatActivity implements SensorEventListener {
    private float x = 0, y = 0, z = 0, x1 = 0;
    DatabaseReference ref;

    /*
        array of floats to hold readings of accelerometer and
        magnetic field sensor
     */
    private float[] accelReadings, magReadings;

    /*
        Declaring variables for the sensor manager, the
        accelerometer, and the magnetic field sensor.
     */
    private SensorManager mSensorManager;
    Sensor accelerometer, magnetometer;

    /*
        Textviews to show the measured angles
     */
    TextView leanLeft, yValue, zValue, leanRight;

    //String to hold the unicode degree symbol
    String degreeSymbol = "\00B0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximate);

        ref = FirebaseDatabase.getInstance().getReference("Sensor");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //Textviews which show the values
        leanLeft = (TextView) findViewById(R.id.pitchValue);
        yValue = (TextView) findViewById(R.id.rollValue);
        zValue = (TextView) findViewById(R.id.azimuthValue);
        leanRight = (TextView) findViewById(R.id.leanRightVal);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        super.onResume();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accelReadings = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            magReadings = event.values;
        if (magReadings != null && accelReadings != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, accelReadings, magReadings);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                z = (float) Math.toDegrees(orientation[0]);
                x = (float) Math.toDegrees(orientation[1]);
                x1 = (float) Math.toDegrees(orientation[1]);
                y = (float) Math.toDegrees(orientation[2]);

                /*
                    I want the angles to be shown with respect
                    to the positive axis, so I need to do a little math here.

                    If the angle is negative, I will take the absolute value of it
                    and subtract it from 360. This way, it shows the equivalent angle
                    measured
                 */
                if (x < 0) {
                    x = Math.abs(x);
                    float x2 = Math.abs(x);  //here the value can be negative of angle so taking absolute
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (x2 >= 20) {     // to send data to firebase
                                String st = Float.toString(x2);
                                AddData(st);

                            }

                        }
                    }, 1000);



                }
                if (x1 > 90){
                    if (x1 < 0) {
                        x1 = Math.abs(x1); // this is to keep lean between 0-90 degree.
                    }
                    x1 = 360 - x1;
                }

                if (y < 0) {
                    y = 360 - Math.abs(y); //calculating roll value
                }

                if (z < 0) {
                    z = 360 - Math.abs(z);  //calculating azimuth value
                }
                //x1 gets negative in right tilt and positive in left tilt

                leanLeft.setText(Float.toString(x) + " \u00B0");
                yValue.setText(Float.toString(y) + " \u00B0");
                zValue.setText(Float.toString(z) + " \u00B0");
                leanRight.setText(Float.toString(x1) + " \u00B0");
            }
        }
    }

    private void AddData(String x) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(timestamp,x);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ref.child("Lean").push().setValue(hashMap);
                Toast.makeText(LeanActivity.this, "In delay function", Toast.LENGTH_SHORT).show();

            }
        }, 1000);
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}