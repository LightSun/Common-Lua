package com.heaven7.java.lua.convertors;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaUtils;
import com.heaven7.java.lua.TypeConvertor;

import java.lang.reflect.Array;

public class ArrayTypeConvertor extends NonSimpleTypeConvertor{

    public Object lua2java(LuaState luaState, Lua2JavaValue arg){
        throw new UnsupportedOperationException("latter will support.");
    }

    public int java2lua(LuaState luaState, Object result){
        luaState.newTable();
        int top = luaState.getTop();
        int length = Array.getLength(result);
        for (int i = 0 ; i < length ; i ++){
            Object ele = Array.get(result, i);
            //must only add one to lua stack
            TypeConvertor convertor = TypeConvertorFactory.getTypeConvertor(ele.getClass());
            convertor.java2lua(luaState, ele);
            LuaUtils.checkTopDelta(luaState, top + 1);
            luaState.rawSeti(-2, i + 1); //lua array from 1
        }
        luaState.setCollectionTypeAsMeta(-1, LuaState.COLLECTION_TYPE_LIST);
        return 1;
    }

}
