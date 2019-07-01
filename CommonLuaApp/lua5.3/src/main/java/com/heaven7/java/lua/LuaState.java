package com.heaven7.java.lua;

/**
 * Created by heaven7 on 2019/7/1.
 */
public final class LuaState extends INativeObject.BaseNativeObject{

    @Override
    protected native long nCreate();

    @Override
    protected native void nRelease(long ptr);

}
