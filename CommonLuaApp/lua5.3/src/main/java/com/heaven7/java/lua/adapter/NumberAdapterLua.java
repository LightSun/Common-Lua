package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;

/*public*/ abstract class NumberAdapterLua extends LuaTypeAdapter {

    @Override
    public int writeToLua(LuaState luaState, Object result) {
        luaState.pushNumber(((Number)result).doubleValue());
        return 1;
    }
}
