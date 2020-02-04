package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTraveller;
import com.heaven7.java.lua.LuaTypeAdapter;
import com.heaven7.java.lua.internal.LuaUtils;
import com.heaven7.java.lua.iota.LuaReflectyContext;

import java.util.Map;

public class MapLuaTypeAdapter extends LuaTypeAdapter {

    private final LuaReflectyContext mContext;
    private final Class<?> mMapClass;
    private final LuaTypeAdapter mKeyAdapter;
    private final LuaTypeAdapter mValueAdapter;

    public MapLuaTypeAdapter(LuaReflectyContext mContext, Class<?> mMapClass, LuaTypeAdapter mKeyAdapter, LuaTypeAdapter mValueAdapter) {
        this.mContext = mContext;
        this.mMapClass = mMapClass;
        this.mKeyAdapter = mKeyAdapter;
        this.mValueAdapter = mValueAdapter;
    }

    public Object lua2java(LuaState luaState, Lua2JavaValue arg) {
        Map map = mContext.createMap(mMapClass);
        arg.toTableValue(luaState)
                .travel(new MapTraveller(mKeyAdapter, mValueAdapter, map));
        return map;
    }

    @Override
    public int java2lua(LuaState luaState, Object result) {
        Map<?, ?> map = (Map<?, ?>) result;
        final int top = luaState.getTop();
        luaState.newTable();
        for (Map.Entry<?, ?> en : map.entrySet()) {
            Object key = en.getKey();
            Object value = en.getValue();
            mKeyAdapter.java2lua(luaState, key);
            mValueAdapter.java2lua(luaState, value);
            LuaUtils.checkTopDelta(luaState, top + 2);
            luaState.rawSet(-3);
        }
        luaState.setCollectionTypeAsMeta(-1, LuaState.COLLECTION_TYPE_MAP);
        return 1;
    }

    private static class MapTraveller extends LuaTraveller {

        private final LuaTypeAdapter mKeyAdapter;
        private final LuaTypeAdapter mValueAdapter;
        private final Map map;
        LuaState luaState;

        public MapTraveller(LuaTypeAdapter mKeyAdapter, LuaTypeAdapter mValueAdapter, Map map) {
            this.mKeyAdapter = mKeyAdapter;
            this.mValueAdapter = mValueAdapter;
            this.map = map;
        }

        @Override
        public int travel(long luaStatePte, Lua2JavaValue key, Lua2JavaValue value) {
            if (luaState == null) {
                luaState = new LuaState(luaStatePte);
            }
            switch (getCollectionType()) {
                case LuaState.COLLECTION_TYPE_LIST:
                case LuaState.COLLECTION_TYPE_SET:
                    throw new UnsupportedOperationException("list/set can't cast list to map.");

                case LuaState.COLLECTION_TYPE_MAP:
                    Object k = mKeyAdapter.lua2java(luaState, key);
                    Object v = mValueAdapter.lua2java(luaState, value);
                    map.put(k, v);
                    break;

                default:
                    throw new UnsupportedOperationException("wrong collection type = " + getCollectionType());
            }
            return 0;
        }
    }
}
