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
import com.heaven7.java.lua.iota.TypeToken;
import com.heaven7.lua.bean.Person;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class LuaTypeAdapterTests extends BaseTest {

    private final LuaState mLuaState;

    public LuaTypeAdapterTests(LuaState state) {
        this.mLuaState = state;
    }

    public void testAll() {
        testBases();
        testBaseArrays();
        testObjects();
        testList();
        testSet();
        testMap();
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
        testArray(new int[]{1, 2, 3});
        testArray(new byte[]{1, 2, 3});
        testArray(new short []{1,2,3});
        testArray(new long []{1,2,3});
        testArray(new float []{1.13434f,23434f,3});
        testArray(new double []{1.13434,23434,3});
        testArray(new boolean []{true, false, true});
        testArray(new char []{'a', 'b', 'c'});
        testArray(new String []{"Hello", "Google", "heaven7"});
    }

    public void testObjects(){
        Person p = new Person();
        p.setAge(18);
        p.setName("heaven7");
        testObject(p);

        Person p2 = new Person();
        p2.setAge(56);
        p2.setName("google");
        testObject(p2);
        testArray(new Person[]{p , p2});
    }
    public void testList(){
        System.out.println("start testList: ");
        List<Person> value = new ArrayList<>();
        Person p = new Person();
        p.setAge(18);
        p.setName("heaven7");

        Person p2 = new Person();
        p2.setAge(56);
        p2.setName("google");
        value.add(p);
        value.add(p2);

        testCollection(new TypeToken<List<Person>>(){}.getType(), value);
    }
    public void testSet(){
        System.out.println("start testSet: ");
        Set<Person> value = new HashSet<>();
        Person p = new Person();
        p.setAge(18);
        p.setName("heaven7");

        Person p2 = new Person();
        p2.setAge(56);
        p2.setName("google");
        value.add(p);
        value.add(p2);

        testCollection(new TypeToken<Set<Person>>(){}.getType(), value);
    }
    public void testMap(){
        System.out.println("start testMap: ");
        Person p = new Person();
        p.setAge(18);
        p.setName("heaven7");

        Person p2 = new Person();
        p2.setAge(56);
        p2.setName("google");
        ConcurrentHashMap<Integer, Person> value = new ConcurrentHashMap<>();
        value.put(111, p);
        value.put(222, p2);
        testCollection(new TypeToken<ConcurrentHashMap<Integer, Person>>(){}.getType(), value);
    }
    private void testCollection(Type type, Object value){
        LuaTypeAdapter adapter = LuaTypeAdapter.get(type);
        mustTrue(adapter.writeToLua(mLuaState, value) == 1);
        Object readVal = adapter.readFromLua(mLuaState, mLuaState.getLuaValue(-1));
        mLuaState.pop(1);
        mustTrue(readVal.getClass() == value.getClass());
        mustTrue(value.equals(readVal));
    }
    private void testObject(Object value){
        LuaTypeAdapter adapter = LuaTypeAdapter.get(value.getClass());
        mustTrue(adapter.writeToLua(mLuaState, value) == 1);
        Object readVal = adapter.readFromLua(mLuaState, mLuaState.getLuaValue(-1));
        mLuaState.pop(1);
        mustTrue(readVal.getClass() == value.getClass());
        mustTrue(value.equals(readVal));
    }
    private <V> void testArray(V value) {
        System.out.println("start test : " + value.getClass().getName());
        LuaTypeAdapter adapter = LuaTypeAdapter.get(value.getClass());
        mustTrue(adapter.writeToLua(mLuaState, value) == 1);
        Object readVal = adapter.readFromLua(mLuaState, mLuaState.getLuaValue(-1));
        mLuaState.pop(1);
        mustTrue(readVal.getClass() == value.getClass());
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
