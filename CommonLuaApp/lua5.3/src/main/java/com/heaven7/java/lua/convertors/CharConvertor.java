package com.heaven7.java.lua.convertors;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.TypeConvertor;

public class CharConvertor implements TypeConvertor {

    @Override
    public Object convert(String arg) {
        return arg.charAt(0);
    }
    @Override
    public Object defaultValue() {
        throw new RuntimeException("char type must be initialize.");
    }
    @Override
    public int java2lua(LuaState luaState, Object result) {
        char ch = (Character) result;
        luaState.pushString(new String(new char[]{ ch }));
        return 1;
    }
    @Override
    public Object lua2java(LuaState luaState, Lua2JavaValue arg) {
        return arg.toCharValue();
    }
}