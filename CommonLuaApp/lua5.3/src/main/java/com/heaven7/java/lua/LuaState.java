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
    //collection type
    public static final int COLLECTION_TYPE_LIST = 1;
    public static final int COLLECTION_TYPE_SET  = 2;
    public static final int COLLECTION_TYPE_MAP  = 3;
    public static final int COLLECTION_TYPE_UNKNOWN  = -1;

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
    public void pop(int n){
        _pop(getNativePointer(), n);
    }

    public int LdoString(String script) {
        return _evaluateScript(getNativePointer(), script);
    }
    //do return a table
    public int execScript(String script) {
        return _evaluateScript(getNativePointer(), script);
    }
    //load return a function
    public int loadScript(String script){
        return _loadScript(getNativePointer(), script);
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

    public void push(Object obj) {
        push(obj, null, true);
    }

    public void push(Object obj, String name){
        push(obj, name, true);
    }
    /**
     * push a object .
     * @param obj the object
     * @param name the global name if need
     * @param pushToStack true as also push it to current lua stack.
     */
    public void push(Object obj, String name, boolean pushToStack){
        if(obj == null){
            throw new NullPointerException();
        }
        if(obj instanceof LuaFunction){
            _pushFunction(getNativePointer(), obj, LuaFunction.class.getName(), name, pushToStack);
        }else {
            _pushJavaObject(getNativePointer(), obj, obj.getClass().getName(), name, pushToStack);
        }
    }
    public void pushClass(Class<?> clazz, String globalK, boolean pushToStack){
        _pushClass(getNativePointer(), clazz.getName(), globalK,  pushToStack);
    }
    public void pushFunction(LuaFunction func){
        push(func);
    }
    public void pushFunctionGlobal(LuaFunction func, String name){
        push(func, name, true);
    }
    public void pushFunctionGlobal(LuaFunction func, String name, boolean pushToStack){
        push(func, name, pushToStack);
    }

    public void pushValue(int idx) {
        _pushValue(getNativePointer(), idx);
    }
    public String toString(int idx) {
        return _toString(getNativePointer(), idx);
    }
    public int toInt(int idx) {
        return _toInt(getNativePointer(), idx);
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
    public void setTable(int index){
        _setTable(getNativePointer(), index);
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
    /**
     * report a error msg to lua
     * @param msg the error msg
     */
    public void error(String msg){
        _error(getNativePointer(), msg);
    }
    public LuaValue getLuaValue(int idx){
        return (LuaValue) _getLuaValue(getNativePointer(), idx);
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
    public void travel(int tab_idx, LuaTraveller lt){
        _travel(getNativePointer(), tab_idx, lt);
    }
    public boolean isNativeWrapper(int idx){
        return _isNativeWrapper(getNativePointer(), idx) > 0;
    }
    public void setCollectionTypeAsMeta(int idx, int type) {
        _setCollectionTypeAsMeta(getNativePointer(), idx, type);
    }
    public int getCollectionType(int idx){
       return _getCollectionType(getNativePointer(), idx);
    }
    public boolean isJavaClass(int idx) {
        return _isJavaClass(getNativePointer(), idx);
    }

    /**
     * get the java object from native. unless you confirm the idx object is lua table and is java object wrapper table. you can call this
     * or else return null.
     * @param idx the lua idx
     * @return the java object
     */
    public Object getJavaObject(int idx) {
        return _getJavaObject(getNativePointer(), idx);
    }
    //=============================== native methods ========================================
    private static synchronized native boolean _isJavaClass(long ptr, int idx);
    private static synchronized native void  _setCollectionTypeAsMeta(long ptr, int idx, int collType);
    private static synchronized native int  _getCollectionType(long ptr, int idx);
    private static synchronized native void  _error(long ptr, String msg);
    private static synchronized native void _travel(long ptr, int idx, Object traveller);
    private static synchronized native int _isNativeWrapper(long ptr, int idx);
    private static synchronized native Object _getJavaObject(long ptr, int idx);

    private static synchronized native int _evaluateScript(long ptr, String script);
    private static synchronized native int _loadScript(long ptr, String script);
    private static synchronized native Object _getLuaValue(long ptr, int idx);
    private static synchronized native int _getTop(long ptr);
    private static synchronized native int _getType(long ptr, int idx);
    private static synchronized native int _getGlobal(long ptr, String var);
    private static synchronized native int _getTable(long ptr, int idx);
    private static synchronized native void  _setTable(long ptr, int idx);
    private static synchronized native boolean _removeGlobal(long ptr, String var);
    private static synchronized native boolean _hasGlobal(long ptr, String var);

    private static synchronized native String _toString(long ptr, int idx);
    private static synchronized native int _toInt(long ptr, int idx);

    private static synchronized native String _pushString(long ptr, String var);
    private static synchronized native void _pushNumber(long ptr, double n);
    private static synchronized native void _pushValue(long ptr, int idx);
    private static synchronized native void _pushnil(long ptr);
    private static synchronized native void _pushBoolean(long ptr, boolean val);
    //globalKey can be null
    private static synchronized native void _pushJavaObject(long ptr, Object obj, String classname, String globalKey, boolean pushToStack);
    private static synchronized native void _pushFunction(long ptr, Object func, String classname, String globalKey, boolean pushToStack);
    private static synchronized native void _pushClass(long ptr, String classname, String globalKey, boolean pushToStack);

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
