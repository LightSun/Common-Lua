package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.LuaValue;
import com.heaven7.java.lua.LuaState;

public class FloatLuaTypeAdapter extends NumberAdapterLua{
    @Override
    public Object defaultValue() {
        return 0f;
    }

    @Override
    public Object readFromLua(LuaState luaState, LuaValue arg) {
        return arg.toFloatValue();
    }
}