package com.laha.vdkmai.player;

import android.Manifest;
import android.content.Context;
import android.graphics.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.Console;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private MediaPlayer player;
    private int grantResults;
    private Timer playTimer;
    private TimerTask playTimerTask;
    private TimerTask stopTimerTask;

    private Handler handler = new Handler();
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private CameraManager mCameraManager;
    private PowerManager.WakeLock mWakeLock;



    private Runnable stopPlayer = new Runnable() {
        @Override
        public void run() {
        /* do what you need to do */
            player.stop();
        /* and here comes the "trick" */
            handler.postDelayed(this, 1000 * 3600 * 24); // 1 day
        }
    };

    private Runnable startPlayer = new Runnable() {
        @Override
        public void run() {
        /* do what you need to do */
            player.start();
        /* and here comes the "trick" */
            handler.postDelayed(this, 1000 * 3600 * 24); // 1 day
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        //handler.postDelayed(runnable, 100);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //String filePath = Environment.getExternalStorageDirectory().getPath()+ "/New Folder/35/file.mp3";
        String filePath = Environment.getExternalStorageDirectory().getPath()+ "/Music/Calming-Music-Nature-Sounds-Zen-Music.mp3";
        Log.d("KHOA", filePath);

        try {

            player = new MediaPlayer();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, grantResults);

            player.setDataSource(filePath);
            player.prepare();
            player.setLooping(true);
            player.start();
            Log.d("KHOA", "Started!");

            ///////////////////////////////
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

            /*
            List<Sensor> msensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
            for(int i=0; i<msensorList.size(); i++)
            {
                Log.d("Khoa",msensorList.get(msensorList.size()-1-i).getName());
            }
            */

            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            Log.d("KHOA", mSensor.toString());

            mWakeLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");

       } catch (IOException e) {
            e.printStackTrace();
            Log.d("KHOA",e.toString());
        }

    }




    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("KHOA", "onSensorChanged " + event.values[0]);
        if(event.values[0] > 0)
        {
            player.pause();
            Log.d("KHOA", "Paused!");
            mWakeLock.release();
            Log.d("KHOA", "Screen Off!");
        }
        else
        {
            player.start();
            Log.d("KHOA", "Started!");
            mWakeLock.acquire();
            Log.d("KHOA", "Screen On!");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("KHOA", "onAccuracyChanged");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("KHOA 2", mSensor.toString());
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mSensorManager.unregisterListener(this);
    }
}
