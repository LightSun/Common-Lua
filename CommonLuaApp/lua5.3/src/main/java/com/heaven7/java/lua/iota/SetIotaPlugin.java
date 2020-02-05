package com.heaven7.java.lua.iota;


import java.util.Set;

/**
 * the collection plugin
 * @author heaven7
 *  @since 1.0.3
 */
public abstract class SetIotaPlugin extends CollectionIotaPlugin {

    /**
     * create collection plugin
     * @param type the collection type.
     */
    public SetIotaPlugin(Class<?> type) {
        super(TYPE_SET, type);
    }

    public abstract Set<?> create(Object obj);

    public abstract Set<?> create(Class<?> clazz);
}
