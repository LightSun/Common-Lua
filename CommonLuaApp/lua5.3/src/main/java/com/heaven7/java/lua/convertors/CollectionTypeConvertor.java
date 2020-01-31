package com.heaven7.java.lua.convertors;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaUtils;
import com.heaven7.java.lua.TypeConvertor;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CollectionTypeConvertor extends NonSimpleTypeConvertor {

    public Object lua2java(LuaState luaState, Lua2JavaValue arg){
        throw new UnsupportedOperationException("latter will support.");
    }
    @Override
    public int java2lua(LuaState luaState, Object result){
        Collection<?> coll = (Collection<?>) result;
        luaState.newTable();
        int top = luaState.getTop();
        int length = coll.size();
        if(coll instanceof List){
            List list = (List) result;
            for (int i = 0 ; i < length ; i ++){
                Object ele = list.get(i);
                //must only add one to lua stack
                TypeConvertor convertor = TypeConvertorFactory.getTypeConvertor(ele.getClass());
                convertor.java2lua(luaState, ele);
                LuaUtils.checkTopDelta(luaState, top + 1);
                luaState.rawSeti(-2, i + 1); //lua array from 1
            }
        }else if(coll instanceof Set){
            for (Iterator<?> it = coll.iterator(); it.hasNext() ; ){
                Object ele = it.next();
                TypeConvertor convertor = TypeConvertorFactory.getTypeConvertor(ele.getClass());
                convertor.java2lua(luaState, ele);
                LuaUtils.checkTopDelta(luaState, top + 1);
                luaState.pushBoolean(true);
                luaState.rawSet(-3);
            }
        } else {
            int i = 0;
            for (Iterator<?> it = coll.iterator(); it.hasNext() ; i++ ){
                Object ele = it.next();
                TypeConvertor convertor = TypeConvertorFactory.getTypeConvertor(ele.getClass());
                convertor.java2lua(luaState, ele);
                LuaUtils.checkTopDelta(luaState, top + 1);
                luaState.rawSeti(-2, i + 1); //lua array from 1
            }
        }
        return 1;
    }
}
