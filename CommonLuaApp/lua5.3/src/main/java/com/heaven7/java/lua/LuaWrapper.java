package com.heaven7.java.lua;

import android.os.Environment;
import android.support.annotation.Keep;
import android.support.v4.util.ArraySet;
import android.util.Log;

import java.io.File;
import java.util.Set;

/**
 * Created by heaven7 on 2019/7/1.
 */
public final class LuaWrapper {

    private static final String SALT = "heaven7";
    private final Set<LuaSearcher> mList = new ArraySet<>();
  //  private final Map<String, LuaFunction>
    private final StringBuilder mSb = new StringBuilder();

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
    @Keep
    public String createTempFile(String fn){
        String dir = Environment.getExternalStorageDirectory() + "/temp";
        File file = new File(dir);
        if(!file.exists()){
            file.mkdirs();
        }
        File dst = new File(dir, SALT + "_" + System.currentTimeMillis());
        if(dst.exists()){
            dst.delete();
        }
        return dst.getAbsolutePath();
    }
    @Keep
    public void print(String str, boolean concat){
        if(concat){
            mSb.append(str);
        }else {
            if(str != null){
                mSb.append(str);
            }
            String content = mSb.toString();
            mSb.delete(0, mSb.length());
            //log it
            Log.i("Lua_Print", content);
        }
    }
    @Keep //called by native
    public String searchLuaModule(String module){
        for (LuaSearcher s : mList){
            String filepath = s.getLuaFilepath(module);
            if(filepath != null){
                return filepath;
            }
        }
        return null;
    }
    @Keep //called by native
    public String searchCModule(String module){
        for (LuaSearcher s : mList){
            String filepath = s.getClibFilepath(module);
            if(filepath != null){
                return filepath;
            }
        }
        return null;
    }

    private native void nNativeInit();

}
