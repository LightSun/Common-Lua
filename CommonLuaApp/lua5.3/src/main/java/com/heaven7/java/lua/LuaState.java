package com.heaven7.java.lua;

import android.support.annotation.Keep;

import static com.heaven7.java.lua.LuaJavaCaller.registerJavaClass;

/**
 * Created by heaven7 on 2019/7/1.
 * the state/stack manager. wrap large methods for lua.
 * @author heaven7
 */
@Keep
public final class LuaState extends INativeObject.BaseNativeObject {

    //lua value type
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

    /**
     * relative to {@linkplain #doFile(String)}, the return is different. if has error this method return error message direct, or else return null.
     * @param filename the full file name
     * @return the error message . or null if call ok.
     */
    public String doFileM(String filename){
        int state = _doFile(getNativePointer(), filename);
        if(state != 0){
            String msg = toString(-1);
            pop(1);
            return msg;
        }
        return null;
    }
    /**
     * relative to {@linkplain #loadFile(String)}, the return is different. if has error this method return error message direct, or else return null.
     * @param filename the full file name
     * @return the error message . or null if call ok.
     */
    public String loadFileM(String filename){
        int state = _loadFile(getNativePointer(), filename);
        if(state != 0){
            String msg = toString(-1);
            pop(1);
            return msg;
        }
        return null;
    }
    /**
     * relative to {@linkplain #doString(String)}, the return is different. if has error this method return error message direct, or else return null.
     * @param filename the full file name
     * @return the error message . or null if call ok.
     */
    public String doStringM(String filename){
        int state = _evaluateScript(getNativePointer(), filename);
        if(state != 0){
            String msg = toString(-1);
            pop(1);
            return msg;
        }
        return null;
    }
    /**
     * relative to {@linkplain #loadString(String)}, the return is different. if has error this method return error message direct, or else return null.
     * @param filename the full file name
     * @return the error message . or null if call ok.
     */
    public String loadStringM(String filename){
        int state = _loadScript(getNativePointer(), filename);
        if(state != 0){
            String msg = toString(-1);
            pop(1);
            return msg;
        }
        return null;
    }

    /**
     * the normal dofile which wrap the native .
     * @param filename the full filename
     * @return the result code. 0 means success.
     */
    public int doFile(String filename) {
        return _doFile(getNativePointer(), filename);
    }
    /**
     * the normal loadFile which wrap the native .
     * @param filename the full filename
     * @return the result code. 0 means success.
     */
    public int loadFile(String filename) {
        return _loadFile(getNativePointer(), filename);
    }
    /**
     * the normal doString which wrap the native .
     * @param filename the full filename
     * @return the result code. 0 means success.
     */
    //do return a table
    public int doString(String script) {
        return _evaluateScript(getNativePointer(), script);
    }
    /**
     * the normal loadString which wrap the native .
     * @param filename the full filename
     * @return the result code. 0 means success.
     */
    //load return a function
    public int loadString(String script){
        return _loadScript(getNativePointer(), script);
    }

    /**
     * get the global value for target key
     * @param key the global key
     * @return the lua data type of value
     */
    public int getGlobal(String key) {
        return _getGlobal(getNativePointer(), key);
    }

    /**
     * push a string to lua stack
     * @param var the string
     */
    public void pushString(String var) {
         _pushString(getNativePointer(), var);
    }

    /**
     * push a number to lua stack
     * @param var the number
     */
    public void pushNumber(double var) {
        _pushNumber(getNativePointer(), var);
    }

    /**
     * push nil to lua stack
     */
    public void pushNil() {
        _pushnil(getNativePointer());
    }

    /**
     * push boolean to lua stack
     * @param val the value
     */
    public void pushBoolean(boolean val) {
        _pushBoolean(getNativePointer(), val);
    }

