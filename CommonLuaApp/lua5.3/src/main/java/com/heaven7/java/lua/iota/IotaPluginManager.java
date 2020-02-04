package com.heaven7.java.lua.iota;

import com.heaven7.java.base.util.Predicates;
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
    private List<CollectionIotaPlugin> mCollectionPlugins;
    private List<MapIotaPlugin> mMapPlugins;

    public IotaPluginManager(BasicTypeAdapterProvider mProvider) {
        this.mProvider = mProvider;
    }

    /**
     * add an 'iota' plugin. which used to judge/create the self collection and map.
     * @param plugin the iota plugin
     */
    public void addIotaPlugin(IotaPlugin plugin){
        if(plugin instanceof CollectionIotaPlugin){
            if(mCollectionPlugins == null){
                mCollectionPlugins = new ArrayList<>(5);
            }
            mCollectionPlugins.add((CollectionIotaPlugin) plugin);
        }else if(plugin instanceof MapIotaPlugin){
            if(mMapPlugins == null){
                mMapPlugins = new ArrayList<>(5);
            }
            mMapPlugins.add((MapIotaPlugin) plugin);
        }else {
            throw new UnsupportedOperationException("only support collection and map iota-plugin.");
        }
    }
    public LuaTypeAdapter getKeyAdapter(Class<?> type) {
        if(Predicates.isEmpty(mMapPlugins)){
            return null;
        }
        //first  ==
        for (MapIotaPlugin plugin : mMapPlugins){
            if(plugin.type == type){
                return plugin.getKeyAdapter(mProvider, type);
            }
        }
        //second. child
        for (MapIotaPlugin plugin : mMapPlugins){
            if(plugin.canProcessChild() && plugin.type.isAssignableFrom(type)){
                return plugin.getKeyAdapter(mProvider, type);
            }
        }
        return null;
    }
    public LuaTypeAdapter getValueAdapter(Class<?> type) {
        if(Predicates.isEmpty(mMapPlugins)){
            return null;
        }
        //first  ==
        for (MapIotaPlugin plugin : mMapPlugins){
            if(plugin.type == type){
                return plugin.getValueAdapter(mProvider, type);
            }
        }
        //second. child
        for (MapIotaPlugin plugin : mMapPlugins){
            if(plugin.canProcessChild() && plugin.type.isAssignableFrom(type)){
                return plugin.getValueAdapter(mProvider, type);
            }
        }
        return null;
    }
    public LuaTypeAdapter getElementAdapter(Class<?> type ) {
        if(Predicates.isEmpty(mCollectionPlugins)){
            return null;
        }
        //first ==
        for (CollectionIotaPlugin plugin : mCollectionPlugins){
            if(plugin.type == type){
                return  plugin.getElementAdapter(mProvider, type);
            }
        }
        //second. child
        for (CollectionIotaPlugin plugin : mCollectionPlugins){
            if(plugin.canProcessChild() && plugin.type.isAssignableFrom(type)){
                return  plugin.getElementAdapter(mProvider, type);
            }
        }
        return null;
    }

    //-----------------------------------------------------------------

    @Override
    public boolean isSet(Class<?> type) {
        return false;
    }
    @Override
    public boolean isMap(Class<?> type) {
        if(Predicates.isEmpty(mMapPlugins)){
            return false;
        }
        //first  ==
        for (MapIotaPlugin plugin : mMapPlugins){
            if(plugin.type == type){
                return true;
            }
        }
        //second. child
        for (MapIotaPlugin plugin : mMapPlugins){
            if(plugin.canProcessChild() && plugin.type.isAssignableFrom(type)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isList(Class<?> type) {
        return false;
    }
    @Override
    public Map getMap(Object obj) {
        if(Predicates.isEmpty(mMapPlugins)){
            return null;
        }
        Class<?> type = obj.getClass();
        //first  ==
        for (MapIotaPlugin plugin : mMapPlugins){
            if(plugin.type == type){
                return plugin.createMap(obj);
            }
        }
        //second. child
        for (MapIotaPlugin plugin : mMapPlugins){
            if(plugin.canProcessChild() && plugin.type.isAssignableFrom(type)){
                return plugin.createMap(obj);
            }
        }
        return null;
    }

    @Override
    public List createList(Class<?> clazz) {
        return null;
    }
    @Override
    public Set createSet(Class<?> clazz) {
        return null;
    }

    @Override
    public Collection createCollection(Class<?> clazz) {
        return null;
    }

    @Override
    public Map createMap(Class<?> clazz) {
        return null;
    }

    @Override
    public boolean isCollection(Class<?> type) {
        if(Predicates.isEmpty(mCollectionPlugins)){
            return false;
        }
        //first  ==
        for (CollectionIotaPlugin plugin : mCollectionPlugins){
            if(plugin.type == type){
                return true;
            }
        }
        //second. child
        for (CollectionIotaPlugin plugin : mCollectionPlugins){
            if(plugin.canProcessChild() && plugin.type.isAssignableFrom(type)){
                return true;
            }
        }
        return false;
    }

    @Override
    public List getList(Object obj) {
        return null;
    }

    @Override
    public Set getSet(Object obj) {
        return null;
    }

    @Override
    public Collection getCollection(Object obj) {
        if(Predicates.isEmpty(mCollectionPlugins)){
            return null;
        }
        Class<?> type = obj.getClass();
        //first  ==
        for (CollectionIotaPlugin plugin : mCollectionPlugins){
            if(plugin.type == type){
                return plugin.createCollection(obj);
            }
        }
        //second. child
        for (CollectionIotaPlugin plugin : mCollectionPlugins){
            if(plugin.canProcessChild() && plugin.type.isAssignableFrom(type)){
                return plugin.createCollection(obj);
            }
        }
        return null;
    }
}
