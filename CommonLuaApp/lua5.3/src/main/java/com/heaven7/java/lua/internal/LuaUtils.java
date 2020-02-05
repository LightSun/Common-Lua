package com.heaven7.java.lua.internal;

import android.support.annotation.RestrictTo;

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

    public static int java2lua(LuaState state, Type type, Object val){
        if(val == null){
            state.pushNil();
            return 1;
        }else {
            return LuaTypeAdapter.get(type != null ? type : val.getClass()).java2lua(state, val);
        }
    }

    public static int simpleJava2lua(LuaState state, Object val){
        if(val == null){
            state.pushNil();
            return 1;
        }else {
            return LuaTypeAdapter.get(val.getClass()).java2lua(state, val);
        }
    }
}
