package com.heaven7.java.lua;

import java.lang.reflect.Type;

import static com.heaven7.java.lua.LuaInitializer.getLuaTypeAdapterManager;

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
        LuaTypeAdapter.get(type, getLuaTypeAdapterManager())
                .java2lua(luaState, value);
    }
}
