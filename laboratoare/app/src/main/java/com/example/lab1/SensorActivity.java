package com.example.lab1;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class SensorActivity extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private List<Sensor> deviceSensors;
    TextView txtList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        txtList = (TextView) findViewById(R.id.sensorslist);

        if (deviceSensors.size() > 0) {
            for (Sensor s : deviceSensors) {
                txtList.append("\n" + s.getName() + "\n" + s.getVendor());
            }
        }

    }

    protected void onResume() {
        super.onResume();
        for (Sensor s : deviceSensors) {
            mSensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
    }
}
