package com.example.admin.ae86;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements KeyControlFragment.OnButtonClickedListener_Control,GravityControlFragment.OnListener_Gravity,VoiceControlFragment.OnListener_Voice,GestureControlFragment.OnListener_Gesture {

    //布局切换按键
    private Button keyControl_button;
    private Button voiceControl_button;
    private Button gravityControl_button;
    private Button gestureControl_button;

    //5 个可选布局
    private KeyControlFragment keyControlFragment;
    private VoiceControlFragment voiceControlFragment;
    private GravityControlFragment gravityControlFragment;
    private GestureControlFragment gestureControlFragment;
    private CameraFragment cameraFragment;

    //蓝牙设备与串口
    private BluetoothDevice mDevice = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 设置三个按钮的布局
        initViews();
        // 开启蓝牙
        startBluetooth();
        // 开启视频
        initCameraPreview();
    }

    // 设置4个按钮的布局
    private void initViews() {
        // 点击按钮，展开布局
        // 方向键方式
        keyControl_button = (Button) findViewById(R.id.keyControl_button);
        keyControl_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 用KeyControlFragment类来布置一个layout
                // 再用以下方法把content的FrameLayout替换为keyControlFragment布置的layout
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                keyControlFragment = new KeyControlFragment();
                fragmentTransaction.replace(R.id.content, keyControlFragment);
                fragmentTransaction.commit();
            }
        });
        // 语音模式
        voiceControl_button = (Button) findViewById(R.id.voiceControl_button);
        voiceControl_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                voiceControlFragment = new VoiceControlFragment();
                fragmentTransaction.replace(R.id.content, voiceControlFragment);
                fragmentTransaction.commit();
            }
        });
        // 重力模式
        gravityControl_button = (Button) findViewById(R.id.gravityControl_button);
        gravityControl_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                gravityControlFragment = new GravityControlFragment();
                fragmentTransaction.replace(R.id.content,gravityControlFragment);
                fragmentTransaction.commit();
            }
        });
        // 手势控制
        gestureControl_button = (Button) findViewById(R.id.gestureControl_button);
        gestureControl_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                gestureControlFragment = new GestureControlFragment();
                fragmentTransaction.replace(R.id.content,gestureControlFragment);
                fragmentTransaction.commit();
            }
        });
    }

    // 设置菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit:
                finish();
                break;
            case R.id.startBluetooth:
                startBluetooth();
                break;
            case R.id.initCameraPreview:
                initCameraPreview();
                break;
        }
        return true;
    }

    // 开启蓝牙
    public void startBluetooth() {
        // 获取蓝牙适配器
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "不支持蓝牙", Toast.LENGTH_LONG).show();
            DisplayText("不支持蓝牙");
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(mIntent, 1);
            DisplayText("蓝牙未打开，请重试");
            return;
        }
        mBluetoothAdapter.startDiscovery();
        // 获取配对设备
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice d : pairedDevices) {
            if (d.getName().equals("HC-06")) {
                mDevice = d;
                DisplayToast("已和蓝牙模块配对！");
                break;
            }
        }
        try {
            Sender.btSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            DisplayToast("套接字创建失败！");
        }
        mBluetoothAdapter.cancelDiscovery();
        try {
            Sender.btSocket.connect();
            DisplayToast("连接成功建立，数据连接打开！");
            DisplayText("蓝牙连接状态良好");
        } catch (IOException e) {
            try {
                DisplayToast("连接没有建立");
                DisplayText("蓝牙未连接，请重试");
                Sender.btSocket.close();
            } catch (IOException e2) {
                DisplayToast("连接没有建立，无法关闭套接字！");
            }
        }
        try {
            Sender.outStream = Sender.btSocket.getOutputStream();
            Sender.inputStream = Sender.btSocket.getInputStream();
        } catch (IOException e) {
            DisplayToast("无法建立输出流");
        }

    }

    // 方向控制接口
    public void onButtonClicked_control(String message){
        bluetoothSender(message);
    }
    // 重力控制接口
    public void on_gravity(String message) {
        bluetoothSender(message);
    }
    // 语音控制接口
    public void on_voice(String message) {
        bluetoothSender(message);
    }
    // 手势控制接口
    public void on_gesture(String message){ 
        bluetoothSender(message); 
    }

    // 蓝牙发送器接口
    public void bluetoothSender(String command){
        try {
            byte[] buffer = command.getBytes();
            Sender.outStream.write(buffer);
        } catch (Exception e) {
        }
    }

    // 消息显示函数
    public void DisplayToast(String str) {
        Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 220);
        toast.show();
    }
    // 信息文字函数
    public void DisplayText(String str) {
        TextView textView = (TextView) findViewById(R.id.state_text);
        textView.setText(str.toCharArray(),0,str.length());
    }

    private void initCameraPreview(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //if(cameraFragment!=null) cameraFragment.stop();
        cameraFragment = new CameraFragment();
        fragmentTransaction.replace(R.id.camera,cameraFragment);
        fragmentTransaction.commit();
        DisplayToast("布局完成");
    }
}
