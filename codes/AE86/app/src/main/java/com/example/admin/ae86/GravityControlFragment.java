package com.example.admin.ae86;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by 123 on 2016/12/1.
 */

public class GravityControlFragment extends Fragment {
    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private SensorEventListener gravityListener;

    private TextView txt_gravity;
    private int x,y;
    private String msg;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View gravityLayout = inflater.inflate(R.layout.gravityfragment,
                container, false);

        txt_gravity = (TextView) gravityLayout.findViewById(R.id.txt_gravity_y);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravityListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                x = (int) event.values[SensorManager.DATA_X];
                y = (int) event.values[SensorManager.DATA_Y];

                if (x < 5 && x > -5 && y > -5 && y < 5) {
                    txt_gravity.setText("停止");
                    msg = "s";
                    onListener_gravity.on_gravity(msg);
                }
                if (x < -5 && y > -5 && y < 5) {
                    txt_gravity.setText("右转");
                    msg = "r";
                    onListener_gravity.on_gravity(msg);
                }
                if (x > 5 && y > -5&& y < 5) {
                    txt_gravity.setText("左转");
                    msg = "l";
                    onListener_gravity.on_gravity(msg);
                }
                if (x < 5 && x > -5 && y < -5)
                {
                    txt_gravity.setText("前进");
                    msg = "f";
                    onListener_gravity.on_gravity(msg);
                }
                if(x < 5 && x > -5 && y > 5)
                {
                    txt_gravity.setText("后退");
                    msg = "b";
                    onListener_gravity.on_gravity(msg);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(gravityListener, gravitySensor, SensorManager.SENSOR_DELAY_GAME);

        return gravityLayout;
    }
    public interface OnListener_Gravity {
        public void on_gravity(String ButtonText);
    }
    private OnListener_Gravity onListener_gravity;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try {
            onListener_gravity = (OnListener_Gravity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement OnButtonClickedListener");
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        sensorManager.unregisterListener(gravityListener);
    }
}
