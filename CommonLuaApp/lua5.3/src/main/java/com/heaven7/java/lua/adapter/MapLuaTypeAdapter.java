package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.LuaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTraveller;
import com.heaven7.java.lua.LuaTypeAdapter;
import com.heaven7.java.lua.internal.LuaUtils;
import com.heaven7.java.lua.iota.LuaReflectyContext;
import com.heaven7.java.lua.iota.Wrapper;

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

    public Object readFromLua(LuaState luaState, LuaValue arg) {
        Map map = mContext.createMap(mMapClass);
        arg.toTableValue(luaState)
                .travel(new MapTraveller(mKeyAdapter, mValueAdapter, map));
        return map instanceof Wrapper ? ((Wrapper) map).unwrap(): map;
    }

    @Override
    public int writeToLua(LuaState luaState, Object result) {
        luaState.newTable();
        Map<?, ?> map = mContext.getMap(result);
        final int top = luaState.getTop();

        for (Map.Entry<?, ?> en : map.entrySet()) {
            Object key = en.getKey();
            Object value = en.getValue();
            mKeyAdapter.writeToLua(luaState, key);
            mValueAdapter.writeToLua(luaState, value);
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
        public int travel(long luaStatePte, LuaValue key, LuaValue value) {
            if (luaState == null) {
                luaState = new LuaState(luaStatePte);
            }
            switch (getCollectionType()) {
                case LuaState.COLLECTION_TYPE_LIST:
                case LuaState.COLLECTION_TYPE_SET:
                    throw new UnsupportedOperationException("list/set can't cast list to map.");

                case LuaState.COLLECTION_TYPE_MAP:
                    Object k = mKeyAdapter.readFromLua(luaState, key);
                    Object v = mValueAdapter.readFromLua(luaState, value);
                    if(k == null || v == null){
                        return 0;
                    }
                    map.put(k, v);
                    break;

                default:
                    throw new UnsupportedOperationException("wrong collection type = " + getCollectionType());
            }
            return 0;
        }
    }
}
