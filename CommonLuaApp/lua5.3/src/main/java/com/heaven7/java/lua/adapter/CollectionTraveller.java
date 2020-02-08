package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTraveller;
import com.heaven7.java.lua.LuaTypeAdapter;

import java.util.Collection;

/*public*/ class CollectionTraveller extends LuaTraveller {

    private final LuaTypeAdapter componentAdapter;
    private final Collection coll;
    LuaState luaState;

    public CollectionTraveller(LuaTypeAdapter componentAdapter, Collection coll) {
        this.componentAdapter = componentAdapter;
        this.coll = coll;
    }

    @Override
    public int travel(long luaStatePte, Lua2JavaValue key, Lua2JavaValue value) {
        if (luaState == null) {
            luaState = new LuaState(luaStatePte);
        }
        switch (getCollectionType()) {
            case LuaState.COLLECTION_TYPE_LIST: {
                Object o = componentAdapter.readFromLua(luaState, value);
                if (o != null) {
                    coll.add(o);
                }
            }break;

            case LuaState.COLLECTION_TYPE_SET:
                Object o = componentAdapter.readFromLua(luaState, key);
                if (o != null) {
                    coll.add(o);
                }
                break;

            case LuaState.COLLECTION_TYPE_MAP:
                throw new UnsupportedOperationException("can't cast map to list.");

            default:
                throw new UnsupportedOperationException("wrong collection type = " + getCollectionType());
        }
        return 0;
    }
}