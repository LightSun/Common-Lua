package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;
import com.heaven7.java.lua.LuaValue;
import com.heaven7.java.lua.internal.LuaUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.heaven7.java.base.util.ArrayUtils.toBooleanArray;
import static com.heaven7.java.base.util.ArrayUtils.toByteArray;
import static com.heaven7.java.base.util.ArrayUtils.toCharArray;
import static com.heaven7.java.base.util.ArrayUtils.toDoubleArray;
import static com.heaven7.java.base.util.ArrayUtils.toFloatArray;
import static com.heaven7.java.base.util.ArrayUtils.toIntArray;
import static com.heaven7.java.base.util.ArrayUtils.toLongArray;
import static com.heaven7.java.base.util.ArrayUtils.toShortArray;

public class ArrayLuaTypeAdapter extends LuaTypeAdapter {

    private static final HashMap<Class<?>, PrimitiveArrayConvertor> sArrayConvertors = new HashMap<>();

    static {
        sArrayConvertors.put(byte.class, new ByteArrayConvertor());
        sArrayConvertors.put(short.class, new ShortArrayConvertor());
        sArrayConvertors.put(int.class, new IntArrayConvertor());
        sArrayConvertors.put(long.class, new LongArrayConvertor());
        sArrayConvertors.put(float.class, new FloatArrayConvertor());
        sArrayConvertors.put(double.class, new DoubleArrayConvertor());
        sArrayConvertors.put(boolean.class, new BoolArrayConvertor());
        sArrayConvertors.put(char.class, new CharArrayConvertor());
    }

    private final Class<?> mComponentClass;
    private final LuaTypeAdapter mComponentAdapter;

    public ArrayLuaTypeAdapter(Class<?> mComponentClass, LuaTypeAdapter mComponentAdapter) {
        this.mComponentClass = mComponentClass;
        this.mComponentAdapter = mComponentAdapter;
    }

    public Object readFromLua(LuaState luaState, LuaValue arg){
        List list = new ArrayList();
        arg.toTableValue(luaState).travel(new CollectionTraveller(mComponentAdapter, list));
        if(mComponentClass.isPrimitive()){
             return sArrayConvertors.get(mComponentClass).convert(list);
        }
        return list.toArray((Object[]) Array.newInstance(mComponentClass, list.size()));
    }

    public int writeToLua(LuaState luaState, Object result){
        luaState.newTable();
        int top = luaState.getTop();
        int length = Array.getLength(result);
        for (int i = 0 ; i < length ; i ++){
            Object ele = Array.get(result, i);
            //must only add one to lua stack
            mComponentAdapter.writeToLua(luaState, ele);
            LuaUtils.checkTopDelta(luaState, top + 1);
            luaState.rawSeti(-2, i + 1); //lua array from 1
        }
        luaState.setCollectionTypeAsMeta(-1, LuaState.COLLECTION_TYPE_LIST);
        return 1;
    }

    private interface PrimitiveArrayConvertor{
        Object convert(List list);
    }
    private static class ByteArrayConvertor implements PrimitiveArrayConvertor{
        @Override
        public Object convert(List list) {
            return toByteArray(list);
        }
    }
    private static class ShortArrayConvertor implements PrimitiveArrayConvertor{
        @Override
        public Object convert(List list) {
            return toShortArray(list);
        }
    }
    private static class IntArrayConvertor implements PrimitiveArrayConvertor{
        @Override
        public Object convert(List list) {
            return toIntArray(list);
        }
    }
    private static class LongArrayConvertor implements PrimitiveArrayConvertor{
        @Override
        public Object convert(List list) {
            return toLongArray(list);
        }
    }
    private static class FloatArrayConvertor implements PrimitiveArrayConvertor{
        @Override
        public Object convert(List list) {
            return toFloatArray(list);
        }
    }
    private static class DoubleArrayConvertor implements PrimitiveArrayConvertor{
        @Override
        public Object convert(List list) {
            return toDoubleArray(list);
        }
    }
    private static class BoolArrayConvertor implements PrimitiveArrayConvertor{
        @Override
        public Object convert(List list) {
            return toBooleanArray(list);
        }
    }
    private static class CharArrayConvertor implements PrimitiveArrayConvertor{
        @Override
        public Object convert(List list) {
            return toCharArray(list);
        }
    }
}
