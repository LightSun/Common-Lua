package com.heaven7.java.lua.iota;


import com.heaven7.java.lua.LuaTypeAdapter;

import java.util.Collection;

/**
 * the collection plugin
 * @author heaven7
 *  @since 1.0.3
 */
public abstract class CollectionIotaPlugin extends IotaPlugin {

    /**
     * create collection plugin
     * @param type the collection type.
     */
    public CollectionIotaPlugin(Class<?> type) {
        super(TYPE_COLLECTION, type);
    }

    /*protected*/ CollectionIotaPlugin(int type, Class<?> typeClass) {
        super(type, typeClass);
    }

    /**
     * get the element type adapter for collection class type. this often used when you want to use a self-type collection.
     * can non-extend {@linkplain Collection}. see {@linkplain LuaReflectyContext#isCollection(Class)} and {@linkplain LuaReflectyContext#createCollection(Class)}.
     * @param type the collection class. can be any similar collection. like {@linkplain Collection}
     * @param provider the type adapter provider. used to get basic type {@linkplain TypeAdapter}.
     * @return the element type adapter. or null if you haven't use self-type collection.
     */
    public abstract LuaTypeAdapter getElementAdapter(BasicTypeAdapterProvider provider, Class<?> type);

    public abstract Collection<?> create(Object obj);

    public abstract Collection<?> create(Class<?> clazz);

}
