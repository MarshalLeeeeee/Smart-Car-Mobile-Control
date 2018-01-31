package com.example.admin.driver;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by 123 on 2016/12/11.
 */

public class SettingActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
    }
}
