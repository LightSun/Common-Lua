package com.heaven7.java.lua;

import com.heaven7.java.lua.iota.LuaTypeAdapterManager;

public final class LuaInitializer {

    private static LuaTypeAdapterManager sTAM;

    public static void setLuaTypeAdapterManager(LuaTypeAdapterManager tam){
        sTAM = tam;
    }
    public static LuaTypeAdapterManager getLuaTypeAdapterManager(){
        return sTAM;
    }
}
