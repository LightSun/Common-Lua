package com.heaven7.java.lua.iota;

import android.widget.Adapter;

import com.heaven7.java.lua.LuaTypeAdapter;
import com.heaven7.java.lua.adapter.BooleanLuaTypeAdapter;
import com.heaven7.java.lua.adapter.ByteLuaTypeAdapter;
import com.heaven7.java.lua.adapter.CharLuaTypeAdapter;
import com.heaven7.java.lua.adapter.DoubleLuaTypeAdapter;
import com.heaven7.java.lua.adapter.FloatLuaTypeAdapter;
import com.heaven7.java.lua.adapter.IntLuaTypeAdapter;
import com.heaven7.java.lua.adapter.LongLuaTypeAdapter;
import com.heaven7.java.lua.adapter.ShortLuaTypeAdapter;
import com.heaven7.java.lua.adapter.StringLuaTypeAdapter;
import com.heaven7.java.lua.internal.$ReflectyTypes;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AbstractLuaTypeAdapterManager implements LuaTypeAdapterManager {

    private static final Map<Type, LuaTypeAdapter> sBaseAdapters = new HashMap<>();

    private final Map<TypeNode, LuaTypeAdapter> sAdapterMap = new HashMap<>();

    @Override
    public LuaReflectyContext getReflectyContext() {
        return null;
    }
    @Override
    public LuaTypeAdapter getTypeAdapter(TypeNode genericNode) {
        return sAdapterMap.get(genericNode);
    }
    @Override
    public void registerTypeAdapter(Type type, LuaTypeAdapter adapter) {
        sAdapterMap.put($ReflectyTypes.getTypeNode(type), adapter);
    }
    @Override
    public void registerBasicTypeAdapter(Class<?> baseType, LuaTypeAdapter adapter) {

    }
    @Override
    public LuaTypeAdapter getBasicTypeAdapter(Class<?> baseType) {
        return sBaseAdapters.get(baseType);
    }

    @Override
    public LuaTypeAdapter getKeyAdapter(Class<?> type) {
        return null;
    }
    @Override
    public LuaTypeAdapter getValueAdapter(Class<?> type) {
        return null;
    }
    @Override
    public LuaTypeAdapter getSetElementAdapter(Class<?> type) {
        return null;
    }
    @Override
    public LuaTypeAdapter getListElementAdapter(Class<?> type) {
        return null;
    }
    @Override
    public LuaTypeAdapter getCollectionElementAdapter(Class<?> type) {
        return null;
    }
    @Override
    public LuaTypeAdapter createCollectionTypeAdapter(Class<?> collectionClass, LuaTypeAdapter component) {
        return null;
    }

    @Override
    public LuaTypeAdapter createSetTypeAdapter(Class<?> type, LuaTypeAdapter component) {
        return null;
    }

    @Override
    public LuaTypeAdapter createListTypeAdapter(Class<?> type, LuaTypeAdapter component) {
        return null;
    }

    @Override
    public LuaTypeAdapter createArrayTypeAdapter(Class<?> componentClass, LuaTypeAdapter componentAdapter) {
        return null;
    }

    @Override
    public LuaTypeAdapter createMapTypeAdapter(Class<?> mapClazz, LuaTypeAdapter keyAdapter, LuaTypeAdapter valueAdapter) {
        return null;
    }

    @Override
    public LuaTypeAdapter createObjectTypeAdapter(Class<?> objectClazz) {
        return null;
    }

    static {
        LuaTypeAdapter convertor = new BooleanLuaTypeAdapter();
        sBaseAdapters.put(boolean.class, convertor);
        sBaseAdapters.put(Boolean.class, convertor);

        convertor = new ByteLuaTypeAdapter();
        sBaseAdapters.put(byte.class, convertor);
        sBaseAdapters.put(Byte.class, convertor);

        convertor = new CharLuaTypeAdapter();
        sBaseAdapters.put(char.class, convertor);
        sBaseAdapters.put(Character.class, convertor);

        convertor = new ShortLuaTypeAdapter();
        sBaseAdapters.put(short.class, convertor);
        sBaseAdapters.put(Short.class, convertor);

        convertor = new IntLuaTypeAdapter();
        sBaseAdapters.put(int.class, convertor);
        sBaseAdapters.put(Integer.class, convertor);

        convertor = new LongLuaTypeAdapter();
        sBaseAdapters.put(long.class, convertor);
        sBaseAdapters.put(Long.class, convertor);

        convertor = new FloatLuaTypeAdapter();
        sBaseAdapters.put(float.class, convertor);
        sBaseAdapters.put(Float.class, convertor);

        convertor = new DoubleLuaTypeAdapter();
        sBaseAdapters.put(double.class, convertor);
        sBaseAdapters.put(Double.class, convertor);

        sBaseAdapters.put(String.class, new StringLuaTypeAdapter());
    }
}
