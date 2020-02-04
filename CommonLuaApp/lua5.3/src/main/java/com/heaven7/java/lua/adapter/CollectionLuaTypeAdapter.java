package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;
import com.heaven7.java.lua.internal.LuaUtils;
import com.heaven7.java.lua.iota.LuaReflectyContext;

import java.util.Collection;
import java.util.Iterator;

public class CollectionLuaTypeAdapter extends LuaTypeAdapter {

    private final LuaReflectyContext mContext;
    private final Class<?> mClass;
    private final LuaTypeAdapter mComponentAdapter;

    public CollectionLuaTypeAdapter(LuaReflectyContext mContext, Class<?> mClass, LuaTypeAdapter mComponentAdapter) {
        this.mContext = mContext;
        this.mClass = mClass;
        this.mComponentAdapter = mComponentAdapter;
    }

    public Object lua2java(LuaState luaState, Lua2JavaValue arg){
        Collection list = mContext.createCollection(mClass);
        arg.toTableValue(luaState).travel(new CollectionTraveller(mComponentAdapter, list));
        return list;
    }

    public int java2lua(LuaState luaState, Object result){
        luaState.newTable();
        int top = luaState.getTop();
        final Collection<?> coll = (Collection) result;
        int i = 0;
        for (Iterator<?> it = coll.iterator(); it.hasNext() ; i++ ){
            Object ele = it.next();
            mComponentAdapter.java2lua(luaState, ele);
            LuaUtils.checkTopDelta(luaState, top + 1);
            luaState.rawSeti(-2, i + 1); //lua array from 1
        }
        luaState.setCollectionTypeAsMeta(-1, LuaState.COLLECTION_TYPE_LIST);
        return 1;
    }

}