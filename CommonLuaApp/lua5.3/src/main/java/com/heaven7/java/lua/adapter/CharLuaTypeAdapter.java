package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.LuaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;

public class CharLuaTypeAdapter extends LuaTypeAdapter {

    @Override
    public Object defaultValue() {
        throw new RuntimeException("char type must be initialize.");
    }
    @Override
    public int writeToLua(LuaState luaState, Object result) {
        char ch = (Character) result;
        luaState.pushString(new String(new char[]{ ch }));
        return 1;
    }
    @Override
    public Object readFromLua(LuaState luaState, LuaValue arg) {
        return arg.toCharValue();
    }
}