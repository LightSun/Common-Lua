package com.heaven7.java.lua.internal;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

public final class MethodInfo{
    private String name;
    private String rawName;
    //private String sig;  //"(Ljava/lang/String;Z)V"
    private Type[] types;
    private Class<?>[] rawTypes;

    public Class<?>[] getRawTypes() {
        return rawTypes;
    }
    public void setRawTypes(Class<?>[] rawTypes) {
        this.rawTypes = rawTypes;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getParameterCount(){
        return types != null ? types.length : 0;
    }

    public Type[] getTypes() {
        return types;
    }
    public void setTypes(Type[] types) {
        this.types = types;
    }

    public void setRawName(String rawName) {
        this.rawName = rawName;
    }
    public String getRawName() {
        return rawName;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodInfo that = (MethodInfo) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(rawName, that.rawName) &&
                Arrays.equals(types, that.types);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawName, name, types);
    }
    @Override
    public String toString() {
        return "MethodInfo{" +
                "name='" + name + '\'' +
                ", rawName='" + rawName + '\'' +
                ", types=" + Arrays.toString(types) +
                '}';
    }
}