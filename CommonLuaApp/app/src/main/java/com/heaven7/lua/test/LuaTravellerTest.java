package com.heaven7.lua.test;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTraveller;
import com.heaven7.java.lua.internal.LuaUtils;

import java.util.Locale;

public final class LuaTravellerTest {

    public static void testBase(LuaState luaState){
        int k = luaState.saveLightly();
        int[] arr = {11,22,33};
        int i = LuaUtils.writeToLua(luaState, arr);
        if(i != 1){
            throw new IllegalStateException();
        }
        int collectionType = luaState.getCollectionType(-1);
        System.out.println("collectionType = " + collectionType);
        class Traveller extends LuaTraveller {
            private final LuaState ls;
            public Traveller(LuaState ls) {
                this.ls = ls;
            }
            @Override
            public int travel(long luaStatePte, Lua2JavaValue key, Lua2JavaValue value) {
                if(checkLuaValueType(ls, key, Lua2JavaValue.TYPE_NUMBER)){
                    checkLuaValueType(ls, value, Lua2JavaValue.TYPE_NUMBER);
                }
                System.out.println("Traveller from >>> LuaTravellerTest__testBase(): key = "
                        + key.toIntValue() + " ,value = " + value.toIntValue());
                return 0;
            }
        }
        luaState.travel(-1, new Traveller(luaState));
        //luaState.dumpLuaStack();
        luaState.restoreLightly(k);
    }

    private static boolean checkLuaValueType(LuaState luaState, Lua2JavaValue lv, int expectType){
        if(lv.getType() != expectType){
            String msg = String.format(Locale.getDefault(),
                    "wrong lua value type. expect is %d, but is %d.", expectType, lv.getType());
            luaState.error(msg);
            return false;
        }
        return true;
    }
}
