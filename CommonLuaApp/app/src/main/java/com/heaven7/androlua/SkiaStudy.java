package com.heaven7.androlua;

import android.Manifest;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.heaven7.core.util.PermissionHelper;

public class SkiaStudy extends Activity {

    private final PermissionHelper mHelper = new PermissionHelper(this);
    GLSurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_skia_study);

        mSurfaceView = findViewById(R.id.glSurface);

        mHelper.startRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1, new PermissionHelper.ICallback() {
            @Override
            public void onRequestPermissionResult(String s, int i, boolean b) {
                if(b){

                }
            }
        });
    }
}
