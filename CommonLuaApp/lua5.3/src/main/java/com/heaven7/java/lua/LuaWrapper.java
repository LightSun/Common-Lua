package com.heaven7.java.lua;

import android.support.v4.util.ArraySet;

import java.util.Set;

/**
 * Created by heaven7 on 2019/7/1.
 */
public final class LuaWrapper {

    private final Set<LuaSearcher> mList = new ArraySet<>();

    private static class Creator{
        static LuaWrapper INSTANCE = new LuaWrapper();
    }
    private LuaWrapper(){
        nNativeInit();
    }

    public static LuaWrapper getDefault(){
        return Creator.INSTANCE;
    }

    public void registerLuaSearcher(LuaSearcher searcher){
        mList.add(searcher);
    }
    public void unregisterLuaSearcher(LuaSearcher searcher){
        mList.remove(searcher);
    }
    public void unregisterLuaSearchers(){
        mList.clear();
    }
    public String searchModule(String module){
        for (LuaSearcher s : mList){
            String filepath = s.getLuaFilepath(module);
            if(filepath != null){
                return filepath;
            }
        }
        return null;
    }

    private native void nNativeInit();

}
