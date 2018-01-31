package com.example.admin.ae86;

import android.bluetooth.BluetoothSocket;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class Sender {
    public static BluetoothSocket btSocket;
    public static OutputStream outStream = null;
    public static InputStream inputStream;
    //public static String IP_Address;

    public static void sendMessage(String msg){
        if(outStream == null) return;
        try{
            outStream.write(msg.getBytes());
        } catch(IOException e){
            Log.d("Lalala~","Send msg "+msg+" failed");
        }
    }
}

class JsonParser {

    public static String parseIatResult(String json) {
        if(TextUtils.isEmpty(json))
            return "";

        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }
}