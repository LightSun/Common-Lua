package com.heaven7.java.lua.iota;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LuaReflectyContext {

    boolean isSet(Class<?> type);
    boolean isMap(Class<?> type);
    boolean isList(Class<?> type);
    boolean isCollection(Class<?> type);

    List getList(Object obj);
    Set getSet(Object obj);
    Collection getCollection(Object obj);
    Map getMap(Object obj);

    List createList(Class<?> clazz);
    Set createSet(Class<?> clazz);
    Collection createCollection(Class<?> clazz);
    Map createMap(Class<?> clazz);
}
