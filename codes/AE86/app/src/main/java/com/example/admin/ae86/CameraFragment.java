package com.example.admin.ae86;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;


public class CameraFragment extends Fragment {
    private OutputStream ops =null;
    private final int PORT = 8888;
    private ImageView image;
    private ServerSocket serverSocket = null;
    private Bitmap bitmap = null;
    private static Handler handler;
    private Matrix mMatrix = new Matrix();
    private receiveThread receivethread;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View cameraLayout = inflater.inflate(R.layout.camerafragment, container, false);

        image=(ImageView) cameraLayout.findViewById(R.id.video);
        mMatrix.reset();
        mMatrix.setRotate(90);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //一帧一帧地显示图片
                if(bitmap!=null) {
                    Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(),bitmap.getHeight(), mMatrix, true);
                    image.setImageBitmap(bitmap2);
                }
                super.handleMessage(msg);
            }
        };

        receivethread = new receiveThread();
        receivethread.start();
        return cameraLayout;
    }

    class receiveThread extends Thread{
        private int length = 0;
        private int num = 0;
        private byte[] buffer = new byte[2048];
        private byte[] data = new byte[204800];
        @Override
        public void run(){
            //showTip("receiveThread initialize...");
            try{
                if(serverSocket==null)serverSocket = new ServerSocket(PORT);
                //不断接受从PORT中发来的数据
                while(true){
                    Socket socket = serverSocket.accept();
                    try{
                        InputStream input = socket.getInputStream();
                        num = 0;
                        do{
                            length = input.read(buffer);
                            if(length >= 0){
                                //将buffer中从0开始length长度的数据读入以num位起始的data中
                                System.arraycopy(buffer,0,data,num,length);
                                num += length;//下一次开始的位置
                            }
                        }while(length >= 0);
                        new setImageThread(data,num).start();
                        input.close();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }finally {
                        socket.close();
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }finally {
                try{
                serverSocket.close();}catch (Exception e){}
            }
        }
    }
    class setImageThread extends Thread{
        private byte[]data;
        private int num;
        public setImageThread(byte[] data, int num){
            this.data = data;
            this.num = num;
        }
        @Override
        public void run(){
            //showTip("setImageThreadinitialize...");
            bitmap = BitmapFactory.decodeByteArray(data, 0, num);
            Message msg=new Message();
            handler.sendMessage(msg);
        }
    }

    public interface OnCamera {
        public void OnCameraInfo(String info);
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private OnCamera onCamera;
    }
