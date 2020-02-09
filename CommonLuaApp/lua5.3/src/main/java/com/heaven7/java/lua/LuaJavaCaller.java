package com.heaven7.java.lua;

import android.support.annotation.Keep;

import com.heaven7.java.lua.internal.ClassInfo;
import com.heaven7.java.lua.internal.FieldInfo;
import com.heaven7.java.lua.internal.CallJavaMethodFunction;
import com.heaven7.java.lua.internal.LuaUtils;
import com.heaven7.java.lua.internal.MethodInfo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

/**
 * Created by heaven7 on 2019/7/29.
 */
public final class LuaJavaCaller {

    private static final HashMap<String, ClassInfo> sInfos = new HashMap<>();

    public static void registerJavaClass(Class<?> clazz) {
        ClassInfo info = sInfos.get(clazz.getName());
        if (info == null) {
            info = new ClassInfo(clazz);
            sInfos.put(clazz.getName(), info);
        }
    }

    public static void registerJavaClasss(Class<?>... clazzs) {
        for (Class<?> clazz : clazzs) {
            registerJavaClass(clazz);
        }
    }

    public static void unregisterJavaClass(Class<?> clazz) {
        sInfos.remove(clazz.getName());
    }
    @Keep
    public static boolean getStaticMethod(long luaStatePtr, String className, String name, int pCount){
        ClassInfo info = sInfos.get(className);
        if(info == null){
            return false;
        }
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            return false;
        }
        List<MethodInfo> list = info.getStaticMethods(name, pCount);

        if(list == null){
            return false;
        }
        new LuaState(luaStatePtr).pushFunction(new CallJavaMethodFunction(clazz, list));
        return true;
    }
    @Keep
    public static boolean getStaticClass(long luaStatePtr, String className, String name){
        ClassInfo info = sInfos.get(className);
        if(info == null){
            return false;
        }
        Class<?> clazz;
        try {
            clazz = Class.forName(className + "$" + name);
        } catch (ClassNotFoundException e) {
            return false;
        }
        new LuaState(luaStatePtr).pushClass(clazz, null, true);
        return true;
    }
    @Keep
    public static boolean getStaticField(long luaStatePtr, String className, String name){
        final LuaState luaState = new LuaState(luaStatePtr);
        ClassInfo info = sInfos.get(className);
        if(info == null){
            return false;
        }
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            return false;
        }
        FieldInfo fi = info.getStaticFieldInfo(name);
        if(fi == null){
            return false;
        }
        try {
            Field field = clazz.getField(fi.getRawName());
            LuaUtils.writeToLua(luaState, fi.getType(), field.get(null));
            return true;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }
    /**
     * create object .this is called from lua.
     *
     * @param luaStatePtr the lua state ptr
     * @param className   the class name
     * @param name        the name .can be null.
     * @param args        the args
     * @param errorMsg    the error msg
     * @return the object.
     */
    @Keep
    public static Object create(long luaStatePtr, String className, String name, Object[] args, Object[] errorMsg) {
        //name can be null
        if (name == null) {
            name = "<init>";
        }
        final LuaState luaState = new LuaState(luaStatePtr);
        try {
            ClassInfo info = sInfos.get(className);
            if(info == null){
                errorMsg[0] = "can't find classInfo for class = " + className;
                return null;
            }
            Class<?> clazz = Class.forName(className);
            if (args == null || args.length == 0) {
                return clazz.newInstance();
            }
            List<MethodInfo> list = info.getConstructors(name, args.length);
            if(list == null){
                errorMsg[0] = "can't find constructor for class("+ className + "), constructor name = "
                        + name  + ", args.length = " + args.length;
                return null;
            }
            //desc -> aesc
            for (int size = list.size(), i = size - 1; i >= 0; i--) {
                MethodInfo mi = list.get(i);
                Object[] out = new Object[mi.getParameterCount()];
                convert(luaState, mi.getTypes(), args, out);
                try {
                    return clazz.getConstructor(mi.getRawTypes()).newInstance(out);
                } catch (Exception e) {
                    if (i == 0) {
                        //last. still error.
                        errorMsg[0] = "can't find correct constructor(" + name + ") for class(" + className + ").";
                    }
                }
            }
        } catch (Exception e) {
            errorMsg[0] = toString(e);
        }finally {
            LuaUtils.recycleValues(args);
        }
        return null;
    }

    /**
     * invoke method
     *
     * @param owner     the owner
     * @param className the class name
     * @param method    the method name
     * @param args      the args
     * @param errorMsg  the out error msg
     * @return the push count to lua stack
     */
    @Keep
    public static int invoke(long luaStatePtr, Object owner, String className, String method, Object[] args, Object[] errorMsg) {
        //name can be null
        final LuaState luaState = new LuaState(luaStatePtr);
        try {
            ClassInfo info = sInfos.get(className);
            if(info == null){
                errorMsg[0] = "can't find classInfo for class = " + className;
                return 0;
            }
            Class<?> clazz = Class.forName(className);

            List<MethodInfo> list = info.getMethods(method, args.length);
            if(list == null){
                errorMsg[0] = "can't find method for class("+ className + "), method name = "
                        + method  + ", args.length = " + args.length;
                return 0;
            }
            //desc -> aesc
            for (int size = list.size(), i = size - 1; i >= 0; i--) {
                MethodInfo mi = list.get(i);
                Object[] out = new Object[mi.getParameterCount()];
                convert(luaState, mi.getTypes(), args, out);
                try {
                    Method m = clazz.getMethod(mi.getRawName(), mi.getRawTypes());
                    Object result = m.invoke(owner, out);
                    return LuaUtils.writeToLua(luaState, m.getGenericReturnType(), result);
                } catch (Exception e) {
                    if (i == 0) {
                        //last. still error.
                        errorMsg[0] = "can't find correct method(" + method + ") for class(" + className + ").";
                    }
                }
            }
        } catch (Exception e) {
            errorMsg[0] = toString(e);
        }
        return 0;
    }

    private static String toString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        e.printStackTrace(writer);
        return sw.toString();
    }

    public static boolean convert(LuaState luaState, Type[] types, Object[] args, Object[] out) {
        Lua2JavaValue value;
        for (int size = args.length, i = 0; i < size; i++) {
            Type type = types[i];
            LuaTypeAdapter adapter = LuaTypeAdapter.get(type);
            if (adapter != null) {
                if (args[i] instanceof Lua2JavaValue) {
                    out[i] = adapter.readFromLua(luaState, (Lua2JavaValue) args[i]);
                } else {
                    throw new IllegalStateException("args must be Lua2JavaValue.");
                }
            } else {
                //change nothing
                out[i] = args[i];
            }
        }
        //补全
        if (args.length < types.length) {
            for (int i = args.length, end = types.length; i < end; i++) {
                LuaTypeAdapter adapter = LuaTypeAdapter.get(types[i]);
                if (adapter != null) {
                    out[i] = adapter.defaultValue();
                } else {
                    out[i] = null;
                }
            }
        }
        return true;
    }

}
