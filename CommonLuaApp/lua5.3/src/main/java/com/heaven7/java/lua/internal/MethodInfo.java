package com.heaven7.java.lua.internal;

import java.lang.reflect.Type;

public final class MethodInfo{
    private String name;
    private String sig;  //"(Ljava/lang/String;Z)V"
    private Type[] types;
    private Class<?>[] rawTypes;
    private String rawName;

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

    public String getSig() {
        return sig;
    }
    public void setSig(String sig) {
        this.sig = sig;
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
}