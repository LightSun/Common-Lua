package com.heaven7.androlua;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.heaven7.core.util.Logger;
import com.heaven7.core.util.PermissionHelper;
import com.heaven7.java.base.util.IOUtils;
import com.heaven7.java.lua.LuaSearcher;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaWrapper;
import com.heaven7.java.pc.schedulers.Schedulers;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

//adb logcat | ndk-stack -sym arm64-v8a
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final String LUA_DIR = Environment.getExternalStorageDirectory() + "/vida/lua";
    private static final String LUA_PARENT_DIR = Environment.getExternalStorageDirectory() + "/vida";
    private final PermissionHelper mHelper = new PermissionHelper(this);

    private LuaState mLuaState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHelper.startRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1, new PermissionHelper.ICallback() {
            @Override
            public void onRequestPermissionResult(String s, int i, boolean b) {
                if(b){
                    initLua();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        mHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void onClickTestLuaObject(View view) {
        /* Schedulers.io().newWorker().schedule(new Runnable() {
            @Override
            public void run() {
                // loadLua("MyClass.lua");
                loadLuaRaw(R.raw.my_class);
            }
        });*/
        loadLuaRaw(R.raw.my_class);
    }

    public void onClickTestLuaScript(View view) {
        executeLuaFile();
    }

    private void initLua() {
        mLuaState = new LuaState();
        LuaWrapper.getDefault().registerLuaSearcher(new LuaSearcher() {
            @Override
            public String getLuaFilepath(String module) {
                Logger.d(TAG, "getLuaFilepath", "module = " + module);
                return LUA_DIR + "/" + module + ".lua";
            }
        });
        Schedulers.io().newWorker().schedule(new Runnable() {
            @Override
            public void run() {
                AssetsFileCopyUtils.copyAll(getApplicationContext(), "lua", LUA_PARENT_DIR);
                Logger.d(TAG, "run", "lua script copy done");
            }
        });
    }

    public void loadLuaAssets(String file) {
        InputStreamReader in = null;
        try {
            in = new InputStreamReader(getAssets().open(file));
            int state = mLuaState.LdoString(IOUtils.readString(in));
            Logger.i(TAG, "loadLua", "state = " + state);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
    public void loadLuaRaw(int file) {
        InputStreamReader in = null;
        try {
            in = new InputStreamReader(getResources().openRawResource(file));
            int state = mLuaState.LdoString(readStringWithLine(in));
            String msg = mLuaState.toString(-1);
            Logger.i(TAG, "loadLua", "state = " + state + " ,msg = " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private void executeLuaFile() {
       /* try {
            //载入脚本
            mLuaState.LdoString(IOUtils.readString(new InputStreamReader(getResources().openRawResource(R.raw.luafile))));

            //执行函数
            mLuaState.getGlobal("functionInLuaFile");
            mLuaState.pushString("from Java params");// 将参数压入栈
            // functionInLuaFile函数有一个参数，一个返回结果
            int paramCount = 1;
            int resultCount = 1;
            int code = mLuaState.pcall(paramCount, resultCount, -1);
            String result = mLuaState.toString(-1);
            if (code != 0) {
                System.err.println("error:" + result + " code:" + code);
            }
            // displayResult2.setText(mLuaState.toString(-1));// 输出 结果

            mLuaState.getGlobal("GetVersion");
            mLuaState.pushString("reload lua test");// 将参数压入栈
//            mLuaState.pushInteger(10);//不能输入int
//            mLuaState.pushString("10");
            mLuaState.pushNumber(10);
            int retCode = mLuaState.pcall(2, 1, -1);
            result = mLuaState.toString(-1);
            //retCode=0表示正确调用，否则有异常
            if (retCode == 0) {
                if (result == null) {
                    System.out.println("GetVersion return empty value");
                } else {
                    System.out.println("GetVersion return value" + result);
                }
            } else {
                System.out.println("error:" + result + " code:" + retCode);
            }

            //test error
            mLuaState.getGlobal("testErrorHandler");
            mLuaState.call(0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public static String readStringWithLine(Reader r) throws IOException {
        BufferedReader br = r instanceof BufferedReader ? (BufferedReader) r : new BufferedReader(r);

        String str;
        StringBuilder sb = new StringBuilder();
        while ((str = br.readLine()) != null) {
            sb.append(str).append("\n");
        }
        return sb.toString();
    }
}
