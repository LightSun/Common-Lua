package com.heaven7.java.lua.convertors;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;
import com.heaven7.java.lua.internal.LuaUtils;

import java.util.Map;

public class MapLuaTypeAdapter extends LuaTypeAdapter {

    public Object lua2java(LuaState luaState, Lua2JavaValue arg){
        throw new UnsupportedOperationException("latter will support.");
    }

    @Override
    public int java2lua(LuaState luaState, Object result) {
        Map<?,?> map = (Map<?, ?>) result;
        int top = luaState.getTop();
        luaState.newTable();
        for (Map.Entry<?,?> en : map.entrySet()){
            Object key = en.getKey();
            Object value = en.getValue();
            LuaTypeAdapter kc = TypeConvertorFactory.getTypeConvertor(key.getClass());
            LuaTypeAdapter vc = TypeConvertorFactory.getTypeConvertor(value.getClass());
            kc.java2lua(luaState, key);
            vc.java2lua(luaState, value);
            LuaUtils.checkTopDelta(luaState, top + 2);
            luaState.rawSet(-3);
        }
        luaState.setCollectionTypeAsMeta(-1, LuaState.COLLECTION_TYPE_MAP);
        return 1;
    }
}
