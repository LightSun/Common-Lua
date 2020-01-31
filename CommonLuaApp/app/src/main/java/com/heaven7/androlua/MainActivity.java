package com.heaven7.androlua;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.heaven7.androlua.bean.Person;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.PermissionHelper;
import com.heaven7.java.lua.LuaJavaCaller;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTest;

import java.nio.charset.Charset;
import java.util.Arrays;

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
    private final PermissionHelper mHelper = new PermissionHelper(this);

    private Luaer mLuaer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLuaer = new Luaer(this);
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
        mLuaer.destroyLuaState();
    }
    public void onClickTestLuaObject(View view) {
        mLuaer.loadLuaRaw(R.raw.my_class);
    }
    public void onClickTestCJson(View view){
        mLuaer.loadLuaAssets("lua/cjson_encode.lua");
    }
    public void onClickTestLuaExtend(View view){
        mLuaer.loadLuaAssets("lua/extend2.lua");
    }
    public void onClickTestBindCpp(View view){ //LuaWrapper
        String script = mLuaer.loadLuaAssetsAsString("lua/LuaTest.lua");
        LuaTest.testBindCpp1(mLuaer.getLuaState(), script);
    }
    public void onClickTestLuaRegistry(View view){
        String script = mLuaer.loadLuaAssetsAsString("lua/luaregistry_test.lua");
        LuaTest.testLuaRegistry(mLuaer.getLuaState(), script);
    }
    public void onClickTestAccessCppObj(View view){
        String script = mLuaer.loadLuaAssetsAsString("lua/access_cpp_obj.lua");
        LuaTest.testAccessCppObjectInLua(mLuaer.getLuaState(), script);
    }

    public void onClickTestBlowfish(View view) {
       // String script = loadLuaAssetsAsString("lua/LuaTest.lua");
       // String script = "1234567890";
        String script = "1234567890 abcdefghijklmnopqrstuvwxyz !@#$%^&*()~_+.";
        byte[] enResult = LuaTest.bf_en(script.getBytes());
        byte[] deResult = LuaTest.bf_de(enResult, script.length());
        Logger.d(TAG, "onClickTestBlowfish", "======== start test blow-fish =========");
        Logger.d(TAG, "onClickTestBlowfish", script);
        Logger.d(TAG, "onClickTestBlowfish", Arrays.toString(enResult));
        Logger.d(TAG, "onClickTestBlowfish", Arrays.toString(deResult));
        Logger.d(TAG, "onClickTestBlowfish", new String(deResult, Charset.defaultCharset()));
        //
        String file = Environment.getExternalStorageDirectory() + "/vida/lua.txt";
        Luaer.writeToFile(file, enResult, script.length());
    }
    public void onClickDecodeBlowfish(View view){
        String file = Environment.getExternalStorageDirectory() + "/vida/lua.txt";
        LuaTest.bfDecodeFile(file);
    }
    public void onClickTestWrapJavaObject(View view){
        Person p = new Person();
        p.setAge(18);
        p.setName("Google/Heaven7");
        LuaJavaCaller.registerJavaClass(Person.class);

        LuaState luaState = mLuaer.getLuaState();
        int k = luaState.saveLightly();
        //luaState.dumpLuaStack();
        luaState.push(p);
        luaState.pushString("call");
        luaState.getTable(-2); //get method
        luaState.dumpLuaStack(); //tab, func

        //br.call(method, args..., size)
        luaState.pushString("getName");
        luaState.pushNumber(0);
        int code = luaState.pcall(2, 1, 0);
        if(code != 0){
            String str = luaState.toString(-1);
            System.out.println("onClickTestWrapJavaObject >>>  exception = " + str);
        }else {
            if(luaState.getType(-1) != LuaState.TYPE_STRING){
                throw new IllegalStateException("result must be string");
            }
            System.out.println("onClickTestWrapJavaObject >>> ok! " + luaState.toString(-1));
        }
        //luaState.dumpLuaStack();
        luaState.restoreLightly(k);
    }
    private void initLua() {
        mLuaer.initLuaState();
        mLuaer.initEnv();
    }

}
