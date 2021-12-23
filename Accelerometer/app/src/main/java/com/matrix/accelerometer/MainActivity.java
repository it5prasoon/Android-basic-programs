package com.matrix.accelerometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private float circleX;
    private float circleY;
    private int circleRadius = 30;
    private CanvasView canvas;

    private Timer timer;
    private Handler canvasHandler;

    private SensorManager sensorManager;
    private Sensor accSensor;
    private float sensorX;
    private float sensorY;
    private float sensorZ;
    private RelativeLayout relativeLayout;

    private long lastSensorUpdateTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);

        relativeLayout = findViewById(R.id.rel_layout);


        int screenWidth = size.x;
        int screenHeight = size.y;

        circleX = screenWidth / 2 - circleRadius + 200;
        circleY = screenHeight / 2 - circleRadius;

        canvas = new CanvasView(MainActivity.this);
        setContentView(canvas);

        canvasHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                canvas.invalidate();
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (sensorX < 0) {
                    circleX -=10;
                } else {
                    circleX +=10;
                }
                canvasHandler.sendEmptyMessage(0);
            }
        }, 0, 100);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {


            long currentTime = System.currentTimeMillis();

            if (currentTime - lastSensorUpdateTime > 100) {
                sensorX = event.values[0];
                sensorY = event.values[1];
                sensorZ = event.values[2];
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }


    private class CanvasView extends View {
        private Paint pen;

        public CanvasView(Context context) {
            super(context);
            setFocusable(true);

            pen = new Paint();
        }

        public void onDraw(Canvas screen) {
            pen.setStyle(Paint.Style.FILL);
            pen.setAntiAlias(true);
            pen.setTextSize(30f);
            pen.setColor(Color.parseColor("#FF0000"));
            screen.drawCircle(circleX, circleY, circleRadius, pen);
        }
    }
}