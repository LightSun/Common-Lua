package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.LuaFunction;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;
import com.heaven7.java.lua.LuaValue;

public class FunctionLuaTypeAdapter extends LuaTypeAdapter {
    @Override
    public Object readFromLua(LuaState luaState, LuaValue arg) {
        return arg.toFunction(luaState);
    }
    @Override
    public int writeToLua(LuaState luaState, Object result) {
        luaState.pushFunction((LuaFunction) result);
        return 1;
    }
}
