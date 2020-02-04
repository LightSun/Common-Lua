package com.heaven7.androlua.test;

import com.heaven7.androlua.bean.Person;
import com.heaven7.java.lua.LuaFunction;
import com.heaven7.java.lua.LuaJavaCaller;
import com.heaven7.java.lua.LuaState;

public final class WrapJavaTest {

    private static final String TAG = "WrapJavaTest";

    public static void testPushFunc(LuaState luaState){
        //luaState.dumpLuaStack();
        int k = luaState.saveLightly();
        luaState.pushFunction(new SimpleFunc());
        boolean wrapper = luaState.isNativeWrapper(-1);
        System.out.println("testPushFunc >> isNativeWrapper = " + wrapper);
        luaState.pushString("testPushFunc");
        int result = luaState.pcall(1, 1, 0);
        if(result == 0){
            System.out.println("testPushFunc >>> ok . " + luaState.toString(-1));
        }else {
            System.out.println("testPushFunc >>>  exception = " + luaState.toString(-1));
        }
        luaState.dumpLuaStack();
        luaState.restoreLightly(k);
    }

    public static void testWrapJavaObjectGlobal(LuaState luaState) {
        Person p = new Person();
        p.setAge(18);
        p.setName("Google/Heaven7");
        LuaJavaCaller.registerJavaClass(Person.class);

        int k = luaState.saveLightly();
        luaState.pushGlobal(p, "test");
        luaState.restoreLightly(k);

        k = luaState.saveLightly();
        luaState.getGlobal("test");
        luaState.dumpLuaStack();
        //test invoke 'p.getName()'
        luaState.pushString("call");
        luaState.getTable(-2); //get method
        luaState.dumpLuaStack(); //tab, func

        //br.call(method, args..., size)
        luaState.pushString("getName");
        luaState.pushNumber(0);
        int code = luaState.pcall(2, 1, 0);
        if(code != 0){
            String str = luaState.toString(-1);
            System.out.println("onClickTestWrapJavaObject >>>  exception = " + str);
        }else {
            if(luaState.getType(-1) != LuaState.TYPE_STRING){
                throw new IllegalStateException("result must be string");
            }
            System.out.println("onClickTestWrapJavaObject >>> ok! " + luaState.toString(-1));
        }

        luaState.restoreLightly(k);
    }

    public static void testWrapJavaObject(LuaState luaState) {
        Person p = new Person();
        p.setAge(18);
        p.setName("Google/Heaven7");
        LuaJavaCaller.registerJavaClass(Person.class);

        int k = luaState.saveLightly();
        //luaState.dumpLuaStack();
        luaState.push(p);
        boolean wrapper = luaState.isNativeWrapper(-1);
        System.out.println("testWrapJavaObject >> isNativeWrapper = " + wrapper);

        luaState.pushString("call");
        luaState.getTable(-2); //get method
        luaState.dumpLuaStack(); //tab, func

        //br.call(method, args..., size)
        luaState.pushString("getName");
        luaState.pushNumber(0);
        int code = luaState.pcall(2, 1, 0);
        if(code != 0){
            String str = luaState.toString(-1);
            System.out.println("onClickTestWrapJavaObject >>>  exception = " + str);
        }else {
            if(luaState.getType(-1) != LuaState.TYPE_STRING){
                throw new IllegalStateException("result must be string");
            }
            System.out.println("onClickTestWrapJavaObject >>> ok! " + luaState.toString(-1));
        }
        //luaState.dumpLuaStack();
        luaState.restoreLightly(k);
    }

    private static class SimpleFunc extends LuaFunction{
        @Override
        protected int execute(LuaState state) {
            String inStr = state.toString(-1);
            state.pushString("SimpleFunc__from__" + inStr);
            return 1;
        }
    }
}
