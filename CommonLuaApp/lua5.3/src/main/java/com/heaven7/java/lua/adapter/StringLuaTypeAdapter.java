package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.LuaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;

public class StringLuaTypeAdapter extends LuaTypeAdapter {
    @Override
    public Object readFromLua(LuaState luaState, LuaValue arg) {
        return arg.toStringValue();
    }
    @Override
    public Object defaultValue() {
        return null;
    }
    @Override
    public int writeToLua(LuaState luaState, Object result) {
        luaState.pushString((String) result);
        return 1;
    }
}