    /**
     * push java object to lua stack.
     * <p>
     * the java object, can be LuaFunction, but can't be Class object.
     * if you want push java class . you should use {@linkplain #pushClass(Class, String, boolean)}. </p>
     * @param obj the java object
     */
    public void push(Object obj) {
        push(obj, null, true);
    }

    /**
     * push java object with global key
     * <p>
     * the java object, can be LuaFunction, but can't be Class object.
     * if you want push java class . you should use {@linkplain #pushClass(Class, String, boolean)}. </p>
     * @param obj the java object.
     * @param name the global key name
     */
    public void push(Object obj, String name){
        push(obj, name, true);
    }
    /**
     * push a object .
     * <p>
     * the java object, can be LuaFunction, but can't be Class object.
     * if you want push java class . you should use {@linkplain #pushClass(Class, String, boolean)}. </p>
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
            registerJavaClass(obj.getClass());
            _pushJavaObject(getNativePointer(), obj, obj.getClass().getName(), name, pushToStack);
        }
    }

    /**
     * push a java class to stack. that means you want to call some static fields or methods by target class in lua.
     * @param clazz the class
     * @param globalK the global key
     * @param pushToStack true to push to current stack
     */
    public void pushClass(Class<?> clazz, String globalK, boolean pushToStack){
        registerJavaClass(clazz);
        _pushClass(getNativePointer(), clazz.getName(), globalK,  pushToStack);
    }

    /**
     * push lua function to stack.
     * @param func the lua function
     */
    public void pushFunction(LuaFunction func){
        push(func);
    }
    /**
     * push lua function to stack with global key.
     * @param func the lua function
     * @param name the global key
     */
    public void pushFunction(LuaFunction func, String name){
        push(func, name, true);
    }
    /**
     * push lua function to stack with global key.
     * @param func the lua function
     * @param name the global key
     * @param pushToStack true to push to stack
     */
    public void pushFunction(LuaFunction func, String name, boolean pushToStack){
        push(func, name, pushToStack);
    }

    /**
     * push the target value as indicate by the index.
     * @param idx the index of value in lua stack
     */
    public void pushValue(int idx) {
        _pushValue(getNativePointer(), idx);
    }

    /**
     * get the string from target index.
     * @param idx the index
     * @return the string
     */
    public String toString(int idx) {
        return _toString(getNativePointer(), idx);
    }

    /**
     * get the number value
     * @param idx the index
     * @return the number
     */
    public double toNumber(int idx) {
        return _toNumber(getNativePointer(), idx);
    }

    /**
     * pcall the function with args.
     * @param nArgs the args count
     * @param nResults the result count
     * @param errFunc the error function index
     * @return the call state . 0 means success.
     */
    public int pcall(int nArgs, int nResults, int errFunc) {
        return _pcall(getNativePointer(), nArgs, nResults, errFunc);
    }
    /**
     * pcall the function with args. and return error msg
     * @param nArgs the args count
     * @param nResults the result count
     * @param errFunc the error function index
     * @return the error message or null of no error.
     */
    public String pcallM(int nArgs, int nResults, int errFunc){
        int state = _pcall(getNativePointer(), nArgs, nResults, errFunc);
        if(state != 0){
            String msg = toString(-1);
            pop(1);
            return msg;
        }
        return null;
    }

    /**
     * call direct.
     * @param nArgs the args count
     * @param nResults he result count
     */
    public void call(int nArgs, int nResults) {
        _call(getNativePointer(), nArgs, nResults);
    }

    /**
     * get the top of stack
     * @return the top
     */
    public int getTop() {
        return _getTop(getNativePointer());
    }

    /**
     * get the value for target table as indicate by index. and the top is the key
     * @param index the table index
     * @return the value type.
     */
    public int getTable(int index) {
        return _getTable(getNativePointer(), index);
    }

    /**
     * set the table. this will pop(-2) as key .pop(-1) as value then set
     * @param index the table index
     */
    public void setTable(int index){
        _setTable(getNativePointer(), index);
    }

    /**
     * new table
     */
    public void newTable() {
        _newTable(getNativePointer());
    }

