package com.heaven7.java.lua;

import android.support.annotation.Keep;

@Keep
public interface LuaTraveller {

    @Keep
    int travel(long luaStatePte, Lua2JavaValue key, Lua2JavaValue value);

}
