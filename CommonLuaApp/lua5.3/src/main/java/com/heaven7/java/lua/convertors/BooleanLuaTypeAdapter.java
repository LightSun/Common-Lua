package com.heaven7.java.lua.convertors;


import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;

public class BooleanLuaTypeAdapter extends LuaTypeAdapter {

    @Override
    public Object convert(String arg) {
        return Float.valueOf(arg).byteValue() == 1;
    }

    @Override
    public Object defaultValue() {
        return false;
    }

    @Override
    public int java2lua(LuaState luaState, Object result) {
        luaState.pushBoolean((Boolean)result);
        return 1;
    }
    @Override
    public Object lua2java(LuaState luaState, Lua2JavaValue arg) {
        return arg.toBooleanValue();
    }
}