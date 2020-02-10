package com.heaven7.lua.test;

import com.heaven7.java.lua.LuaState;
import com.heaven7.lua.Luaer;
import com.heaven7.lua.bean.Person;

public final class JavaCallerTest {

    //test load 'JavaCaller.lua' then wra

    /**
     * 1, load 'JavaCaller.lua'
     * 2, wrap a java object
     * 3. call java object method from lua script
     */
    public static void test1(Luaer luaer){
        String errorMsg = luaer.doFile("JavaCaller.lua");
        if(errorMsg != null){
            System.err.println(errorMsg);
            return;
        }
        LuaState luaState = luaer.getLuaState();
        //get wrap method
        luaState.pushString("wrap");
        luaState.getTable(-2);

        Person p = new Person();
        p.setName("heaven7");
        luaState.push(p);
        String msg = luaState.pcallm(1, 1, 0);
        if(msg != null){
            System.err.println(msg);
            return;
        }
        luaState.setGlobal("P");
        int result = luaState.doString("print(P.getName()); P.setName('google'); print( P.getName());");
        try {
            if(result != 0){
                System.err.println(luaState.toString(-1));
            }
        }finally {
            luaState.removeGlobal("P");
        }
    }
}
