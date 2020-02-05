package com.heaven7.java.lua.iota;


/**
 * the collection or map plugin
 *
 * @author heaven7
 * @since 1.0.3
 */
public abstract class IotaPlugin {

    public static final int TYPE_LIST       = 1;
    public static final int TYPE_SET        = 2;
    public static final int TYPE_COLLECTION = 3;
    public static final int TYPE_MAP        = 4;

    public final Class<?> typeClass;
    public final int type;

    /**
     * create plugin for collection or map.
     * @param type the type
     * @param typeClass the collection or map class
     *             for map, this is key adapter
     */
    protected IotaPlugin(int type, Class<?> typeClass) {
        this.type = type;
        this.typeClass = typeClass;
    }

    /**
     * indicate that this plugin can handle child class or not. for example: if type = List.class. if this
     * method return true. it means. any child class of List. can be handled in this plugin.
     *
     * @return true if can process child type or not. default is false.
     */
    public boolean canProcessChild() {
        return false;
    }
    public abstract Object create(Object obj);

    public abstract Object create(Class<?> clazz);
}