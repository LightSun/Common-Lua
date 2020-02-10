package com.heaven7.lua.test;

import com.heaven7.java.lua.LuaState;
import com.heaven7.lua.Luaer;

public final class AndroidEnvTest {

    public static void testR(Luaer luaer){
        LuaState state = luaer.getLuaState();
        String str = luaer.loadLuaAssetsAsString("lua/AndroidR.lua");
        int code = state.doString(str);
        System.err.println("code = " + code + " ,msg = " + state.toString(-1));
    }
}
