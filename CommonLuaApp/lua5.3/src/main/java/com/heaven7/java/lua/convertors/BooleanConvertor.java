package com.heaven7.java.lua.convertors;


import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.TypeConvertor;

public class BooleanConvertor implements TypeConvertor {

    @Override
    public Object convert(String arg) {
        return Float.valueOf(arg).byteValue() == 1;
    }

    @Override
    public Object defaultValue() {
        return false;
    }

    @Override
    public void convert(LuaState luaState, Object result) {
        luaState.pushBoolean((Boolean)result);
    }
    @Override
    public Object convert(Lua2JavaValue arg) {
        return arg.toBooleanValue();
    }
}