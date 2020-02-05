package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;

public class FloatLuaTypeAdapter extends NumberAdapterLua{
    @Override
    public Object defaultValue() {
        return 0f;
    }

    @Override
    public Object lua2java(LuaState luaState, Lua2JavaValue arg) {
        return arg.toFloatValue();
    }
}