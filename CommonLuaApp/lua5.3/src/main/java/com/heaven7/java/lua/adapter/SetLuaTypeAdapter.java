package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;
import com.heaven7.java.lua.internal.LuaUtils;
import com.heaven7.java.lua.iota.LuaReflectyContext;
import com.heaven7.java.lua.iota.Wrapper;

import java.util.Iterator;
import java.util.Set;

public class SetLuaTypeAdapter extends LuaTypeAdapter {

    private final LuaReflectyContext mContext;
    private final Class<?> mClass;
    private final LuaTypeAdapter mComponentAdapter;

    public SetLuaTypeAdapter(LuaReflectyContext mContext, Class<?> mClass, LuaTypeAdapter mComponentAdapter) {
        this.mContext = mContext;
        this.mClass = mClass;
        this.mComponentAdapter = mComponentAdapter;
    }

    public Object readFromLua(LuaState luaState, Lua2JavaValue arg){
        Set list = mContext.createSet(mClass);
        arg.toTableValue(luaState).travel(new CollectionTraveller(mComponentAdapter, list));
        return list instanceof Wrapper ? ((Wrapper) list).unwrap(): list;
    }

    public int writeToLua(LuaState luaState, Object result){
        luaState.newTable();
        final int top = luaState.getTop();
        final Set<?> set = mContext.getSet(result);
        for (Iterator<?> it = set.iterator(); it.hasNext() ; ){
            Object ele = it.next();
            mComponentAdapter.writeToLua(luaState, ele);
            LuaUtils.checkTopDelta(luaState, top + 1);
            luaState.pushBoolean(true);
            luaState.rawSet(-3);
        }
        luaState.setCollectionTypeAsMeta(-1, LuaState.COLLECTION_TYPE_SET);
        return 1;
    }
}