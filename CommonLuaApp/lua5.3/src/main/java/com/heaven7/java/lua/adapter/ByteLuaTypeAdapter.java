package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;

public class ByteLuaTypeAdapter extends IntLuaTypeAdapter {

    @Override
    public Object lua2java(LuaState luaState, Lua2JavaValue arg) {
        return arg.toByteValue();
    }
}