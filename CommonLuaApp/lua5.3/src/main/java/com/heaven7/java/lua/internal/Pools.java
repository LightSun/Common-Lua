package com.heaven7.java.lua.internal;

import com.heaven7.java.base.util.SparseArrayDelegate;
import com.heaven7.java.base.util.SparseFactory;

import java.lang.reflect.Type;
import java.util.Objects;

/*public*/ final class Pools {

    private static final SparseArrayDelegate<FieldInfo> FIELDS = SparseFactory.newSparseArray(30);
    private static final SparseArrayDelegate<MethodInfo> METHODS = SparseFactory.newSparseArray(50);

    public static FieldInfo obtainField(String rawName, String name, Type type){
        int key = Objects.hash(rawName, name, type);
        FieldInfo val = FIELDS.get(key);
        if(val == null){
            FieldInfo info = new FieldInfo();
            info.setName(name);
            info.setRawName(rawName);
            info.setType(type);
            FIELDS.put(key, info);
            return info;
        }
        return val;
    }

    public static MethodInfo obtainMethod(String rawName, String name, Type[] types, Class<?>[] rawTypes){
        int key = Objects.hash(rawName, name, types);
        MethodInfo val = METHODS.get(key);
        if(val == null){
            MethodInfo info = new MethodInfo();
            info.setName(name);
            info.setRawName(rawName);
            info.setTypes(types);
            info.setRawTypes(rawTypes);
            METHODS.put(key, info);
            return info;
        }
        return val;
    }
}
