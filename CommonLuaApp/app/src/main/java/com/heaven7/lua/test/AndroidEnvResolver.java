package com.heaven7.lua.test;

import android.content.Context;
import android.util.ArrayMap;

import com.heaven7.java.lua.LuaFunction;
import com.heaven7.java.lua.LuaState;

import java.util.Map;

public final class AndroidEnvResolver {

    public static AndroidEnvResolver INSTANCE;

    private final Context mAppContext;
    private final Map<String, String> mFullnameMap = new ArrayMap<>();
    private final PushClassFunction mFunc_pushClass = new PushClassFunction();

    private AndroidEnvResolver(Context mAppContext) {
        this.mAppContext = mAppContext.getApplicationContext();
        mFullnameMap.put("R", mAppContext.getPackageName() + ".R");
    }

    public static AndroidEnvResolver init(Context context){
        INSTANCE = new AndroidEnvResolver(context);
        return INSTANCE;
    }
    public static AndroidEnvResolver get(){
        return INSTANCE;
    }
    public String getPackage(){
       return mAppContext.getPackageName();
    }
    public LuaFunction getClassName(){
        return new GetCNFunction(mFullnameMap);
    }
    public LuaFunction pushClass(){
        return mFunc_pushClass;
    }

    private static class PushClassFunction extends LuaFunction{
        @Override
        protected int execute(LuaState state) {
            String str = state.toString(-1);
            if(str == null){
                state.pushNil();
                return 1;
            }
            try {
                state.pushClass(Class.forName(str), str, true);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            return 1;
        }
    }
    private static class GetCNFunction extends LuaFunction{
        final Map<String, String> mFullnameMap;
        public GetCNFunction(Map<String, String> mFullnameMap) {
            this.mFullnameMap = mFullnameMap;
        }
        @Override
        protected int execute(LuaState state) {
            String alias = state.toString(-1);
            if(alias == null){
                state.pushNil();
                return 1;
            }
            String s = mFullnameMap.get(alias);
            if(s == null){
                state.pushNil();
            }else {
                state.pushString(s);
            }
            return 1;
        }
    }
}
