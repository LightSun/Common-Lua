package com.heaven7.java.lua;

import android.support.annotation.Keep;

import com.heaven7.java.lua.convertors.TypeConvertorFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
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
    public static Object create(long luaStatePtr, String className, String name, Object[] args, String[] errorMsg) {
        //name can be null
        if (name == null) {
            name = "<init>";
        }
        final LuaState luaState = new LuaState(luaStatePtr);
        try {
            ClassInfo info = sInfos.get(className);
            Class<?> clazz = Class.forName(className);
            if (args == null || args.length == 0) {
                return clazz.newInstance();
            }
            List<MethodInfo> list = info.getConstructorInfoes(name, args.length);
            //desc -> aesc
            for (int size = list.size(), i = size - 1; i >= 0; i--) {
                MethodInfo mi = list.get(i);
                Object[] out = new Object[mi.getParameterCount()];
                convert(luaState, mi.getTypes(), args, out);
                try {
                    return clazz.getConstructor(mi.getTypes()).newInstance(out);
                } catch (Exception e) {
                    if (i == 0) {
                        //last. still error.
                        errorMsg[0] = "can't find correct constructor(" + name + ") for class(" + className + ").";
                    }
                }
            }
        } catch (Exception e) {
            errorMsg[0] = toString(e);
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
     */
    @Keep
    public static void invoke(long luaStatePtr, Object owner, String className, String method, Object[] args, String[] errorMsg) {
        //name can be null
        final LuaState luaState = new LuaState(luaStatePtr);
        try {
            ClassInfo info = sInfos.get(className);
            Class<?> clazz = Class.forName(className);

            List<MethodInfo> list = info.getMethodInfoes(method, args.length);
            //desc -> aesc
            for (int size = list.size(), i = size - 1; i >= 0; i--) {
                MethodInfo mi = list.get(i);
                Object[] out = new Object[mi.getParameterCount()];
                convert(luaState, mi.getTypes(), args, out);
                try {
                    Object result = clazz.getMethod(mi.getName(), mi.getTypes()).invoke(owner, out);
                    convertResultToLua(luaState, result);
                    break;
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
    }

    private static void convertResultToLua(LuaState luaState, Object result) {
        if (result == null) {
            luaState.pushNil();
            return;
        }
        TypeConvertor converter = TypeConvertorFactory.getTypeConvertor(result.getClass());
        converter.convert(luaState, result);
    }

    private static String toString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        e.printStackTrace(writer);
        return sw.toString();
    }

    private static boolean convert(LuaState luaState, Class<?>[] types, Object[] args, Object[] out) {
        for (int size = args.length, i = 0; i < size; i++) {
            Class<?> type = types[i];
            TypeConvertor converter = TypeConvertorFactory.getTypeConvertor(type);
            if (converter != null) {
                if (args[i] instanceof Lua2JavaValue) {
                    out[i] = converter.convert((Lua2JavaValue) args[i]);
                } else {
                    out[i] = converter.convert(args[i].toString());
                }
            } else {
                //change nothing
                out[i] = args[i];
            }
        }
        //补全
        if (args.length < types.length) {
            for (int i = args.length, end = types.length; i < end; i++) {
                TypeConvertor converter = TypeConvertorFactory.getTypeConvertor(types[i]);
                if (converter != null) {
                    out[i] = converter.defaultValue();
                } else {
                    out[i] = null;
                }
            }
        }
        return true;
    }

}
