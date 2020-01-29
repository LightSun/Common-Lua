package com.heaven7.java.lua.convertors;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaUtils;
import com.heaven7.java.lua.TypeConvertor;

import java.util.Map;

public class MapTypeConvertor extends NonSimpleTypeConvertor {

    public Object convert(Lua2JavaValue arg){
        throw new UnsupportedOperationException("latter will support.");
    }

    @Override
    public void convert(LuaState luaState, Object result) {
        Map<?,?> map = (Map<?, ?>) result;
        int top = luaState.getTop();
        luaState.newTable();
        for (Map.Entry<?,?> en : map.entrySet()){
            Object key = en.getKey();
            Object value = en.getValue();
            TypeConvertor kc = TypeConvertorFactory.getTypeConvertor(key.getClass());
            TypeConvertor vc = TypeConvertorFactory.getTypeConvertor(value.getClass());
            kc.convert(luaState, key);
            vc.convert(luaState, value);
            LuaUtils.checkTopDelta(luaState, top + 2);
            luaState.rawSet(-3);
        }
    }
}
