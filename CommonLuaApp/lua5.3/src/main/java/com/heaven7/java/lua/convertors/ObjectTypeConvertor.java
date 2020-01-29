package com.heaven7.java.lua.convertors;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;

public class ObjectTypeConvertor extends NonSimpleTypeConvertor {

    public Object convert(Lua2JavaValue arg){
        throw new UnsupportedOperationException("latter will support.");
    }
    @Override
    public void convert(LuaState luaState, Object result) {
        luaState.push(result);
    }
}
