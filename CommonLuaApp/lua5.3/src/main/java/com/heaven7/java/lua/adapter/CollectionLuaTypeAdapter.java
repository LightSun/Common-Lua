package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.LuaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;
import com.heaven7.java.lua.internal.LuaUtils;
import com.heaven7.java.lua.iota.LuaReflectyContext;
import com.heaven7.java.lua.iota.Wrapper;

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

    public Object readFromLua(LuaState luaState, LuaValue arg){
        Collection list = mContext.createCollection(mClass);
        arg.toTableValue(luaState).travel(new CollectionTraveller(mComponentAdapter, list));
        return list instanceof Wrapper ? ((Wrapper) list).unwrap(): list;
    }

    public int writeToLua(LuaState luaState, Object result){
        luaState.newTable();
        int top = luaState.getTop();
        final Collection<?> coll = mContext.getCollection(result);
        int i = 0;
        for (Iterator<?> it = coll.iterator(); it.hasNext() ; i++ ){
            Object ele = it.next();
            mComponentAdapter.writeToLua(luaState, ele);
            LuaUtils.checkTopDelta(luaState, top + 1);
            luaState.rawSeti(-2, i + 1); //lua array from 1
        }
        luaState.setCollectionTypeAsMeta(-1, LuaState.COLLECTION_TYPE_LIST);
        return 1;
    }

}