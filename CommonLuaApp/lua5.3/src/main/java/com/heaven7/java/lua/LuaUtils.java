package com.heaven7.java.lua;

import android.support.annotation.RestrictTo;

import static com.heaven7.java.lua.convertors.TypeConvertorFactory.getTypeConvertor;

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

    public static int java2lua(LuaState luaState, Object val){
        if(val == null){
            luaState.pushNil();
            return 1;
        }else {
            return getTypeConvertor(val.getClass()).java2lua(luaState, val);
        }
    }
}
