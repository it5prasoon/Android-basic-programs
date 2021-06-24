package com.matrix.detectmotion;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // create variables of the two class
    private Accelerometer accelerometer;
    private Gyroscope gyroscope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // instantiate them with this as context
        accelerometer = new Accelerometer(this);
        gyroscope = new Gyroscope(this);

        // create a listener for accelerometer
        accelerometer.setListener(new Accelerometer.Listener() {
            //on translation method of accelerometer
            @Override
            public void onTranslation(float tx, float ty, float ts) {
                // set the color red if the device moves in positive x axis
                if (tx > 1.0f) {
                    getWindow().getDecorView().setBackgroundColor(Color.RED);
                }
                // set the color blue if the device moves in negative x axis
                else if (tx < -1.0f) {
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                }
            }
        });

        // create a listener for gyroscope
        gyroscope.setListener(new Gyroscope.Listener() {
            // on rotation method of gyroscope
            @Override
            public void onRotation(float rx, float ry, float rz) {
                // set the color green if the device rotates on positive z axis
                if (rz > 1.0f) {
                    getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                }
                // set the color yellow if the device rotates on positive z axis
                else if (rz < -1.0f) {
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                }
            }
        });
    }

    // create on resume method
    @Override
    protected void onResume() {
        super.onResume();

        // this will send notification to
        // both the sensors to register
        accelerometer.register();
        gyroscope.register();
    }

    // create on pause method
    @Override
    protected void onPause() {
        super.onPause();

        // this will send notification in
        // both the sensors to unregister
        accelerometer.unregister();
        gyroscope.unregister();
    }
}
