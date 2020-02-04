package com.heaven7.java.lua;

public abstract class LuaTypeAdapter {

    public Object convert(String arg){
        return null;
    }
    public Object defaultValue(){
        return null;
    }
    /**
     * convert lua data to java
     * @param luaState the lua state
     * @param arg the argument
     * @return the java value which represent lua data
     */
    public abstract Object lua2java(LuaState luaState, Lua2JavaValue arg);

    /**
     * convert java object to lua
     * @param luaState the lua stack
     * @param result the java object
     * @return the push result count of lua stack
     */
    public abstract int java2lua(LuaState luaState, Object result);
}