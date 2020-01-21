package com.heaven7.java.lua;

public final class MethodInfo extends INativeObject.BaseNativeObject{
    private String name;
    private String sig;  //"(Ljava/lang/String;Z)V"
    private Class<?>[] types;
    // public Object method; //method/constructor

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

    public Class<?>[] getTypes() {
        return types;
    }

    public void setTypes(Class<?>[] types) {
        this.types = types;
    }

    @Override
    protected native long nCreate();

    @Override
    protected native void nRelease(long ptr);
    @Override
    protected boolean destroyNativeOnRecycle() {
        return false;
    }
}