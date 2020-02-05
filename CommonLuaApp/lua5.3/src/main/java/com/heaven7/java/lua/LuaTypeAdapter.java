package com.heaven7.java.lua;

import com.heaven7.java.lua.internal.$ReflectyTypes;
import com.heaven7.java.lua.internal.IotaUtils;
import com.heaven7.java.lua.iota.LuaTypeAdapterManager;

import java.lang.reflect.Type;

public abstract class LuaTypeAdapter {

    public static LuaTypeAdapter get(Type type){
        return get(type, LuaInitializer.getLuaTypeAdapterManager());
    }
    public static LuaTypeAdapter get(Type type, LuaTypeAdapterManager tam){
        if(type instanceof Class){
            LuaTypeAdapter lta = tam.getBasicTypeAdapter((Class<?>) type);
            if(lta != null){
                return lta;
            }
        }
        return IotaUtils.getTypeAdapter($ReflectyTypes.getTypeNode(type), tam);
    }
    public Object defaultValue(){
        return null;
    }
    /**
     * convert lua data to java
     * @param luaState the lua state
     * @param arg the argument
     * @return the java value which represent lua data
     */
    public abstract Object lua2java(LuaState luaState, Lua2JavaValue arg);

    /**
     * convert java object to lua
     * @param luaState the lua stack
     * @param result the java object
     * @return the push result count of lua stack
     */
    public abstract int java2lua(LuaState luaState, Object result);
}