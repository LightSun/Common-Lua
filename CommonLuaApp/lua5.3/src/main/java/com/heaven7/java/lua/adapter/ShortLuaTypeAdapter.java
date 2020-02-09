package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.LuaValue;
import com.heaven7.java.lua.LuaState;

public class ShortLuaTypeAdapter extends IntLuaTypeAdapter {
    @Override
    public Object readFromLua(LuaState luaState, LuaValue arg) {
        return arg.toShortValue();
    }
}