package com.heaven7.java.lua.iota;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GroupLuaReflectyContext implements LuaReflectyContext {

    private final List<LuaReflectyContext> contexts;

    public GroupLuaReflectyContext(LuaReflectyContext... contexts) {
        this(Arrays.asList(contexts));
    }
    public GroupLuaReflectyContext(List<LuaReflectyContext> contexts) {
        this.contexts = contexts;
    }

    @Override
    public boolean isSet(Class<?> type) {
        return false;
    }

    @Override
    public boolean isMap(Class<?> type) {
        return false;
    }

    @Override
    public boolean isList(Class<?> type) {
        return false;
    }

    @Override
    public boolean isCollection(Class<?> type) {
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
        return null;
    }

    @Override
    public Map getMap(Object obj) {
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
}
