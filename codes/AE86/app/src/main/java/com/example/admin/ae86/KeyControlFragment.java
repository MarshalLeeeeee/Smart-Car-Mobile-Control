package com.example.admin.ae86;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by 123 on 2016/12/1.
 */

public class KeyControlFragment extends Fragment {

    private Button forward;
    private Button back;
    private Button stop;
    private Button left;
    private Button right;

    private String msg;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View keyControlLayout = inflater.inflate(R.layout.keycontrolfragment, container, false);

        forward=(Button) keyControlLayout.findViewById(R.id.forward);
        forward.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                msg = "f";
                onButtonClickedListener_control.onButtonClicked_control(msg);
            }});
        back=(Button) keyControlLayout.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                msg = "b";
                onButtonClickedListener_control.onButtonClicked_control(msg);
            }});
        stop=(Button) keyControlLayout.findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                msg = "s";
                onButtonClickedListener_control.onButtonClicked_control(msg);
            }});
        left=(Button) keyControlLayout.findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                msg = "l";
                onButtonClickedListener_control.onButtonClicked_control(msg);
            }});
        right=(Button) keyControlLayout.findViewById(R.id.right);
        right.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                msg = "r";
                onButtonClickedListener_control.onButtonClicked_control(msg);
            }});


        return keyControlLayout;
    }

    // 发送器接口
    public interface OnButtonClickedListener_Control {
        public void onButtonClicked_control(String ButtonText);
    }

    private OnButtonClickedListener_Control onButtonClickedListener_control;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try {
            onButtonClickedListener_control = (OnButtonClickedListener_Control) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement OnButtonClickedListener");
        }
    }
}
