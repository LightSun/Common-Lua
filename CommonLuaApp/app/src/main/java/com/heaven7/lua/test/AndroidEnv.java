package com.heaven7.lua.test;

import android.content.Context;

import com.heaven7.java.lua.LuaJavaCaller;
import com.heaven7.java.lua.LuaState;
import com.heaven7.lua.Luaer;

public final class AndroidEnv {

    public static void initialize(Context context, Luaer luaer){
        LuaJavaCaller.registerJavaClass(AndroidEnvResolver.class);
        AndroidEnvResolver env = AndroidEnvResolver.init(context);

        LuaState state = luaer.getLuaState();
        String str = luaer.loadLuaAssetsAsString("lua/AndroidEnv.lua");
        state.doString(str);
        //state.dumpLuaStack();

        state.pushString("SET_ANDROID_ENV");
        state.getTable(-2);
        //state.dumpLuaStack();

        state.push(env);
       // state.dumpLuaStack();
        int result = state.pcall(1, 0, 0);
        if(result != 0){
            System.err.println(state.toString(-1));
        }
        System.out.println("AndroidEnv init " + (result == 0 ? "success" : "failed"));
    }
}
