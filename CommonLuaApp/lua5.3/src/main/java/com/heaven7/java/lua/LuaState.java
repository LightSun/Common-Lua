package com.heaven7.java.lua;

import android.support.annotation.Keep;

/**
 * Created by heaven7 on 2019/7/1.
 */
@Keep
public final class LuaState extends INativeObject.BaseNativeObject{

    static {
        System.loadLibrary("luajava");
    }

    @Override
    protected native long nCreate();

    @Override
    protected native void nRelease(long ptr);

    public int LdoString(String script){
        return _evaluateScript(getNativePointer(), script);
    }
    public int evaluateScript(String script){
        return _evaluateScript(getNativePointer(), script);
    }
    public int getGlobal(String var){
        return _getGlobal(getNativePointer(), var);
    }
    public String pushString(String var){
       return  _pushString(getNativePointer(), var);
    }
    public void pushNumber(double var){
        _pushNumber(getNativePointer(), var);
    }
    public String toString(int idx){
        return _toString(getNativePointer(), idx);
    }
    public int pcall(int nArgs, int nResults, int errFunc) {
        return _pcall(getNativePointer(), nArgs, nResults, errFunc);
    }
    public void call(int nArgs, int nResults) {
        _call(getNativePointer(), nArgs, nResults);
    }

    private static synchronized native int _evaluateScript(long ptr, String script);
    private static synchronized native int _getGlobal(long ptr, String var);
    private static synchronized native String _pushString(long ptr, String var);
    private static synchronized native void _pushNumber(long ptr, double n);
    private static synchronized native String _toString(long ptr, int idx);
    private static synchronized native int _pcall(long ptr, int nArgs, int nResults, int errFunc);
    private static synchronized native void  _call(long ptr, int nArgs, int nResults);
}
