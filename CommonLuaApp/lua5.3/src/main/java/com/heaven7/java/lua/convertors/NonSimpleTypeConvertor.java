package com.heaven7.java.lua.convertors;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.TypeConvertor;

public abstract class NonSimpleTypeConvertor implements TypeConvertor {

    public Object convert(String arg){
        return null;
    }
    public Object defaultValue(){
        return null;
    }
    public Object lua2java(LuaState luaState, Lua2JavaValue arg){
        return null;
    }
}
