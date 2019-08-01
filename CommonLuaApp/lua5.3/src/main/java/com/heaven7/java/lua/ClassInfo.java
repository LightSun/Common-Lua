package com.heaven7.java.lua;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by heaven7 on 2019/7/29.
 */
public final class ClassInfo {

    private static final Map<Class<?>, String> sBases = new HashMap<>();

    //private final String mClassName;
    //lua_method_name, info
    private final Map<String, MethodInfo> mMethodMap = new HashMap<>();
    private final Map<String, MethodInfo> mConstructorMap = new HashMap<>();
    private final Map<String, Integer> mIndexMap = new HashMap<>();

    static {
        sBases.put(boolean.class, "Z");
        sBases.put(byte.class, "B");
        sBases.put(char.class, "C");
        sBases.put(short.class, "S");
        sBases.put(int.class, "I");
        sBases.put(long.class, "J");
        sBases.put(float.class, "F");
        sBases.put(double.class, "D");
    }

    public ClassInfo(Class<?> clazz) {
        //this.mClassName = clazz.getName();

        StringBuilder sb = new StringBuilder();
        //constructors
        Constructor[] cons = clazz.getConstructors();
        for (Constructor con : cons) {
            LuaMethod lm = (LuaMethod) con.getAnnotation(LuaMethod.class);
            MethodInfo info = new MethodInfo();
            info.setName("<init>");
            info.setTypes(con.getParameterTypes());

            getSigAndFill(sb, mConstructorMap, null, lm, info);
        }
        //methods
        Method[] methods = clazz.getMethods();
        for (Method m : methods) {
            LuaMethod lm = m.getAnnotation(LuaMethod.class);
            MethodInfo info = new MethodInfo();

            info.setName(m.getName());
            info.setTypes(m.getParameterTypes());
            getSigAndFill(sb, mMethodMap, m.getReturnType(), lm, info);
        }
    }

    //for constructor. returnType = null
    private void getSigAndFill(StringBuilder sb, Map<String, MethodInfo> map, Class<?> returnType, LuaMethod lm, MethodInfo info) {
        sb.append("(");
        for (Class<?> cla : info.getTypes()) {
            sb.append(typeToSig(cla));
        }
        sb.append(")");
        sb.append(returnType != null ? typeToSig(returnType) : "V");
        info.setSig(sb.toString());
        sb.delete(0, sb.length());

        if (lm != null && lm.value().length() > 0) {
            map.put(lm.value(), info);
        } else {
            int index = getNextMethodIndex(info.getName());
            if (index > 0) {
                map.put(info.getName() + index, info);
            } else {
                if(map.containsKey(info.getName())){
                    mIndexMap.put(info.getName(), 1);
                    map.put(info.getName() + 1, info);
                }else {
                    map.put(info.getName(), info);
                }
            }
        }
    }

    public MethodInfo getMethodInfo(String name){
        return mMethodMap.get(name);
    }
    public MethodInfo getConstructorInfo(String name){
        return mConstructorMap.get(name);
    }

    private int getNextMethodIndex(String expectName) {
        Integer index = mIndexMap.get(expectName);
        if(index == null){
            return 0;
        }
        mIndexMap.put(expectName, index + 1);
        return index + 1;
    }
    private static String typeToSig(Class<?> type) {
        if(type.isArray()){
            return "["+ typeToSig(type.getComponentType());
        }
        if(type == void.class){
            return "V";
        }
        String sig = sBases.get(type);
        if(sig != null){
            return sig;
        }
        String[] strs = type.getName().split("\\.");
        StringBuilder sb = new StringBuilder();
        sb.append("L");
        for (int i = 0; i < strs.length ; i++) {
            sb.append(strs[i]);
            if(i != strs.length - 1){
                sb.append("/");
            }
        }
        sb.append(";");
        return sb.toString();
    }
}
