package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;

public class CharLuaTypeAdapter extends LuaTypeAdapter {

    @Override
    public Object defaultValue() {
        throw new RuntimeException("char type must be initialize.");
    }
    @Override
    public int java2lua(LuaState luaState, Object result) {
        char ch = (Character) result;
        luaState.pushString(new String(new char[]{ ch }));
        return 1;
    }
    @Override
    public Object lua2java(LuaState luaState, Lua2JavaValue arg) {
        return arg.toCharValue();
    }
}