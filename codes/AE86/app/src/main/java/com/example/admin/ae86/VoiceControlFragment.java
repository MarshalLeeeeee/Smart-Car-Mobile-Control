package com.example.admin.ae86;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by 123 on 2016/12/1.
 */

public class VoiceControlFragment extends Fragment {

    private String msg;
    private com.iflytek.cloud.SpeechRecognizer mIat;
    private String engine="iat";
    private String rate="16000";
    private Toast mToast;
    private TextView txt_voice;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View voiceLayout = inflater.inflate(R.layout.voicefragment,
                container, false);

        final Button voice_button_start = (Button) voiceLayout.findViewById(R.id.record_button);

        SpeechUtility.createUtility(getActivity(), "appid=5a13d557");
        mToast=Toast.makeText(getActivity(),"",Toast.LENGTH_LONG);
        txt_voice = (TextView)voiceLayout.findViewById(R.id.txt_voice);
        mIat= com.iflytek.cloud.SpeechRecognizer.createRecognizer(getActivity(), mInitListener);

        voice_button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIat.setParameter(SpeechConstant.DOMAIN,engine);
                mIat.setParameter(SpeechConstant.SAMPLE_RATE, rate);
                mIat.startListening(mRecognizerListener);
            }
        });

        return voiceLayout;
    }

    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int i) {
            showTip("begin initialize");
            if (i != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + i);
            }
            showTip("initialize success");
        }
    };

    private com.iflytek.cloud.RecognizerListener mRecognizerListener = new com.iflytek.cloud.RecognizerListener() {
        @Override
        public void onBeginOfSpeech() {
            showTip("开始说话");
        }

        @Override
        public void onEndOfSpeech() {
            showTip("结束说话");
        }

        @Override
        public void onResult(com.iflytek.cloud.RecognizerResult recognizerResult, boolean b) {
            showTip("???");
            String text=JsonParser.parseIatResult(recognizerResult.getResultString());
            txt_voice.setText(text);
            showTip(text);
            String stringA = "前进";
            String stringB = "后退";
            String stringC = "停止";
            String stringD = "左转";
            String stringE = "右转";
            if (text.equals(stringA)){
                msg = "f";
                onListener_voice.on_voice(msg);
            }
            if (text.equals(stringB)){
                msg = "b";
                onListener_voice.on_voice(msg);
            }
            if (text.equals(stringC)){
                msg = "s";
                onListener_voice.on_voice(msg);
            }
            if (text.equals(stringD)){
                msg = "l";
                onListener_voice.on_voice(msg);
            }
            if (text.equals(stringE)){
                msg = "r";
                onListener_voice.on_voice(msg);
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            showTip(speechError.getErrorDescription());
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };


    public interface OnListener_Voice {
        public void on_voice(String ButtonText);
    }

    private OnListener_Voice onListener_voice;

    private void showTip(String str){
        if (!TextUtils.isEmpty(str)){
            mToast.setText(str);
            mToast.show();
        }
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try {
            onListener_voice = (OnListener_Voice) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement OnButtonClickedListener");
        }
    }
}
