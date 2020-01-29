package com.heaven7.java.lua.convertors;

import com.heaven7.java.lua.Lua2JavaValue;

public class ShortConvertor extends IntConvertor {
    @Override
    public Object convert(String arg) {
        return Float.valueOf(arg).shortValue();
    }

    @Override
    public Object convert(Lua2JavaValue arg) {
        return arg.toShortValue();
    }
}