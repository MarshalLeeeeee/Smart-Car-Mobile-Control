package com.example.admin.driver;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback{
    private Button Transmit;
    private Button Stop;
    private SurfaceView surfaceView = null;
    private SurfaceHolder surfaceHolder = null;
    private Camera camera = null;
    private boolean flag = true;
    private String IP = "192.168.1.110";
    private String name = "car";
    private int serverPort = 8888;
    private int videoRate = 1;//刷新间隔
    private int tmpRate = 0;//视频序号
    private int videoQuality = 85;//视频质量
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Transmit = (Button) findViewById(R.id.Transmit);
        Stop = (Button) findViewById(R.id.Stop);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
        //点击发送视频
        Transmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                flag = true;
            }
        });
        //点击停止发送
        Stop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                flag = false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(0,0,0,"系统设置");
        menu.add(0,1,1,"退出程序");
        Toast.makeText(MainActivity.this, "Menu success!", Toast.LENGTH_LONG).show();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        super.onOptionsItemSelected(item);//获取菜单
        switch(item.getItemId())//菜单序号
        {
            case 0://系统设置
            {
                Intent intent=new Intent(this,SettingActivity.class);
                startActivity(intent);
            }
            break;
            case 1://退出
            {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            break;
        }
        Toast.makeText(MainActivity.this, "Option ok!", Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        // TODO Auto-generated method stub
        Toast.makeText(MainActivity.this, "onPreviewFrame initialize...", Toast.LENGTH_LONG).show();
        if(!flag) return;
        //视频刷新
        if(tmpRate < videoRate){
            tmpRate++;
            return;
        }
        tmpRate = 0;
        //Toast.makeText(MainActivity.this, "before try!", Toast.LENGTH_LONG).show();
        //使用系统自带的类来发送图片
        Camera.Size size = camera.getParameters().getPreviewSize();//获取大小
        try{//将YUV格式图像数据data转换成jpg
            if(data != null){
                //Toast.makeText(MainActivity.this, "try OKay!", Toast.LENGTH_LONG).show();
                YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                if(image != null){
                    //使用handler将图片发送出去
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    //to jpg
                    image.compressToJpeg(new Rect(0,0,size.width,size.height),videoQuality,outputStream);
                    outputStream.flush();
                    Thread thread = new sendThread(outputStream,name,IP,serverPort);
                    thread.start();
                }
            }
        }catch(Exception ex){
            Log.e("Sys", "Error:" + ex.getMessage());
            Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_LONG).show();
        }
    }

    //发送图片线程
    class sendThread extends Thread{
        private String name;
        private String ip;
        private int PORT;
        private int num = 0;
        private byte[] buffer = new byte[2048];
        private ByteArrayOutputStream output;
        public sendThread(ByteArrayOutputStream output,String name,String ip,int port){
            this.output = output;
            this.name = name;
            this.ip = ip;
            this.PORT = port;
            try{
                Toast.makeText(MainActivity.this, "sendThread initialize...", Toast.LENGTH_LONG).show();
                output.close();
            }catch(Exception e){
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_LONG).show();
            }
        }
        
        @Override
        public void run(){
            Toast.makeText(MainActivity.this, "before thread's try!", Toast.LENGTH_LONG).show();
            try{
                Toast.makeText(MainActivity.this, "sendThread working...", Toast.LENGTH_LONG).show();
                Socket socket = new Socket(ip,PORT);
                OutputStream out = socket.getOutputStream();
                ByteArrayInputStream inputstream = new ByteArrayInputStream(output.toByteArray());
                num = inputstream.read(buffer);
                do{
                    Toast.makeText(MainActivity.this, "success", Toast.LENGTH_LONG).show();
                    out.write(buffer,0,num);
                    num = inputstream.read(buffer);
                }while(num >= 0);
                out.flush();
                out.close();
                socket.close();
            }catch(UnknownHostException e){
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_LONG).show();
            }catch(IOException e){
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_LONG).show();
            }catch (Exception e){
                Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressWarnings("deprecation")

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//显示器类型
        //读取配置文件
        SharedPreferences preParas = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        name = preParas.getString("name","car");
        IP = preParas.getString("IP","192.168.1.102");
        String tmp = preParas.getString("serverPort", "8888");
        serverPort = Integer.parseInt(tmp);
        tmp = preParas.getString("videoRate", "1");
        videoRate = Integer.parseInt(tmp);
        tmp = preParas.getString("videoQuality","85");
        videoQuality = Integer.parseInt(tmp);
        super.onStart();
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    1);}
        try {
            camera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        try{
            if(camera != null){
                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        try{
            Toast.makeText(MainActivity.this, "create try ok", Toast.LENGTH_LONG).show();
            if(camera != null){
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            }
        }catch(IOException e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "失败surface", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        if (camera == null) return;
        camera.stopPreview();
        camera.setPreviewCallback(this);
        camera.setDisplayOrientation(90); //设置横行录制
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(640, 480);
        camera.setParameters(parameters);
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        if(camera != null){
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}
