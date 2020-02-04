package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;
import com.heaven7.java.lua.internal.LuaUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ArrayLuaTypeAdapter extends LuaTypeAdapter {

    private final Class<?> mComponentClass;
    private final LuaTypeAdapter mComponentAdapter;

    public ArrayLuaTypeAdapter(Class<?> mComponentClass, LuaTypeAdapter mComponentAdapter) {
        this.mComponentClass = mComponentClass;
        this.mComponentAdapter = mComponentAdapter;
    }

    public Object lua2java(LuaState luaState, Lua2JavaValue arg){
        List list = new ArrayList();
        arg.toTableValue(luaState).travel(new CollectionTraveller(mComponentAdapter, list));
        return list.toArray((Object[]) Array.newInstance(mComponentClass, list.size()));
    }

    public int java2lua(LuaState luaState, Object result){
        luaState.newTable();
        int top = luaState.getTop();
        int length = Array.getLength(result);
        for (int i = 0 ; i < length ; i ++){
            Object ele = Array.get(result, i);
            //must only add one to lua stack
            mComponentAdapter.java2lua(luaState, ele);
            LuaUtils.checkTopDelta(luaState, top + 1);
            luaState.rawSeti(-2, i + 1); //lua array from 1
        }
        luaState.setCollectionTypeAsMeta(-1, LuaState.COLLECTION_TYPE_LIST);
        return 1;
    }

}
