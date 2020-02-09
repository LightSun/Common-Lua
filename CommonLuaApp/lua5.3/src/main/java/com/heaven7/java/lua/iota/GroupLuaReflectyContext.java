package com.heaven7.java.lua.iota;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*public*/ class GroupLuaReflectyContext implements LuaReflectyContext {

    private final List<LuaReflectyContext> contexts;

    public GroupLuaReflectyContext(LuaReflectyContext... contexts) {
        this(Arrays.asList(contexts));
    }
    public GroupLuaReflectyContext(List<LuaReflectyContext> contexts) {
        this.contexts = contexts;
    }

    @Override
    public boolean isSet(Class<?> type) {
        for (LuaReflectyContext context : contexts){
            if(context.isSet(type)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMap(Class<?> type) {
        for (LuaReflectyContext context : contexts){
            if(context.isMap(type)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isList(Class<?> type) {
        for (LuaReflectyContext context : contexts){
            if(context.isList(type)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isCollection(Class<?> type) {
        for (LuaReflectyContext context : contexts){
            if(context.isCollection(type)){
                return true;
            }
        }
        return false;
    }

    @Override
    public List getList(Object obj) {
        List list;
        for (LuaReflectyContext context : contexts){
            list = context.getList(obj);
            if(list != null){
                return list;
            }
        }
        return null;
    }

    @Override
    public Set getSet(Object obj) {
        Set list;
        for (LuaReflectyContext context : contexts){
            list = context.getSet(obj);
            if(list != null){
                return list;
            }
        }
        return null;
    }

    @Override
    public Collection getCollection(Object obj) {
        Collection list;
        for (LuaReflectyContext context : contexts){
            list = context.getCollection(obj);
            if(list != null){
                return list;
            }
        }
        return null;
    }

    @Override
    public Map getMap(Object obj) {
        Map list;
        for (LuaReflectyContext context : contexts){
            list = context.getMap(obj);
            if(list != null){
                return list;
            }
        }
        return null;
    }

    @Override
    public List createList(Class<?> clazz) {
        List list;
        for (LuaReflectyContext context : contexts){
            list = context.createList(clazz);
            if(list != null){
                return list;
            }
        }
        return null;
    }

    @Override
    public Set createSet(Class<?> clazz) {
        Set list;
        for (LuaReflectyContext context : contexts){
            list = context.createSet(clazz);
            if(list != null){
                return list;
            }
        }
        return null;
    }

    @Override
    public Collection createCollection(Class<?> clazz) {
        Collection list;
        for (LuaReflectyContext context : contexts){
            list = context.createCollection(clazz);
            if(list != null){
                return list;
            }
        }
        return null;
    }

    @Override
    public Map createMap(Class<?> clazz) {
        Map list;
        for (LuaReflectyContext context : contexts){
            list = context.createMap(clazz);
            if(list != null){
                return list;
            }
        }
        return null;
    }
    @Override
    public Object createObject(Class<?> clazz) {
        Object bj;
        for (LuaReflectyContext context : contexts){
            bj = context.createObject(clazz);
            if(bj != null){
                return bj;
            }
        }
        return null;
    }
}
