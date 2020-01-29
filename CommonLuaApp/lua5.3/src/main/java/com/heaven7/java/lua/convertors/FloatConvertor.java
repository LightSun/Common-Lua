package com.heaven7.java.lua.convertors;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.TypeConvertor;

public class FloatConvertor extends NumberConvertor implements TypeConvertor {
    @Override
    public Object convert(String arg) {
        return Float.valueOf(arg);
    }

    @Override
    public Object defaultValue() {
        return 0f;
    }

    @Override
    public Object convert(Lua2JavaValue arg) {
        return arg.toFloatValue();
    }
}