package com.heaven7.java.lua.convertors;

import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.TypeConvertor;

/*public*/ abstract class NumberConvertor implements TypeConvertor {

    @Override
    public void convert(LuaState luaState, Object result) {
        luaState.pushNumber(((Number)result).doubleValue());
    }
}
