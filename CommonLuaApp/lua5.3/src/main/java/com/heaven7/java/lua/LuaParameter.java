package com.heaven7.java.lua;

import com.heaven7.java.lua.internal.LuaUtils;

import java.lang.reflect.Type;

public final class LuaParameter {

    private final Type type;
    private final Object value;

    public LuaParameter(Type type, Object value) {
        this.type = type;
        this.value = value;
    }
    public Type getType() {
        return type;
    }
    public Object getValue() {
        return value;
    }
    public void java2lua(LuaState luaState){
        LuaUtils.writeToLua(luaState, type, value);
    }
}
