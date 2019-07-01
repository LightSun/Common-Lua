package com.heaven7.androlua;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.heaven7.core.util.Logger;
import com.heaven7.java.base.util.IOUtils;
import com.heaven7.java.lua.LuaSearcher;
import com.heaven7.java.lua.LuaWrapper;

import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;

import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private LuaState mLuaState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLua();
    }

    @Override
    protected void onDestroy() {
        if (mLuaState != null && !mLuaState.isClosed()) {
            mLuaState.close();
            mLuaState = null;
        }
        super.onDestroy();
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

    /**
     * 只是在第一次调用，如果升级脚本也不需要重复初始化
     */
    private void initLua() {
        mLuaState = LuaStateFactory.newLuaState();
        mLuaState.openLibs();
        LuaWrapper.getDefault().registerLuaSearcher(new LuaSearcher() {
            @Override
            public String getLuaFilepath(String module) {
                Logger.d(TAG, "getLuaFilepath", "module = " + module);
                return null;
            }
        });
        //为了lua能使用系统日志，传入Log
        try {
            //push一个对象到对象到栈中
            mLuaState.pushObjectValue(Log.class);
            //设置为全局变量
            mLuaState.setGlobal("Log");
        } catch (LuaException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public void loadLua(String file) {
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
            int state = mLuaState.LdoString(IOUtils.readString(in));
            Logger.i(TAG, "loadLua", "state = " + state);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private void executeLuaFile() {
        try {
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
        }
    }
}
