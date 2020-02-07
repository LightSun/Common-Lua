package com.heaven7.java.lua.iota;

import com.heaven7.java.lua.LuaTypeAdapter;

import java.util.Map;

/**
 * the map plugin
 * @author heaven7
 *  @since 1.0.3
 */
public abstract class MapIotaPlugin extends IotaPlugin {

     public MapIotaPlugin(Class<?> type) {
         super(TYPE_MAP, type);
     }

    public abstract Map<?,?> create(Object obj);

    public abstract Map<?,?> create(Class<?> clazz);

    /**
     * get the key adapter for target map class type. this often used when you want to use a self-type map.
     * can non-extend {@linkplain Map}. see {@linkplain LuaReflectyContext#isMap(Class)} and {@linkplain LuaReflectyContext#createMap(Class)}.
     * @param provider the type adapter provider. used to get basic type {@linkplain TypeAdapter}.
     * @param type the class. can be {@linkplain com.heaven7.java.base.util.SparseArrayDelegate}.
     * @return the key type adapter
     */
    public abstract LuaTypeAdapter getKeyAdapter(BasicTypeAdapterProvider provider, Class<?> type);
    /**
     * get the value adapter for target map class type. this often used when you want to use a self-type map.
     * can non-extend {@linkplain Map}. see {@linkplain LuaReflectyContext#isMap(Class)} and {@linkplain LuaReflectyContext#createMap(Class)}.
     * @param provider the type adapter provider. used to get basic type {@linkplain TypeAdapter}.
     * @param type the class . can be any class like {@linkplain Map}.
     * @return the value type adapter. or null if you haven't use self-type map.
     */
    public abstract LuaTypeAdapter getValueAdapter(BasicTypeAdapterProvider provider, Class<?> type);
 }
