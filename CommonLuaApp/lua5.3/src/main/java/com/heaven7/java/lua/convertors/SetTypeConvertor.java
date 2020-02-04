package com.heaven7.java.lua.convertors;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaUtils;
import com.heaven7.java.lua.TypeConvertor;

import java.util.Iterator;
import java.util.Set;

public class SetTypeConvertor extends NonSimpleTypeConvertor{

    public Object lua2java(LuaState luaState, Lua2JavaValue arg){
        throw new UnsupportedOperationException("latter will support.");
    }

    public int java2lua(LuaState luaState, Object result){
        luaState.newTable();
        int top = luaState.getTop();
        final Set<?> set = (Set) result;
        for (Iterator<?> it = set.iterator(); it.hasNext() ; ){
            Object ele = it.next();
            TypeConvertor convertor = TypeConvertorFactory.getTypeConvertor(ele.getClass());
            convertor.java2lua(luaState, ele);
            LuaUtils.checkTopDelta(luaState, top + 1);
            luaState.pushBoolean(true);
            luaState.rawSet(-3);
        }
        luaState.setCollectionTypeAsMeta(-1, LuaState.COLLECTION_TYPE_SET);
        return 1;
    }

}