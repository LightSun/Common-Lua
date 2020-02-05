package com.heaven7.java.lua.iota;

import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.base.util.SparseArrayDelegate;
import com.heaven7.java.base.util.SparseFactory;
import com.heaven7.java.lua.LuaTypeAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * the iota plugin manager
 * @since 1.0.3
 * @author heaven7
 */
/*public*/ class IotaPluginManager implements LuaReflectyContext {

    private final BasicTypeAdapterProvider mProvider;
    private final SparseArrayDelegate<List<IotaPlugin>> mPluginMap = SparseFactory.newSparseArray(5);

    public IotaPluginManager(BasicTypeAdapterProvider mProvider) {
        this.mProvider = mProvider;
    }

    /**
     * add an 'iota' plugin. which used to judge/create the self collection and map.
     * @param plugin the iota plugin
     */
    public void addIotaPlugin(IotaPlugin plugin){
        List<IotaPlugin> pluginList = mPluginMap.get(plugin.type);
        if(pluginList == null){
            pluginList = new ArrayList<>();
            mPluginMap.put(plugin.type, pluginList);
        }
        pluginList.add(plugin);
    }

    public LuaTypeAdapter getKeyAdapter(Class<?> type) {
        List<IotaPlugin> mMapPlugins = mPluginMap.get(IotaPlugin.TYPE_MAP);
        if(Predicates.isEmpty(mMapPlugins)){
            return null;
        }
        //first  ==
        for (IotaPlugin p : mMapPlugins){
            if(p.typeClass == type){
                return ((MapIotaPlugin)p).getKeyAdapter(mProvider, type);
            }
        }
        //second. child
        for (IotaPlugin p : mMapPlugins){
            if(p.canProcessChild() && p.typeClass.isAssignableFrom(type)){
                return ((MapIotaPlugin)p).getKeyAdapter(mProvider, type);
            }
        }
        return null;
    }
    public LuaTypeAdapter getValueAdapter(Class<?> type) {
        List<IotaPlugin> mMapPlugins = mPluginMap.get(IotaPlugin.TYPE_MAP);
        if(Predicates.isEmpty(mMapPlugins)){
            return null;
        }
        //first  ==
        for (IotaPlugin p : mMapPlugins){
            if(p.typeClass == type){
                return ((MapIotaPlugin)p).getValueAdapter(mProvider, type);
            }
        }
        //second. child
        for (IotaPlugin p : mMapPlugins){
            if(p.canProcessChild() && p.typeClass.isAssignableFrom(type)){
                return ((MapIotaPlugin)p).getValueAdapter(mProvider, type);
            }
        }
        return null;
    }

    //-----------------------------------------------------------------

    @Override
    public boolean isSet(Class<?> type) {
        return hasType(mPluginMap.get(IotaPlugin.TYPE_SET), type);
    }
    @Override
    public boolean isList(Class<?> type) {
        return hasType(mPluginMap.get(IotaPlugin.TYPE_LIST), type);
    }
    @Override
    public boolean isCollection(Class<?> type) {
        return hasType(mPluginMap.get(IotaPlugin.TYPE_COLLECTION), type);
    }
    @Override
    public boolean isMap(Class<?> type) {
        return hasType(mPluginMap.get(IotaPlugin.TYPE_MAP), type);
    }

    @Override
    public Map getMap(Object obj) {
        return create(mPluginMap.get(IotaPlugin.TYPE_MAP), obj);
    }
    @Override
    public List getList(Object obj) {
        return create(mPluginMap.get(IotaPlugin.TYPE_LIST), obj);
    }

    @Override
    public Set getSet(Object obj) {
        return create(mPluginMap.get(IotaPlugin.TYPE_SET), obj);
    }

    @Override
    public Collection getCollection(Object obj) {
        return create(mPluginMap.get(IotaPlugin.TYPE_COLLECTION), obj);
    }

    @Override
    public List createList(Class<?> clazz) {
        return create(mPluginMap.get(IotaPlugin.TYPE_LIST), clazz);
    }
    @Override
    public Set createSet(Class<?> clazz) {
        return create(mPluginMap.get(IotaPlugin.TYPE_SET), clazz);
    }
    @Override
    public Collection createCollection(Class<?> clazz) {
        return create(mPluginMap.get(IotaPlugin.TYPE_COLLECTION), clazz);
    }
    @Override
    public Map createMap(Class<?> clazz) {
        return create(mPluginMap.get(IotaPlugin.TYPE_MAP), clazz);
    }

    public LuaTypeAdapter getSetElementAdapter(Class<?> type) {
        return getElementAdapter(mPluginMap.get(IotaPlugin.TYPE_SET), type);
    }
    public LuaTypeAdapter getListElementAdapter(Class<?> type) {
        return getElementAdapter(mPluginMap.get(IotaPlugin.TYPE_LIST), type);
    }
    public LuaTypeAdapter getCollectionElementAdapter(Class<?> type) {
        return getElementAdapter(mPluginMap.get(IotaPlugin.TYPE_COLLECTION), type);
    }

    private LuaTypeAdapter getElementAdapter(List<IotaPlugin> plugins, Class<?> type) {
        if(Predicates.isEmpty(plugins)){
            return null;
        }
        final BasicTypeAdapterProvider mProvider = this.mProvider;
        //first  ==
        for (IotaPlugin plugin : plugins){
            if(plugin.typeClass == type){
                return ((CollectionIotaPlugin)plugin).getElementAdapter(mProvider, type);
            }
        }
        //second. child
        for (IotaPlugin plugin : plugins){
            if(plugin.canProcessChild() && plugin.typeClass.isAssignableFrom(type)){
                return ((CollectionIotaPlugin)plugin).getElementAdapter(mProvider, type);
            }
        }
        return null;
    }
    private static boolean hasType(List<IotaPlugin> plugins, Class<?> type) {
        if(Predicates.isEmpty(plugins)){
            return false;
        }
        //first  ==
        for (IotaPlugin plugin : plugins){
            if(plugin.typeClass == type){
                return true;
            }
        }
        //second. child
        for (IotaPlugin plugin : plugins){
            if(plugin.canProcessChild() && plugin.typeClass.isAssignableFrom(type)){
                return true;
            }
        }
        return false;
    }
    @SuppressWarnings("unchecked")
    private static <T> T create(List<IotaPlugin> plugins, Object obj){
        if(Predicates.isEmpty(plugins)){
            return null;
        }
        Class<?> type = obj.getClass();
        //first  ==
        for (IotaPlugin plugin : plugins){
            if(plugin.typeClass == type){
                return (T) plugin.create(obj);
            }
        }
        //second. child
        for (IotaPlugin plugin : plugins){
            if(plugin.canProcessChild() && plugin.typeClass.isAssignableFrom(type)){
                return (T) plugin.create(obj);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> T create(List<IotaPlugin> plugins, Class<?> clazz){
        if(Predicates.isEmpty(plugins)){
            return null;
        }
        //first  ==
        for (IotaPlugin plugin : plugins){
            if(plugin.typeClass == clazz){
                return (T) plugin.create(clazz);
            }
        }
        //second. child
        for (IotaPlugin plugin : plugins){
            if(plugin.canProcessChild() && plugin.typeClass.isAssignableFrom(clazz)){
                return (T) plugin.create(clazz);
            }
        }
        return null;
    }
}
