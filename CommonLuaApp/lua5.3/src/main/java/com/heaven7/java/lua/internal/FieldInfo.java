package com.heaven7.java.lua.internal;

import java.lang.reflect.Type;

public final class FieldInfo {

    private String rawName;
    private String name;
    private Type type;
    private Class<?> rawType;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

    public Class<?> getRawType() {
        return rawType;
    }
    public void setRawType(Class<?> rawType) {
        this.rawType = rawType;
    }

    public String getRawName() {
        return rawName;
    }
    public void setRawName(String rawName) {
        this.rawName = rawName;
    }
}
