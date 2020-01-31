package com.heaven7.java.lua.convertors;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.TypeConvertor;

public class StringTypeConvertor implements TypeConvertor {

    @Override
    public Object convert(String arg) {
        return arg;
    }
    @Override
    public Object lua2java(LuaState luaState, Lua2JavaValue arg) {
        return arg.toStringValue();
    }
    @Override
    public Object defaultValue() {
        return null;
    }
    @Override
    public int java2lua(LuaState luaState, Object result) {
        luaState.pushString((String) result);
        return 1;
    }
}