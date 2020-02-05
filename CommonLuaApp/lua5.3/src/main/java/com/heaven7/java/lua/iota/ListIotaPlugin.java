package com.heaven7.java.lua.iota;


import java.util.List;

/**
 * the collection plugin
 * @author heaven7
 *  @since 1.0.3
 */
public abstract class ListIotaPlugin extends CollectionIotaPlugin {

    /**
     * create collection plugin
     * @param type the collection type.
     */
    public ListIotaPlugin(Class<?> type) {
        super(TYPE_LIST, type);
    }

    public abstract List<?> create(Object obj);

    public abstract List<?> create(Class<?> clazz);
}
