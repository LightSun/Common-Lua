package com.heaven7.java.lua.convertors;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;

public class ByteConvertor extends IntConvertor {

    @Override
    public Object convert(String arg) {
        return Float.valueOf(arg).byteValue();
    }
    @Override
    public Object lua2java(LuaState luaState, Lua2JavaValue arg) {
        return arg.toByteValue();
    }
}