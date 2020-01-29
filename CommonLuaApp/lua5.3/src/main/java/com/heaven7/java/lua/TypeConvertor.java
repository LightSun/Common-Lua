package com.heaven7.java.lua;

public interface TypeConvertor {

    Object convert(String arg);

    Object convert(Lua2JavaValue arg);

    Object defaultValue();

    void convert(LuaState luaState, Object result);
}