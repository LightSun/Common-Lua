package com.heaven7.java.lua.convertors;

import com.heaven7.java.lua.LuaTypeAdapter;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class TypeConvertorFactory {

    private static final Map<Type, LuaTypeAdapter> sBaseConvertors = new HashMap<>();
    private static final Map<Type, LuaTypeAdapter> sRegisterConvertors = new HashMap<>();
    private static final Set<Class> sIncludeChildClasses = new HashSet<>();
    private static final Set<Class> sIncludeChildInterfaces = new HashSet<>();

    static {
        LuaTypeAdapter convertor = new BooleanLuaTypeAdapter();
        sBaseConvertors.put(boolean.class, convertor);
        sBaseConvertors.put(Boolean.class, convertor);

        convertor = new ByteLuaTypeAdapter();
        sBaseConvertors.put(byte.class, convertor);
        sBaseConvertors.put(Byte.class, convertor);

        convertor = new CharLuaTypeAdapter();
        sBaseConvertors.put(char.class, convertor);
        sBaseConvertors.put(Character.class, convertor);

        convertor = new ShortLuaTypeAdapter();
        sBaseConvertors.put(short.class, convertor);
        sBaseConvertors.put(Short.class, convertor);

        convertor = new IntLuaTypeAdapter();
        sBaseConvertors.put(int.class, convertor);
        sBaseConvertors.put(Integer.class, convertor);

        convertor = new LongLuaTypeAdapter();
        sBaseConvertors.put(long.class, convertor);
        sBaseConvertors.put(Long.class, convertor);

        convertor = new FloatLuaTypeAdapter();
        sBaseConvertors.put(float.class, convertor);
        sBaseConvertors.put(Float.class, convertor);

        convertor = new DoubleLuaTypeAdapter();
        sBaseConvertors.put(double.class, convertor);
        sBaseConvertors.put(Double.class, convertor);

        sBaseConvertors.put(String.class, new StringLuaTypeAdapter());
    }

    public static void registerTypeConvertor(Class<?> clazz, LuaTypeAdapter tc , boolean includeChild){
        sRegisterConvertors.put(clazz, tc);
        if(includeChild){
            if(clazz.isInterface()){
                sIncludeChildInterfaces.add(clazz);
            }else {
                sIncludeChildClasses.add(clazz);
            }
        }
    }
    public static void unregisterTypeConvertor(Class<?> clazz){
        sRegisterConvertors.remove(clazz);
        if(clazz.isInterface()){
            sIncludeChildInterfaces.remove(clazz);
        }else {
            sIncludeChildClasses.remove(clazz);
        }
    }

    public static LuaTypeAdapter getTypeConvertor(Class<?> clazz){
        //1, bases
        LuaTypeAdapter convertor = sBaseConvertors.get(clazz);
        if(convertor != null){
            return convertor;
        }
        //2, registers
        convertor = sRegisterConvertors.get(clazz);
        if(convertor != null){
            return convertor;
        }
        // array, collection, map
        if(clazz.isArray()){
            return new ArrayLuaTypeAdapter();
        }else if(Set.class.isAssignableFrom(clazz)){
            return new SetLuaTypeAdapter();
        }else if(List.class.isAssignableFrom(clazz)){
            return new ListLuaTypeAdapter();
        } else if(Collection.class.isAssignableFrom(clazz)){
            return new CollectionLuaTypeAdapter();
        } else if(Map.class.isAssignableFrom(clazz)){
            return new MapLuaTypeAdapter();
        }else {
            //3, check super class
            Class<?> superclass = clazz.getSuperclass();
            if(!sIncludeChildClasses.isEmpty()){
                while (superclass != null){
                    if(sIncludeChildClasses.contains(superclass)){
                        convertor = sRegisterConvertors.get(superclass);
                        if(convertor != null){
                            return convertor;
                        }
                    }
                    superclass = superclass.getSuperclass();
                }
            }
            //4, check super interface
            if(!sIncludeChildInterfaces.isEmpty()){
                superclass = clazz;
                do {
                    for (Class<?> cls : superclass.getInterfaces()){
                        if(sIncludeChildInterfaces.contains(cls)){
                            convertor = sRegisterConvertors.get(cls);
                            if(convertor != null){
                                return convertor;
                            }
                        }
                    }
                    superclass = superclass.getSuperclass();
                }while (superclass != null);
            }
            //wrap java to c++ then bind to lua
            return new ObjectLuaTypeAdapter();
        }
    }
}
