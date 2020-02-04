package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;

public class ShortLuaTypeAdapter extends IntLuaTypeAdapter {
    @Override
    public Object convert(String arg) {
        return Float.valueOf(arg).shortValue();
    }

    @Override
    public Object lua2java(LuaState luaState, Lua2JavaValue arg) {
        return arg.toShortValue();
    }
}