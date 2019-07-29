package com.heaven7.java.lua;

/**
 * Created by heaven7 on 2019/7/23.
 */
public final class LuaTest {

    public static void testBindCpp1(LuaState state, String script){
        nTestBindCpp1(state.getNativePointer(), script);
    }
    public static void testLuaRegistry(LuaState state, String script){
        nTestLuaRegistry(state.getNativePointer(), script);
    }

    private static native void nTestBindCpp1(long nativePointer, String script);
    private static native void nTestLuaRegistry(long nativePointer, String script);
}
