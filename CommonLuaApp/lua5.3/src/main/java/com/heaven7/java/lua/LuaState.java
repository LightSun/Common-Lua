package com.heaven7.java.lua;

import android.support.annotation.Keep;

/**
 * Created by heaven7 on 2019/7/1.
 */
@Keep
public final class LuaState extends INativeObject.BaseNativeObject {

    public static final int TYPE_NONE = -1;
    public static final int TYPE_NIL = 0;
    public static final int TYPE_BOOLEAN = 1;
    public static final int TYPE_LIGHT_USERDATA = 2;
    public static final int TYPE_NUMMBER = 3;
    public static final int TYPE_STRING = 4;
    public static final int TYPE_TABLE = 5;
    public static final int TYPE_FUNCTION = 6;
    public static final int TYPE_USERDATA = 7;
    public static final int TYPE_THREAD = 8;

    static {
        System.loadLibrary("lua");
        System.loadLibrary("luajava");
    }

    public LuaState(long ptr) {
        super(ptr);
    }

    public LuaState() {
        this(0);
    }

    @Override
    protected long nCreate() {
        return _nCreate();
    }

    @Override
    protected void nRelease(long ptr) {
        _nRelease(ptr);
    }

    public int saveLightly() {
        return getTop();
    }

    public void restoreLightly(int k) {
        int top = getTop();
        if (top > k) {
            _pop(getNativePointer(), top - k);
        }
    }

    public int LdoString(String script) {
        return _evaluateScript(getNativePointer(), script);
    }

    public int evaluateScript(String script) {
        return _evaluateScript(getNativePointer(), script);
    }

    public int getGlobal(String var) {
        return _getGlobal(getNativePointer(), var);
    }

    public String pushString(String var) {
        return _pushString(getNativePointer(), var);
    }

    public void pushNumber(double var) {
        _pushNumber(getNativePointer(), var);
    }

    public void pushNil() {
        _pushnil(getNativePointer());
    }

    public void pushBoolean(boolean val) {
        _pushBoolean(getNativePointer(), val);
    }

    public void push(Object result) {
        _pushJavaObject(getNativePointer(), result, result.getClass().getName(), null);
    }
    public void pushGlobal(Object result, String name) {
        if(name == null){
            throw new NullPointerException("push to global need set a name for it.");
        }
        _pushJavaObject(getNativePointer(), result, result.getClass().getName(), name);
    }

    public void pushValue(int idx) {
        _pushValue(getNativePointer(), idx);
    }

    public String toString(int idx) {
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

    public int getTable(int index) {
        return _getTable(getNativePointer(), index);
    }

    public void newTable() {
        _newTable(getNativePointer());
    }

    public void rawSeti(int idx, int arrayIndex) {
        _rawseti(getNativePointer(), idx, arrayIndex);
    }

    public void rawSet(int idx) {
        _rawset(getNativePointer(), idx);
    }
    public void dumpLuaStack() {
        _dumpLuaStack(getNativePointer());
    }
    public int getType(int idx) {
        return _getType(getNativePointer(), idx);
    }
    public boolean removeGlobal(String key){
        if(key == null){
            throw new NullPointerException();
        }
        return _removeGlobal(getNativePointer(), key);
    }
    public boolean hasGlobal(String key){
        return _hasGlobal(getNativePointer(), key);
    }

    private static synchronized native int _evaluateScript(long ptr, String script);
    private static synchronized native int _getTop(long ptr);
    private static synchronized native int _getType(long ptr, int idx);
    private static synchronized native int _getGlobal(long ptr, String var);
    private static synchronized native int _getTable(long ptr, int idx);
    private static synchronized native boolean _removeGlobal(long ptr, String var);
    private static synchronized native boolean _hasGlobal(long ptr, String var);

    private static synchronized native String _toString(long ptr, int idx);

    private static synchronized native String _pushString(long ptr, String var);
    private static synchronized native void _pushNumber(long ptr, double n);
    private static synchronized native void _pushValue(long ptr, int idx);
    private static synchronized native void _pushnil(long ptr);
    private static synchronized native void _pushBoolean(long ptr, boolean val);
    private static synchronized native void _pushJavaObject(long ptr, Object obj, String classname, String globalKey); //globalKey can be null

    private static synchronized native int _pcall(long ptr, int nArgs, int nResults, int errFunc);
    private static synchronized native void _call(long ptr, int nArgs, int nResults);

    private static synchronized native void _newTable(long ptr);
    private static synchronized native void _rawseti(long ptr, int index, long arrayIndex);
    private static synchronized native void _rawset(long ptr, int index);

    private static synchronized native void _pop(long ptr, int count);
    private static synchronized native void _dumpLuaStack(long ptr);
    private static synchronized native long _nCreate();
    private static synchronized native void _nRelease(long ptr);
}
