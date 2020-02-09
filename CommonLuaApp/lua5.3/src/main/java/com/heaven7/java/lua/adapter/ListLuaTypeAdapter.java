package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.LuaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;
import com.heaven7.java.lua.internal.LuaUtils;
import com.heaven7.java.lua.iota.LuaReflectyContext;
import com.heaven7.java.lua.iota.Wrapper;

import java.util.List;

public class ListLuaTypeAdapter extends LuaTypeAdapter {

    private final LuaReflectyContext mContext;
    private final Class<?> mListClass;
    private final LuaTypeAdapter mComponentAdapter;

    public ListLuaTypeAdapter(LuaReflectyContext mContext, Class<?> mListClass, LuaTypeAdapter mComponentAdapter) {
        this.mContext = mContext;
        this.mListClass = mListClass;
        this.mComponentAdapter = mComponentAdapter;
    }

    public Object readFromLua(LuaState luaState, LuaValue arg){
        List list = mContext.createList(mListClass);
        arg.toTableValue(luaState).travel(new CollectionTraveller(mComponentAdapter, list));
        return list instanceof Wrapper ? ((Wrapper) list).unwrap(): list;
    }

    public int writeToLua(LuaState luaState, Object result){
        luaState.newTable();
        final int top = luaState.getTop();
        final List list = mContext.getList(result);
        for (int i = 0, size = list.size() ; i < size ; i ++){
            Object ele = list.get(i);
            //must only add one to lua stack
            mComponentAdapter.writeToLua(luaState, ele);
            //LuaUtils.java2lua(luaState, ele);
            LuaUtils.checkTopDelta(luaState, top + 1);
            luaState.rawSeti(-2, i + 1); //lua array from 1
        }
        luaState.setCollectionTypeAsMeta(-1, LuaState.COLLECTION_TYPE_LIST);
        return 1;
    }
}