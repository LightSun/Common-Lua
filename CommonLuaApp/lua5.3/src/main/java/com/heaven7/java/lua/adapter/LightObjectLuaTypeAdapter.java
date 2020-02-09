package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.LuaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;

public final class LightObjectLuaTypeAdapter extends LuaTypeAdapter {

    @Override
    public Object readFromLua(LuaState luaState, LuaValue arg) {
        switch (arg.getType()){
            case LuaValue.TYPE_NULL:
                return null;
            case LuaValue.TYPE_TABLE_LIKE:
                int index = (int) arg.getValuePtr();
                if(luaState.isNativeWrapper(index)){
                    return luaState.getJavaObject(index);
                }
            default:
                throw new UnsupportedOperationException("for LightObjectLuaTypeAdapter. lua-table must be the java object wrapper.");
        }
    }

    @Override
    public int writeToLua(LuaState luaState, Object result) {
        luaState.push(result);
        return 1;
    }
}
