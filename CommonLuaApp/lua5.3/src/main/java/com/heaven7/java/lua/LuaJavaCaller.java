package com.heaven7.java.lua;

import android.support.annotation.Keep;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by heaven7 on 2019/7/29.
 */
public final class LuaJavaCaller {

    public static final int PARAMETER_ERROR = 1;
    public static final int INVOKE_ERROR    = 2;
    private static final HashMap<String, ClassInfo> sInfos = new HashMap<>();
    private static final Map<Class<?>, TypeHandler> sConverters = new HashMap<>();

    static {
        sConverters.put(boolean.class, new BooleanConvertor());
        sConverters.put(byte.class, new ByteConvertor());
        sConverters.put(char.class, new CharConvertor());
        sConverters.put(short.class, new ShortConvertor());
        sConverters.put(int.class, new IntConvertor());
        sConverters.put(long.class, new LongConvertor());
        sConverters.put(float.class, new FloatConvertor());
        sConverters.put(double.class, new DoubleConvertor());
    }

    public static void registerJavaClass(Class<?> clazz){
        ClassInfo info = new ClassInfo(clazz);
        sInfos.put(clazz.getName(), info);
    }
    public static void registerJavaClasss(Class<?>...clazzs){
        for (Class<?> clazz : clazzs){
            registerJavaClass(clazz);
        }
    }
    public static void unregisterJavaClass(Class<?> clazz){
        sInfos.remove(clazz.getName());
    }

    /**
     * create object .this is called from lua.
     * @param className the class name
     * @param name the name
     * @param args the args
     * @param errorMsg the error msg
     * @return the object.
     */
    @Keep
    public static Object create(String className, String name, Object[] args, String[] errorMsg){
        //name can be null
        try {
            ClassInfo info = sInfos.get(className);
            ClassInfo.MethodInfo mi = info.getConstructorInfo(name);
            Class<?> clazz = Class.forName(className);

            Object[] out = new Object[mi.types.length];
            int state = convert(mi.types, args, out);
            switch (state){
                case PARAMETER_ERROR:
                    errorMsg[0] = "PARAMETER_ERROR";
                    return null;
                case INVOKE_ERROR:
                    errorMsg[0] = "INVOKE_ERROR";
                    return null;
                default:
                    errorMsg[0] = null;
            }
            return clazz.getConstructor(mi.types).newInstance(out);
        }catch (Exception e){
            errorMsg[0] = toString(e);
        }
        return null;
    }

    /**
     * invoke method
     * @param owner the owner
     * @param className the class name
     * @param method the method name
     * @param args the args
     * @param errorMsg the out error msg
     * @return the invoke result.
     */
    @Keep
    public static Object invoke(Object owner, String className, String method, Object[] args, String[] errorMsg){
        //name can be null
        try {
            ClassInfo info = sInfos.get(className);
            ClassInfo.MethodInfo mi = info.getMethodInfo(method);
            Class<?> clazz = Class.forName(className);

            Object[] out = new Object[mi.types.length];
            int state = convert(mi.types, args, out);
            switch (state){
                case PARAMETER_ERROR:
                    errorMsg[0] = "PARAMETER_ERROR";
                    return null;
                case INVOKE_ERROR:
                    errorMsg[0] = "INVOKE_ERROR";
                    return null;
                default:
                    errorMsg[0] = null;
            }
            return clazz.getMethod(method, mi.types).invoke(owner, out);
        }catch (Exception e){
            errorMsg[0] = toString(e);
        }
        return null;
    }
    private static String toString(Throwable e){
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        e.printStackTrace(writer);
        return sw.toString();
    }

    private static int convert(Class<?>[] types, Object[] src, Object[] out){
        if(src.length > types.length){
            return PARAMETER_ERROR;
        }
        try {
            for (int size = src.length , i = 0 ; i < size ; i ++){
                Class<?> type = types[i];
                TypeHandler converter = sConverters.get(type);
                if(converter != null){
                    out[i] = converter.convert(src[i].toString());
                }else {
                    //change nothing
                    out[i] = src[i];
                }
            }
            //补全
            if(src.length < types.length){
                int start = src.length;
                int end = types.length;
                for (int i = start; i < end ; i ++){
                    TypeHandler converter = sConverters.get(types[i]);
                    if(converter != null){
                        out[i] = converter.defaultValue();
                    }else {
                        out[i] = null;
                    }
                }
            }
        }catch (Exception e){
            return INVOKE_ERROR;
        }
        return 0;
    }

    private interface TypeHandler{
        Object convert(String arg);
        Object defaultValue();
    }
    private static class IntConvertor implements TypeHandler {
        @Override
        public Object convert(String arg) {
            return Float.valueOf(arg).intValue();
        }
        @Override
        public Object defaultValue() {
            return 0;
        }
    }
    private static class FloatConvertor implements TypeHandler{
        @Override
        public Object convert(String arg) {
            return Float.valueOf(arg);
        }
        @Override
        public Object defaultValue() {
            return 0f;
        }
    }
    private static class ShortConvertor extends IntConvertor  {
        @Override
        public Object convert(String arg) {
            return Float.valueOf(arg).shortValue();
        }
    }
    private static class ByteConvertor extends IntConvertor {
        @Override
        public Object convert(String arg) {
            return Float.valueOf(arg).byteValue();
        }
    }
    private static class LongConvertor extends IntConvertor  {
        @Override
        public Object convert(String arg) {
            return Double.valueOf(arg).longValue();
        }
    }
    private static class DoubleConvertor implements TypeHandler {
        @Override
        public Object convert(String arg) {
            return Double.valueOf(arg);
        }
        @Override
        public Object defaultValue() {
            return 0d;
        }
    }
    private static class CharConvertor implements TypeHandler {
        @Override
        public Object convert(String arg) {
            return arg.charAt(0);
        }
        @Override
        public Object defaultValue() {
            throw new RuntimeException("char type must be initialize.");
        }
    }
    private static class BooleanConvertor implements TypeHandler {
        @Override
        public Object convert(String arg) {
            return Float.valueOf(arg).byteValue() == 1;
        }
        @Override
        public Object defaultValue() {
            return false;
        }
    }

}
