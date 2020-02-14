package com.heaven7.lua;

import android.app.Activity;
import android.os.Bundle;

import com.heaven7.android.lua.app.R;

//adb logcat | ndk-stack -sym arm64-v8a
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
