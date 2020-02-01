package com.heaven7.java.lua;

public interface TypeConvertor {

    Object convert(String arg);

    /**
     * convert lua data to java
     * @param luaState the lua state
     * @param arg the argument
     * @return the java value which represent lua data
     */
    Object lua2java(LuaState luaState, Lua2JavaValue arg);

    Object defaultValue();
    /**
     * convert java object to lua
     * @param luaState the lua stack
     * @param result the java object
     * @return the push result count of lua stack
     */
    int java2lua(LuaState luaState, Object result);
}