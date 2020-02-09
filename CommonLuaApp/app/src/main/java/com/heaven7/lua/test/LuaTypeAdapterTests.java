package com.heaven7.lua.test;

import com.heaven7.java.lua.LuaState;
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

import java.lang.reflect.Array;

public final class LuaTypeAdapterTests extends BaseTest {

    private final LuaState mLuaState;

    public LuaTypeAdapterTests(LuaState state) {
        this.mLuaState = state;
    }

    public void testAll() {
        testBases();
        testBaseArrays();
    }

    public void testBases() {
        testBase(new ByteLuaTypeAdapter(), Byte.class, Byte.MAX_VALUE);
        testBase(new ShortLuaTypeAdapter(), Short.class, Short.MAX_VALUE);
        testBase(new IntLuaTypeAdapter(), Integer.class, Integer.MAX_VALUE);
        testBase(new LongLuaTypeAdapter(), Long.class, Long.MAX_VALUE);
        testBase(new FloatLuaTypeAdapter(), Float.class, Float.MAX_VALUE);
        testBase(new DoubleLuaTypeAdapter(), Double.class, Double.MAX_VALUE);
        testBase(new BooleanLuaTypeAdapter(), Boolean.class, Boolean.TRUE);
        testBase(new CharLuaTypeAdapter(), Character.class, Character.MAX_VALUE);
        testBase(new StringLuaTypeAdapter(), String.class, "heaven7");
    }

    public void testBaseArrays() {
        testArray(int[].class, new int[]{1, 2, 3});
        testArray(byte[].class, new byte[]{1, 2, 3});
        testArray(short[].class, new short []{1,2,3});
        testArray(long[].class, new long []{1,2,3});
        testArray(float[].class, new float []{1.13434f,23434f,3});
        testArray(double[].class, new double []{1.13434,23434,3});
        testArray(boolean[].class, new boolean []{true, false, true});
        testArray(char[].class, new char []{'a', 'b', 'c'});
        testArray(String[].class, new String []{"Hello", "Google", "heaven7"});
    }

    private <V, T> void testArray(Class<T> tClass, V value) {
        System.out.println("start test : " + tClass.getName());
        LuaTypeAdapter adapter = LuaTypeAdapter.get(value.getClass());
        mustTrue(adapter.writeToLua(mLuaState, value) == 1);
        Object readVal = adapter.readFromLua(mLuaState, mLuaState.getLuaValue(-1));
        mLuaState.pop(1);
        mustTrue(readVal.getClass() == tClass);
        mustTrue(Array.getLength(value) == Array.getLength(readVal));
        for (int i = 0, len = Array.getLength(value); i < len; i++) {
            mustTrue(Array.get(value, i).equals(Array.get(readVal, i)));
        }
    }

    private <V, T> void testBase(LuaTypeAdapter adapter, Class<T> tClass, V value) {
        System.out.println("start test : " + tClass.getName());
        mustTrue(adapter.writeToLua(mLuaState, value) == 1);
        Object readVal = adapter.readFromLua(mLuaState, mLuaState.getLuaValue(-1));
        mLuaState.pop(1);
        mustTrue(readVal.getClass() == tClass);
        mustTrue(readVal.equals(value));
    }
}
