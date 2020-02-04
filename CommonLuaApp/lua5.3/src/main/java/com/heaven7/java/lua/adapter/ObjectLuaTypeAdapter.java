package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;

public class ObjectLuaTypeAdapter extends LuaTypeAdapter {

    public Object lua2java(LuaState luaState, Lua2JavaValue arg){
        throw new UnsupportedOperationException("latter will support.");
    }
    @Override
    public int java2lua(LuaState luaState, Object result) {
        luaState.push(result);
        return 1;
    }
}
