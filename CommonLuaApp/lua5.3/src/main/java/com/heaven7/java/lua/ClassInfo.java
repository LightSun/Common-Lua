package com.heaven7.java.lua;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by heaven7 on 2019/7/29.
 */
public final class ClassInfo {

    private static final Map<Class<?>, String> sBases = new HashMap<>();
    private static final Comparator<MethodInfo> COM_MethodInfo = new Comparator<MethodInfo>() {
        @Override
        public int compare(MethodInfo o1, MethodInfo o2) {
            return Integer.compare(o2.getParameterCount(), o2.getParameterCount());
        }
    };

    //private final String mClassName;
    //lua_method_name, info
    private final Map<String, List<MethodInfo>> mMethodMap = new HashMap<>();
    private final Map<String, List<MethodInfo>> mConstructorMap = new HashMap<>();

    static {
        sBases.put(boolean.class, "Z");
        sBases.put(byte.class, "B");
        sBases.put(char.class, "C");
        sBases.put(short.class, "S");
        sBases.put(int.class, "I");
        sBases.put(long.class, "J");
        sBases.put(float.class, "F");
        sBases.put(double.class, "D");
        sBases.put(void.class, "V");
    }

    public ClassInfo(Class<?> clazz) {
        //this.mClassName = clazz.getName();

        StringBuilder sb = new StringBuilder();
        //constructors
        Constructor[] cons = clazz.getConstructors();
        for (Constructor con : cons) {
            Annotation anno = con.getAnnotation(LuaIgnore.class);
            if(anno != null){
                continue;
            }
            LuaMethod lm = (LuaMethod) con.getAnnotation(LuaMethod.class);
            String luaMethodName = lm != null ? lm.value() : "<init>";

            MethodInfo info = new MethodInfo();
            info.setName("<init>");
            info.setTypes(con.getGenericParameterTypes());
            info.setRawTypes(con.getParameterTypes());

            getSigAndFill(sb, mConstructorMap, null, luaMethodName, info);
        }
        //methods
        Method[] methods = clazz.getMethods();
        for (Method m : methods) {
            LuaMethod lm = m.getAnnotation(LuaMethod.class);
            String luaMethodName = lm != null ? lm.value() : m.getName();

            MethodInfo info = new MethodInfo();
            info.setName(m.getName());
            info.setTypes(m.getGenericParameterTypes());
            info.setRawTypes(m.getParameterTypes());
            getSigAndFill(sb, mMethodMap, m.getReturnType(), luaMethodName, info);
        }
        //sort param count. desc
        for (List<MethodInfo> list : mMethodMap.values()){
            Collections.sort(list, COM_MethodInfo);
        }
        for (List<MethodInfo> list : mConstructorMap.values()){
            Collections.sort(list, COM_MethodInfo);
        }
    }

    //for constructor. returnType = null
    private static void getSigAndFill(StringBuilder sb, Map<String, List<MethodInfo>> map, Class<?> returnType, String methodName, MethodInfo info) {
        sb.append("(");
        for (Class<?> cla : info.getRawTypes()) {
            sb.append(typeToSig(cla));
        }
        sb.append(")");
        sb.append(returnType != null ? typeToSig(returnType) : "V");
        info.setSig(sb.toString());
        sb.delete(0, sb.length());

        List<MethodInfo> methodInfos = map.get(methodName);
        if(methodInfos == null){
            methodInfos = new ArrayList<>();
            map.put(methodName, methodInfos);
        }
        methodInfos.add(info);
    }
    //desc
    public List<MethodInfo> getMethodInfoes(String name, int expectParamCount){
        List<MethodInfo> infos = mMethodMap.get(name);
        if(infos == null){
            return null;
        }
        List<MethodInfo> list = new ArrayList<>();
        for (MethodInfo mi : infos){
            if(mi.getParameterCount() >= expectParamCount){
                list.add(mi);
            }else {
                break;
            }
        }
        return list;
    }
    //desc
    public List<MethodInfo> getConstructorInfoes(String name, int expectParamCount){
        List<MethodInfo> infos = mConstructorMap.get(name);
        if(infos == null){
            return null;
        }
        List<MethodInfo> list = new ArrayList<>();
        for (MethodInfo mi : infos){
            if(mi.getParameterCount() >= expectParamCount){
                list.add(mi);
            }else {
                break;
            }
        }
        return list;
    }
    private static String typeToSig(Class<?> type) {
        if(type.isArray()){
            return "["+ typeToSig(type.getComponentType());
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
