package com.heaven7.java.lua.convertors;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaUtils;
import com.heaven7.java.lua.TypeConvertor;

import java.lang.reflect.Array;

public class ArrayTypeConvertor extends NonSimpleTypeConvertor{

    public Object convert(Lua2JavaValue arg){
        throw new UnsupportedOperationException("latter will support.");
    }

    public void convert(LuaState luaState, Object result){
        luaState.newTable();
        int top = luaState.getTop();
        int length = Array.getLength(result);
        for (int i = 0 ; i < length ; i ++){
            Object ele = Array.get(result, i);
            //must only add one to lua stack
            TypeConvertor convertor = TypeConvertorFactory.getTypeConvertor(ele.getClass());
            convertor.convert(luaState, ele);
            LuaUtils.checkTopDelta(luaState, top + 1);
            luaState.rawSeti(-2, i + 1); //lua array from 1
        }
    }

}
