package com.heaven7.java.lua.internal;

import android.support.annotation.RestrictTo;

import com.heaven7.java.lua.LuaState;


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

}
