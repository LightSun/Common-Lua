package com.heaven7.java.lua.internal;

import java.lang.reflect.Type;
import java.util.Objects;

public final class FieldInfo {

    private String rawName;
    private String name;
    private Type type;

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

    public String getRawName() {
        return rawName;
    }
    public void setRawName(String rawName) {
        this.rawName = rawName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldInfo fieldInfo = (FieldInfo) o;
        return Objects.equals(rawName, fieldInfo.rawName) &&
                Objects.equals(name, fieldInfo.name) &&
                Objects.equals(type, fieldInfo.type);
    }
    @Override
    public int hashCode() {
        return Objects.hash(rawName, name, type);
    }
    @Override
    public String toString() {
        return "FieldInfo{" +
                "rawName='" + rawName + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
