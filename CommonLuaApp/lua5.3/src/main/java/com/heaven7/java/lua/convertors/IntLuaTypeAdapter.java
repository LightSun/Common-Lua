package com.heaven7.java.lua.convertors;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;

public class IntLuaTypeAdapter extends NumberAdapterLua{

    @Override
    public Object convert(String arg) {
        return Float.valueOf(arg).intValue();
    }
    @Override
    public Object lua2java(LuaState luaState, Lua2JavaValue arg) {
        return arg.toIntValue();
    }
    @Override
    public Object defaultValue() {
        return 0;
    }
}