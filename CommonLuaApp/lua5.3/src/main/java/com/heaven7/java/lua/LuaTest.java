package com.heaven7.java.lua;

/**
 * Created by heaven7 on 2019/7/23.
 */
public final class LuaTest {

    static {
        System.loadLibrary("lua_test");
    }

    public static void testBindCpp1(LuaState state, String script){
        nTestBindCpp1(state.getNativePointer(), script);
    }
    public static void testLuaRegistry(LuaState state, String script){
        nTestLuaRegistry(state.getNativePointer(), script);
    }
    public static void testLuaRegistryWrapper(LuaState state, String script){
        nTestLuaRegistryWrapper(state.getNativePointer(), script);
    }

    public static void testAccessCppObjectInLua(LuaState state, String script){
        nTestAccessCppObjectInLua(state.getNativePointer(), script);
    }

    private static native void nTestAccessCppObjectInLua(long nativePointer, String script);
    private static native void nTestBindCpp1(long nativePointer, String script);
    private static native void nTestLuaRegistry(long nativePointer, String script);
    private static native void nTestLuaRegistryWrapper(long nativePointer, String script);
}
