package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;

public final class LightObjectLuaTypeAdapter extends LuaTypeAdapter {

    @Override
    public Object readFromLua(LuaState luaState, Lua2JavaValue arg) {
        switch (arg.getType()){
            case Lua2JavaValue.TYPE_NULL:
                return null;
            case Lua2JavaValue.TYPE_TABLE_LIKE:
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
