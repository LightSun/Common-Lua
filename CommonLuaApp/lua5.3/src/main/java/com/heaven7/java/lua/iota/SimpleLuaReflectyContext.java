package com.heaven7.java.lua.iota;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class SimpleLuaReflectyContext implements LuaReflectyContext {

    @Override
    public boolean isSet(Class<?> type) {
        return Set.class.isAssignableFrom(type);
    }

    @Override
    public boolean isMap(Class<?> type) {
        return Map.class.isAssignableFrom(type);
    }

    @Override
    public boolean isList(Class<?> type) {
        return List.class.isAssignableFrom(type);
    }
    @Override
    public boolean isCollection(Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }

    @Override
    public List getList(Object obj) {
        if(obj instanceof List){
            return (List) obj;
        }
        return null;
    }

    @Override
    public Set getSet(Object obj) {
        if(obj instanceof Set){
            return (Set) obj;
        }
        return null;
    }

    @Override
    public Collection getCollection(Object obj) {
        if(obj instanceof Collection){
            return (Collection) obj;
        }
        return null;
    }

    @Override
    public Map getMap(Object obj) {
        if(obj instanceof Map){
            return (Map) obj;
        }
        return null;
    }

    @Override
    public List createList(Class<?> clazz) {
        if(LinkedList.class.isAssignableFrom(clazz)){
            return new LinkedList();
        }else if(Vector.class.isAssignableFrom(clazz)){
            return new Vector();
        }else if(CopyOnWriteArrayList.class.isAssignableFrom(clazz)){
            return new CopyOnWriteArrayList();
        }
        else if(List.class.isAssignableFrom(clazz)) {
            return new ArrayList();
        }
        return null;
    }

    @Override
    public Set createSet(Class<?> clazz) {
        if(CopyOnWriteArraySet.class.isAssignableFrom(clazz)){
            return new CopyOnWriteArraySet();
        } else if(SortedSet.class.isAssignableFrom(clazz)){
            return new TreeSet();
        }else if(Set.class.isAssignableFrom(clazz)){
            return new HashSet();
        }
        return null;
    }

    @Override
    public Collection createCollection(Class<?> clazz) {
        if(LinkedList.class.isAssignableFrom(clazz)){
            return new LinkedList();
        }else if(Vector.class.isAssignableFrom(clazz)){
            return new Vector();
        }else if(CopyOnWriteArrayList.class.isAssignableFrom(clazz)){
            return new CopyOnWriteArrayList();
        }
        else if(List.class.isAssignableFrom(clazz)){
            return new ArrayList();
        }else if(CopyOnWriteArraySet.class.isAssignableFrom(clazz)){
            return new CopyOnWriteArraySet();
        }
        else if(SortedSet.class.isAssignableFrom(clazz)){
            return new TreeSet();
        }else if(Set.class.isAssignableFrom(clazz)){
            return new HashSet();
        }
        return null;
    }

    @Override
    public Map createMap(Class<?> clazz) {
        if(ConcurrentHashMap.class.isAssignableFrom(clazz)){
            return new ConcurrentHashMap();
        }
        else if(WeakHashMap.class.isAssignableFrom(clazz)){
            return new WeakHashMap();
        }
        else if(LinkedHashMap.class.isAssignableFrom(clazz)){
            return new LinkedHashMap();
        }
        else if(SortedMap.class.isAssignableFrom(clazz)){
            return new TreeMap();
        }
        else if(Map.class.isAssignableFrom(clazz)){
            return new HashMap();
        }
        return null;
    }
    @Override
    public Object createObject(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
