package com.heaven7.java.lua;

import android.support.annotation.Keep;

/**
 * Created by heaven7 on 2019/7/1.
 */
@Keep
public final class LuaState extends INativeObject.BaseNativeObject{

    static {
        System.loadLibrary("lua");
        System.loadLibrary("luajava");
    }

    public LuaState(long ptr) {
        super(ptr);
    }
    public LuaState(){
        this(0);
    }

    @Override
    protected long nCreate(){
        return _nCreate();
    }
    @Override
    protected void nRelease(long ptr){
        _nRelease(ptr);
    }

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
    public void pushNil(){
        _pushnil(getNativePointer());
    }
    public void pushBoolean(boolean val) {
        _pushBoolean(getNativePointer(), val);
    }
    public void push(Object result) {
        _pushJavaObject(getNativePointer(), result, result.getClass().getName());
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
    public int getTop() {
        return _getTop(getNativePointer());
    }
    public void getTable(int index) {
        //TODO
    }
    public void newTable(){
        _newTable(getNativePointer());
    }
    public void rawSeti(int idx, int arrayIndex){
        _rawseti(getNativePointer(), idx, arrayIndex);
    }
    public void rawSet(int idx){
        _rawset(getNativePointer(), idx);
    }
    public void dumpLuaStack(){
        _dumpLuaStack(getNativePointer());
    }

   //TODO private static synchronized native int _getTable(long ptr, int index);
    private static synchronized native int _getTop(long ptr);

    private static synchronized native int _evaluateScript(long ptr, String script);
    private static synchronized native int _getGlobal(long ptr, String var);
    private static synchronized native String _toString(long ptr, int idx);
    private static synchronized native String _pushString(long ptr, String var);
    private static synchronized native void _pushNumber(long ptr, double n);
    private static synchronized native void _pushnil(long ptr);
    private static synchronized native void _pushBoolean(long ptr, boolean val);
    private static synchronized native void _pushJavaObject(long ptr, Object obj, String classname);

    private static synchronized native int _pcall(long ptr, int nArgs, int nResults, int errFunc);
    private static synchronized native void  _call(long ptr, int nArgs, int nResults);

    private static synchronized native void  _newTable(long ptr);
    private static synchronized native void  _rawseti(long ptr, int index, long arrayIndex);
    private static synchronized native void  _rawset(long ptr, int index);

    private static synchronized native void  _dumpLuaStack(long ptr);
    private static synchronized native long  _nCreate();
    private static synchronized native void  _nRelease(long ptr);
}
