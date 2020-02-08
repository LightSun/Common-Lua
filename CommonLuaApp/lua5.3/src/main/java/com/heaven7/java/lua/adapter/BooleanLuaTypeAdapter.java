package com.heaven7.java.lua.adapter;


import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;

public class BooleanLuaTypeAdapter extends LuaTypeAdapter {

    @Override
    public Object defaultValue() {
        return false;
    }

    @Override
    public int writeToLua(LuaState luaState, Object result) {
        luaState.pushBoolean((Boolean)result);
        return 1;
    }
    @Override
    public Object readFromLua(LuaState luaState, Lua2JavaValue arg) {
        return arg.toBooleanValue();
    }
}