package com.heaven7.java.lua.convertors;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;
import com.heaven7.java.lua.internal.LuaUtils;

import java.util.Collection;
import java.util.Iterator;

public class CollectionLuaTypeAdapter extends LuaTypeAdapter {

    public Object lua2java(LuaState luaState, Lua2JavaValue arg){
        throw new UnsupportedOperationException("latter will support.");
    }

    public int java2lua(LuaState luaState, Object result){
        luaState.newTable();
        int top = luaState.getTop();
        final Collection<?> coll = (Collection) result;
        int i = 0;
        for (Iterator<?> it = coll.iterator(); it.hasNext() ; i++ ){
            Object ele = it.next();
            LuaTypeAdapter convertor = TypeConvertorFactory.getTypeConvertor(ele.getClass());
            convertor.java2lua(luaState, ele);
            LuaUtils.checkTopDelta(luaState, top + 1);
            luaState.rawSeti(-2, i + 1); //lua array from 1
        }
        luaState.setCollectionTypeAsMeta(-1, LuaState.COLLECTION_TYPE_LIST);
        return 1;
    }

}