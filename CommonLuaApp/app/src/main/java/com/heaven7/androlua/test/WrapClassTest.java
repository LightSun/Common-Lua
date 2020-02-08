package com.heaven7.androlua.test;

import com.heaven7.androlua.R;
import com.heaven7.androlua.bean.Person;
import com.heaven7.java.lua.LuaJavaCaller;
import com.heaven7.java.lua.LuaState;

public final class WrapClassTest {

    public static void testBase(LuaState state){
        LuaJavaCaller.registerJavaClass(R.layout.class);
        state.pushClass(R.layout.class, "P", false);
        state.evaluateScript("local id = P.getField(P, 'main'); print('id =', id)");
        state.dumpLuaStack();
    }

    public static void testMethod(LuaState state){
        LuaJavaCaller.registerJavaClass(Person.class);
        state.pushClass(Person.class, "P", false);
        state.evaluateScript("local m = P.getMethod(P, 'getH'); print(m('heaven7'))");
    }
}
