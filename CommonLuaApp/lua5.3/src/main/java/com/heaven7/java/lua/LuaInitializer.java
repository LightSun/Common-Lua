package com.heaven7.java.lua;

import com.heaven7.java.lua.iota.ILuaTypeAdapterManager;

public final class LuaInitializer {

    private static ILuaTypeAdapterManager sTAM;

    public static void setLuaTypeAdapterManager(ILuaTypeAdapterManager tam){
        sTAM = tam;
    }
    public static ILuaTypeAdapterManager getLuaTypeAdapterManager(){
        return sTAM;
    }
}
