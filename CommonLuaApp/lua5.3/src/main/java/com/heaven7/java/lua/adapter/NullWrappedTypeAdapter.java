package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;
import com.heaven7.java.lua.LuaValue;

public final class NullWrappedTypeAdapter extends LuaTypeAdapter {

    private final LuaTypeAdapter base;

    public NullWrappedTypeAdapter(LuaTypeAdapter base) {
        this.base = base;
    }
    @Override
    public Object defaultValue() {
        return base.defaultValue();
    }
    @Override
    public Object readFromLua(LuaState luaState, LuaValue arg) {
        if(arg.getType() == LuaValue.TYPE_NULL){
            return base.defaultValue();
        }
        return base.readFromLua(luaState, arg);
    }
    @Override
    public int writeToLua(LuaState luaState, Object result) {
        if(result == null){
            luaState.pushNil();
            return 1;
        }
        return base.writeToLua(luaState, result);
    }
}
