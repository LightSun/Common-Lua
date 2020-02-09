package com.heaven7.java.lua;

import com.heaven7.java.lua.internal.$ReflectyTypes;
import com.heaven7.java.lua.internal.IotaUtils;

import java.lang.reflect.Type;

public abstract class LuaTypeAdapter {

    public static LuaTypeAdapter get(Type type){
        return IotaUtils.getTypeAdapter($ReflectyTypes.getTypeNode(type),
                LuaInitializer.getLuaTypeAdapterManager());
    }
    public Object defaultValue(){
        return null;
    }
    /**
     * read data from lua
     * @param luaState the lua state
     * @param arg the argument
     * @return the java value which represent lua data
     */
    public abstract Object readFromLua(LuaState luaState, LuaValue arg);

    /**
     * write java object to lua
     * @param luaState the lua stack
     * @param result the java object
     * @return the push result count of lua stack
     */
    public abstract int writeToLua(LuaState luaState, Object result);
}