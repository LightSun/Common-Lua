package com.heaven7.java.lua.internal;

import android.support.annotation.RestrictTo;

import com.heaven7.java.lua.LuaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;

import java.lang.reflect.Type;


@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class LuaUtils {

    public static void checkTopDelta(LuaState ls, int expect) {
        int cTop = ls.getTop();
        if (cTop != expect) {
            throw new IllegalStateException("wrong top = " + cTop + ",expect top = " + (expect));
        }
    }
    public static int adjustIdx(LuaState ls, int idx){
        return idx < 0 ? ls.getTop() + idx + 1 : idx;
    }
    public static int writeToLua(LuaState state, Type type, Object val){
        if(val == null){
            state.pushNil();
            return 1;
        }else {
            return LuaTypeAdapter.get(type != null ? type : val.getClass()).writeToLua(state, val);
        }
    }
    public static int writeToLua(LuaState state, Object val){
        if(val == null){
            state.pushNil();
            return 1;
        }else {
            return LuaTypeAdapter.get(val.getClass()).writeToLua(state, val);
        }
    }
    public static void recycleValues(Object[] args) {
         if(args != null){
             for (Object val : args){
                 if(val instanceof LuaValue){
                     ((LuaValue) val).recycle();
                 }
             }
         }
    }
}