    /**
     * raw set the index for table. this will not trigger '__index' method for lua meta.
     * @param idx the table index
     * @param arrayIndex the array index
     */
    public void rawSeti(int idx, int arrayIndex) {
        _rawseti(getNativePointer(), idx, arrayIndex);
    }

    /**
     * raw set the table.
     * relative to {@linkplain #setTable(int)} just not trigger '__index' method for lua meta.
     * @param idx the index of table
     */
    public void rawSet(int idx) {
        _rawset(getNativePointer(), idx);
    }

    /**
     * simple dump the lua stack
     */
    public void dumpLuaStack() {
        _dumpLuaStack(getNativePointer());
    }

    /**
     * get the value type for target index
     * @param idx the value index in stack
     * @return the value type
     * @see #TYPE_FUNCTION and etc.
     */
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

    /**
     * get the lua value for target index. after you use the LuaValue. you should call {@linkplain LuaValue#recycle()} to recycle it.
     * @param idx the value index
     * @return the lua value . or null.
     */
    public LuaValue getLuaValue(int idx){
        return (LuaValue) _getLuaValue(getNativePointer(), idx);
    }

    /**
     * set global . this will pop(-1) as the value.
     * @param name the global key
     */
    public void setGlobal(String name){
        _setGlobal(getNativePointer(), name);
    }

    /**
     * remove the global
     * @param key the global key
     * @return true if remove success
     */
    public boolean removeGlobal(String key){
        if(key == null){
            throw new NullPointerException();
        }
        return _removeGlobal(getNativePointer(), key);
    }

    /**
     * indicate has the global key or not
     * @param key the key
     * @return true if has
     */
    public boolean hasGlobal(String key){
        return _hasGlobal(getNativePointer(), key);
    }

    /**
     * travel the table by target traveller
     * @param tab_idx the table idx
     * @param lt the traveller
     */
    public void travel(int tab_idx, LuaTraveller lt){
        _travel(getNativePointer(), tab_idx, lt);
    }

    /**
     * indicate is native wrapper or not. if is a java object or java function .this always return true.
     * @param idx the idx
     * @return true if is native wrapper.
     */
    public boolean isNativeWrapper(int idx){
        return _isNativeWrapper(getNativePointer(), idx) > 0;
    }

    /**
     * set the collection type for target table
     * @param idx the table index
     * @param type the collection type. see {@linkplain #COLLECTION_TYPE_LIST} and etc.
     */
    public void setCollectionTypeAsMeta(int idx, int type) {
        _setCollectionTypeAsMeta(getNativePointer(), idx, type);
    }

    /**
     * get the collection type
     * @param idx the table index
     * @return the collection type
     */
    public int getCollectionType(int idx){
       return _getCollectionType(getNativePointer(), idx);
    }

    /**
     * indicate the target value of index is java class or not
     * @param idx the idx
     * @return true if is java class
     */
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

    private static synchronized native int _doFile(long ptr, String script);
    private static synchronized native int _loadFile(long ptr, String script);
    private static synchronized native int _evaluateScript(long ptr, String script);
    private static synchronized native int _loadScript(long ptr, String script);
    private static synchronized native Object _getLuaValue(long ptr, int idx);
    private static synchronized native int _getTop(long ptr);
    private static synchronized native int _getType(long ptr, int idx);

    private static synchronized native int _getTable(long ptr, int idx);
    private static synchronized native void  _setTable(long ptr, int idx);
    private static synchronized native void _setGlobal(long ptr, String name);
    private static synchronized native int _getGlobal(long ptr, String var);
    private static synchronized native boolean _removeGlobal(long ptr, String var);
    private static synchronized native boolean _hasGlobal(long ptr, String var);

    private static synchronized native String _toString(long ptr, int idx);
    private static synchronized native double _toNumber(long ptr, int idx);

    private static synchronized native void  _pushString(long ptr, String var);
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
