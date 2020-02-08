package com.heaven7.java.lua.internal;

import com.heaven7.java.lua.anno.LuaField;
import com.heaven7.java.lua.anno.LuaIgnore;
import com.heaven7.java.lua.anno.LuaMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by heaven7 on 2019/7/29.
 */
public final class ClassInfo {

    private static final Comparator<MethodInfo> COM_MethodInfo = new Comparator<MethodInfo>() {
        @Override
        public int compare(MethodInfo o1, MethodInfo o2) {
            return Integer.compare(o2.getParameterCount(), o2.getParameterCount());
        }
    };

    //private final String mClassName;
    //object info
    private final Map<String, List<MethodInfo>> mMethodMap = new HashMap<>();
    private final Map<String, List<MethodInfo>> mConstructorMap = new HashMap<>();
    //static info for class
    private final Map<String, List<MethodInfo>> mStaticMethodMap = new HashMap<>();
    private final List<FieldInfo> mStaticFields = new ArrayList<>();

    //private final String mClassName;

    public ClassInfo(Class<?> clazz) {
       // this.mClassName = clazz.getName();

        //constructors
        Constructor[] cons = clazz.getConstructors();
        for (Constructor con : cons) {
            Annotation anno = con.getAnnotation(LuaIgnore.class);
            if(anno != null){
                continue;
            }
            LuaMethod lm = (LuaMethod) con.getAnnotation(LuaMethod.class);
            String luaMethodName = lm != null ? lm.value() : "<init>";

            MethodInfo info = Pools.obtainMethod("<init>", luaMethodName, con.getGenericParameterTypes(), con.getParameterTypes());
            fillMap(mConstructorMap, luaMethodName, info);
        }
        //fields
        Field[] fields = clazz.getFields();
        for (Field f : fields){
            //only permit static public
            if((f.getModifiers() & Modifier.STATIC) != Modifier.STATIC){
                continue;
            }
            LuaField lf = f.getAnnotation(LuaField.class);
            String name = lf != null ? lf.value() : f.getName();
            FieldInfo fi = Pools.obtainField(f.getName(), name, f.getGenericType());

            mStaticFields.add(fi);
        }
        //methods
        Method[] methods = clazz.getMethods();
        for (Method m : methods) {
            LuaMethod lm = m.getAnnotation(LuaMethod.class);
            String luaMethodName = lm != null ? lm.value() : m.getName();

            MethodInfo info = Pools.obtainMethod(m.getName(), luaMethodName, m.getGenericParameterTypes(), m.getParameterTypes());
            Map<String, List<MethodInfo>> map = (m.getModifiers() & Modifier.STATIC) != Modifier.STATIC
                   ? mMethodMap : mStaticMethodMap ;
            fillMap(map, luaMethodName, info);
        }
        //sort param count. desc
        for (List<MethodInfo> list : mMethodMap.values()){
            if(list.size() > 1){
                Collections.sort(list, COM_MethodInfo);
            }
        }
        for (List<MethodInfo> list : mStaticMethodMap.values()){
            if(list.size() > 1){
                Collections.sort(list, COM_MethodInfo);
            }
        }
        for (List<MethodInfo> list : mConstructorMap.values()){
            if(list.size() > 1){
                Collections.sort(list, COM_MethodInfo);
            }
        }
    }

    public FieldInfo getStaticFieldInfo(String name) {
        for (FieldInfo fi : mStaticFields){
            if(fi.getName().equals(name)){
                return fi;
            }
        }
        return null;
    }
    public List<MethodInfo> getStaticMethods(String name, int expectParamCount) {
        return getMethods(mStaticMethodMap, name, expectParamCount);
    }
    //desc
    public List<MethodInfo> getMethods(String name, int expectParamCount){
        return getMethods(mMethodMap, name, expectParamCount);
    }
    //desc
    public List<MethodInfo> getConstructors(String name, int expectParamCount){
        return getMethods(mConstructorMap, name, expectParamCount);
    }
    //for constructor. returnType = null
    private static void fillMap(Map<String, List<MethodInfo>> map, String methodName, MethodInfo info) {
        List<MethodInfo> methodInfos = map.get(methodName);
        if(methodInfos == null){
            methodInfos = new ArrayList<>(3);
            map.put(methodName, methodInfos);
        }
        methodInfos.add(info);
    }

    private static List<MethodInfo> getMethods(Map<String, List<MethodInfo>> mMethodMap, String name, int expectParamCount){
        List<MethodInfo> infos = mMethodMap.get(name);
        if(infos == null){
            return null;
        }
        if(expectParamCount < 0){
            return infos;
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
}
