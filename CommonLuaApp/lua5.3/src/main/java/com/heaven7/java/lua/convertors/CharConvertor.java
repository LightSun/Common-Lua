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
    public void convert(LuaState luaState, Object result) {
        char ch = (Character) result;
        luaState.pushString(new String(new char[]{ ch }));
    }
    @Override
    public Object convert(Lua2JavaValue arg) {
        return arg.toCharValue();
    }
}