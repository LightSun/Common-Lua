package com.heaven7.java.lua.iota.obj;

import com.heaven7.java.lua.LuaTypeAdapter;
import com.heaven7.java.lua.anno.LuaAdapter;
import com.heaven7.java.lua.anno.LuaExpose;
import com.heaven7.java.lua.anno.LuaField;
import com.heaven7.java.lua.anno.LuaMethod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class SimpleLuaReflectyDelegate implements ReflectyDelegate<LuaTypeAdapter, LuaAdapter, LuaField, LuaMethod, LuaExpose> {
    @Override
    public void sort(List<MemberProxy> out) {

    }
    @Override
    public boolean isAllowInherit(Class<LuaAdapter> clazz) {
        return false;
    }
    @Override
    public boolean isAllowInherit(LuaExpose fieldInherit) {
        return fieldInherit == null || fieldInherit.serialize();
    }
    @Override
    public boolean shouldIncludeField(Field field, LuaField fieldAnno, boolean isInherit) {
        return true;
    }
    @Override
    public boolean shouldIncludeMethod(Method method, LuaMethod methodAnno, boolean isInherit) {
        return false;
    }
    @Override
    public boolean isGetMethod(Method method, LuaMethod mn) {
        return false;
    }
    @Override
    public String getPropertyFromMethod(Method method, LuaMethod mn) {
        return mn != null ? mn.value() : method.getName();
    }
    @Override
    public String getPropertyFromField(Field field, LuaField fn) {
        return fn != null ? fn.value() : field.getName();
    }
    @Override
    public FieldProxy createFieldProxy(Class<?> owner, LuaAdapter classDesc, Field field, String property, LuaField fn) {
        return new FieldProxy(owner, field, property);
    }
    @Override
    public MethodProxy createMethodProxy(Class<?> owner, LuaAdapter classDesc, Method get, Method set, String property, LuaMethod mn) {
        return null;
    }
    @Override
    public LuaTypeAdapter performReflectClass(Class<?> clazz) {
        LuaAdapter la = clazz.getAnnotation(LuaAdapter.class);
        if(la == null){
            return null;
        }
        try {
            return (LuaTypeAdapter) la.value().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("can't create LuaTypeAdapter for class = " + clazz.getName());
        }
    }
}
