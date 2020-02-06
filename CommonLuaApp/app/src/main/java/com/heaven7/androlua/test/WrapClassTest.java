package com.heaven7.androlua.test;

import com.heaven7.androlua.bean.Person;
import com.heaven7.java.lua.LuaJavaCaller;
import com.heaven7.java.lua.LuaState;

public final class WrapClassTest {

    public static void testBase(LuaState state){
        LuaJavaCaller.registerJavaClass(Person.class);
        state.pushClass(Person.class, "P", false);
        state.evaluateScript("local a = P.getField(P, 'A1'); print('A1 = ', a)");
        state.dumpLuaStack();
    }
}
