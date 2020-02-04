package com.heaven7.java.lua.convertors;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;

public class LongLuaTypeAdapter extends IntLuaTypeAdapter {
    @Override
    public Object convert(String arg) {
        return Double.valueOf(arg).longValue();
    }

    @Override
    public Object lua2java(LuaState luaState, Lua2JavaValue arg) {
        return arg.toLongValue();
    }
}