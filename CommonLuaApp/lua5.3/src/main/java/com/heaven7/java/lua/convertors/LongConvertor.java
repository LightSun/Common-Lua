package com.heaven7.java.lua.convertors;

import com.heaven7.java.lua.Lua2JavaValue;

public class LongConvertor extends IntConvertor {
    @Override
    public Object convert(String arg) {
        return Double.valueOf(arg).longValue();
    }

    @Override
    public Object convert(Lua2JavaValue arg) {
        return arg.toLongValue();
    }
}