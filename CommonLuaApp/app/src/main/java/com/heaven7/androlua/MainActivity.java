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
import com.heaven7.java.lua.LuaTest;
import com.heaven7.java.lua.LuaWrapper;
import com.heaven7.java.pc.schedulers.Schedulers;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//adb logcat | ndk-stack -sym arm64-v8a

/**
 *   final public static int LUA_OK = 0;
 *   final public static int LUA_YIELD = 1;
 *   final public static int LUA_ERRRUN = 2;
 *   final public static int LUA_ERRSYNTAX = 3;
 *   final public static int LUA_ERRMEM = 4;
 *   final public static int LUA_ERRGCMM = 5;
 *   final public static int LUA_ERRERR = 6;
 */
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
    public void onClickDetachLua(View view) {
        if(mLuaState != null){
            mLuaState.destroyNative();
            mLuaState = null;
        }
    }
    public void onClickTestLuaObject(View view) {
        loadLuaRaw(R.raw.my_class);
    }
    public void onClickTestCJson(View view){
        loadLuaAssets("lua/cjson_encode.lua");
    }
    public void onClickTestLuaExtend(View view){
        loadLuaAssets("lua/extend2.lua");
    }
    public void onClickTestBindCpp(View view){
        String script = loadLuaAssetsAsString("lua/LuaTest.lua");
        LuaTest.testBindCpp1(mLuaState, script);
    }
    public void onClickTestLuaRegistry(View view){
        String script = loadLuaAssetsAsString("lua/luaregistry_test.lua");
        LuaTest.testLuaRegistry(mLuaState, script);
    }
    public void onClickTestAccessCppObj(View view){
        String script = loadLuaAssetsAsString("lua/access_cpp_obj.lua");
        LuaTest.testAccessCppObjectInLua(mLuaState, script);
    }

    public void onClickTestBlowfish(View view) {
       // String script = loadLuaAssetsAsString("lua/LuaTest.lua");
       // String script = "1234567890";
        String script = "1234567890abcdefghijklmnopqrstuvwxyz!@#$%^&*()~_+. ";
        byte[] enResult = LuaTest.bf_en(script.getBytes());
        byte[] deResult = LuaTest.bf_de(enResult, script.length());
        Logger.d(TAG, "onClickTestBlowfish", "======== start test blow-fish =========");
        Logger.d(TAG, "onClickTestBlowfish", script);
        Logger.d(TAG, "onClickTestBlowfish", Arrays.toString(enResult));
        Logger.d(TAG, "onClickTestBlowfish", Arrays.toString(deResult));
        Logger.d(TAG, "onClickTestBlowfish", new String(deResult, Charset.defaultCharset()));
       // LuaTest.bf_baseTest();
    }
    public void onClickTestLuaScript(View view) {
        executeLuaFile();
    }
    private void initLua() {
        mLuaState = new LuaState();
        final Map<String, Boolean> mCMap = new HashMap<>();
        mCMap.put("cjson", true);
        LuaWrapper.getDefault().registerLuaSearcher(new LuaSearcher() {
            @Override
            public String getLuaFilepath(String module) {
                if(mCMap.containsKey(module)){
                    return null;
                }
                Logger.d(TAG, "getLuaFilepath", "module = " + module);
                return LUA_DIR + "/" + module + ".lua";
            }
            @Override
            public String getClibFilepath(String module) {
                Logger.d(TAG, "getClibFilepath", "module = " + module);
                //return LUA_DIR + "/lib" + module + ".so";
                return new File(getFilesDir(), "libcjson.so").getPath();
            }
        });
        Schedulers.io().newWorker().schedule(new Runnable() {
            @Override
            public void run() {
                AssetsFileCopyUtils.copyAll(getApplicationContext(), "lua", LUA_PARENT_DIR);
                File dst = new File(getFilesDir(), "libcjson.so");
                System.out.println("libcjson: path is " + dst.getPath());
                if(dst.exists()){
                    System.out.println("libcjson load ok(already copied).");
                    return;
                }
                //lua load libcjson .the c json can't be put to sdcard.
                try {
                    InputStream in = getAssets().open("clua/libcjson.so");
                    OutputStream out = new FileOutputStream(dst);
                    IOUtils.copyLarge(in, out);
                    IOUtils.closeQuietly(in);
                    IOUtils.closeQuietly(out);
                    System.out.println("libcjson load ok.");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Logger.d(TAG, "run", "lua script copy done");
            }
        });
    }

    public void loadLuaAssets(String file) {
        InputStreamReader in = null;
        try {
            in = new InputStreamReader(getAssets().open(file));
            int state = mLuaState.LdoString(readStringWithLine(in));
            Logger.i(TAG, "loadLua", "state = " + state + ", " + mLuaState.toString(-1));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
    public String loadLuaAssetsAsString(String file) {
        InputStreamReader in = null;
        try {
            in = new InputStreamReader(getAssets().open(file));
            return readStringWithLine(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
