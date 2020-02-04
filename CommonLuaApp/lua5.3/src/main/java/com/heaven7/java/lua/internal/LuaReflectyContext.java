package com.heaven7.java.lua.internal;

public interface LuaReflectyContext {

    boolean isSet(Class<?> type);

    boolean isMap(Class<?> type);

    boolean isList(Class<?> type);

    boolean isCollection(Class<?> type);
}
