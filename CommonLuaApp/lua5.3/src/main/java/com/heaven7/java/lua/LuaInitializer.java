package com.heaven7.java.lua;

import com.heaven7.java.lua.iota.ILuaTypeAdapterManager;
import com.heaven7.java.lua.iota.LuaTypeAdapterManager;
import com.heaven7.java.lua.iota.SimpleLuaReflectyContext;

public final class LuaInitializer {

    private static ILuaTypeAdapterManager sTAM = new LuaTypeAdapterManager(new SimpleLuaReflectyContext());

    public static void setLuaTypeAdapterManager(ILuaTypeAdapterManager tam){
        sTAM = tam;
    }
    public static ILuaTypeAdapterManager getLuaTypeAdapterManager(){
        return sTAM;
    }
}
