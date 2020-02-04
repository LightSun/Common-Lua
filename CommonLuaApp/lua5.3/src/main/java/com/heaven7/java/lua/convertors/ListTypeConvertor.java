package com.heaven7.java.lua.convertors;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaUtils;

import java.util.List;

public class ListTypeConvertor extends NonSimpleTypeConvertor{

    public Object lua2java(LuaState luaState, Lua2JavaValue arg){
        throw new UnsupportedOperationException("latter will support.");
    }

    public int java2lua(LuaState luaState, Object result){
        luaState.newTable();
        int top = luaState.getTop();
        final List list = (List) result;
        for (int i = 0, size = list.size() ; i < size ; i ++){
            Object ele = list.get(i);
            //must only add one to lua stack
            LuaUtils.java2lua(luaState, ele);
            LuaUtils.checkTopDelta(luaState, top + 1);
            luaState.rawSeti(-2, i + 1); //lua array from 1
        }
        luaState.setCollectionTypeAsMeta(-1, LuaState.COLLECTION_TYPE_LIST);
        return 1;
    }

}