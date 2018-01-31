package com.example.admin.ae86;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class CameraSurfacePreview extends SurfaceView implements SurfaceHolder.Callback{

    private SurfaceHolder mHolder;
    private Canvas canvas;
    private Matrix matrix = new Matrix();
    private final int PORT = 8888;

    public CameraSurfacePreview(Context context) {
        super(context);

        // 获取SurfaceHolder对象
        mHolder = getHolder();
        // 注册回调监听器
        mHolder.addCallback(this);
        // 设置SurfaceHolder类型
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {

        Thread th = new MyThread();
        th.start();
        th.setPriority(Thread.NORM_PRIORITY + 5);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void takePicture(Camera.PictureCallback imageCallback) {
        //mCamera.takePicture(null, null, imageCallback);
    }

    class MyThread extends Thread {
        public Socket socket;
        public ServerSocket serverSocket;

        public void run() {
            byte[] buffer = new byte[1024];
            int len = 0;
            // 打开端口
            try {
                serverSocket = new ServerSocket(PORT);
            } catch (IOException e2) {
                e2.printStackTrace();
            }

            //不断接受从PORT中发来的数据
            while (true) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                InputStream input = null;
                try {
                    socket = serverSocket.accept();
                    input = socket.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // 将读到的数据写入buffer里，再将buffer里的数据写到outStream里
                try {
                    while ((len = input.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                try {
                    outStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // 显示出来
                byte data[] = outStream.toByteArray();
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                matrix.setRotate(90,bitmap.getWidth()/2,bitmap.getHeight()/2);
                matrix.setTranslate(-30,0);

                canvas = mHolder.lockCanvas(); // 通过lockCanvas加锁并得到該SurfaceView的画布
                canvas.drawColor(Color.BLACK);
                canvas.drawBitmap(bitmap,matrix, null); // 把SurfaceView的画布传给物件，物件会用这个画布将自己绘制到上面的某个位置
                mHolder.unlockCanvasAndPost(canvas); // 释放锁并提交画布进行重绘
                if(!bitmap.isRecycled())
                    bitmap.recycle();
                System.gc();

                // 关闭
                try {
                    input.close();
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
