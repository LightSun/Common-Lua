package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.LuaValue;
import com.heaven7.java.lua.LuaState;

public class DoubleLuaTypeAdapter extends NumberAdapterLua {

    @Override
    public Object defaultValue() {
        return 0d;
    }

    @Override
    public Object readFromLua(LuaState luaState, LuaValue arg) {
        return arg.toDoubleValue();
    }
}