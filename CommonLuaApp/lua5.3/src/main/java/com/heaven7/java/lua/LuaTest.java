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
    public static byte[] bf_en(byte[] in){
        byte[] arr;
        if(in.length % 8 != 0){
            arr = new byte[(in.length / 8 + 1) * 8];
        }else {
            arr = new byte[in.length];
        }
        nBFDo("heaven7".getBytes(), in, arr, 1);
        return arr;
    }
    public static byte[] bf_de(byte[] in, int resultLen){
        assert in.length % 8 == 0;
        byte[] arr = new byte[in.length];
        nBFDo("heaven7".getBytes(), in, arr, 0);
        if(resultLen != in.length){
            byte[] tmp = new byte[resultLen];
            System.arraycopy(arr, 0, tmp, 0, resultLen);
            return tmp;
        }
        return arr;
    }
    public static void bf_baseTest(){
        nBFTest();
    }
    public static void bfDecodeFile(String file) {
        nBfDecodeFile(file);
    }
    private static native void nTestAccessCppObjectInLua(long nativePointer, String script);
    private static native void nTestBindCpp1(long nativePointer, String script);
    private static native void nTestLuaRegistry(long nativePointer, String script);
    private static native void nTestLuaRegistryWrapper(long nativePointer, String script);

    private static native void nBFDo(byte[] key,byte[] data, byte[] out, int en);
    private static native void nBFTest();
    private static native void nBfDecodeFile(String file);
}
