package com.example.admin.ae86;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by 123 on 2016/12/18.
 */

public class GestureControlFragment extends Fragment {
    private int x1;
    private int x2;
    private int y1;
    private int y2;
    private String msg;


    private TextView txt_gesture_show;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.gesturefragment, container, false);
        txt_gesture_show = (TextView)view.findViewById(R.id.txt_gesture_show);
        txt_gesture_show.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch(e.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x1 = (int)e.getX();
                        y1 = (int)e.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = (int)e.getX();
                        y2 = (int)e.getY();
                        if(y1 - y2 > 100) {
                            txt_gesture_show.setText("前进");
                            msg = "f";
                            onListener_gesture.on_gesture(msg);
                        } else if(y2 - y1 > 100) {
                            txt_gesture_show.setText("后退");
                            msg = "b";
                            onListener_gesture.on_gesture(msg);
                        } else if(x1 - x2 > 100) {
                            txt_gesture_show.setText("左转");
                            msg = "l";
                            onListener_gesture.on_gesture(msg);
                        } else if(x2 - x1 > 100) {
                            txt_gesture_show.setText("右转");
                            msg = "r";
                            onListener_gesture.on_gesture(msg);
                        } else if(((x1-x2)<10||(x2-x1)<10) && ((y1-y2)<10||(y2-y1)<10)){
                            txt_gesture_show.setText("停止");
                            msg = "s";
                            onListener_gesture.on_gesture(msg);
                        }
                        break;
                }
                return true;
            }
        });
        return view;
    }

    public interface OnListener_Gesture {
        public void on_gesture(String ButtonText);
    }

    private OnListener_Gesture onListener_gesture;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try {
            onListener_gesture = (OnListener_Gesture) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement OnButtonClickedListener");
        }
    }
}
