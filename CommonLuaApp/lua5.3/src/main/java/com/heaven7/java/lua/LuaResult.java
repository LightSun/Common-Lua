package com.heaven7.java.lua;

public final class LuaResult {

    public static final int MAX_RESULT_COUNT = 5;

    private Lua2JavaValue value1;
    private Lua2JavaValue value2;
    private Lua2JavaValue value3;
    private Lua2JavaValue value4;
    private Lua2JavaValue value5;

    private LuaResult(){}

    public static LuaResult of(LuaState luaState, int resultCount) {
        LuaResult result = new LuaResult();
        switch (resultCount){
            case 1:
                result.value1 = luaState.getLuaValue(-1);
                break;

            case 2:
                result.value2 = luaState.getLuaValue(-1);
                result.value1 = luaState.getLuaValue(-2);
                break;

            case 3:
                result.value3 = luaState.getLuaValue(-1);
                result.value2 = luaState.getLuaValue(-2);
                result.value1 = luaState.getLuaValue(-3);
                break;

            case 4:
                result.value4 = luaState.getLuaValue(-1);
                result.value3 = luaState.getLuaValue(-2);
                result.value2 = luaState.getLuaValue(-3);
                result.value1 = luaState.getLuaValue(-4);
                break;

            case 5:
                result.value5 = luaState.getLuaValue(-1);
                result.value4 = luaState.getLuaValue(-2);
                result.value3 = luaState.getLuaValue(-3);
                result.value2 = luaState.getLuaValue(-4);
                result.value1 = luaState.getLuaValue(-5);
                break;

            default:
                throw new UnsupportedOperationException("currently support max 5 result count.");
        }
        return result;
    }

    public Lua2JavaValue getValue1() {
        return value1;
    }
    public void setValue1(Lua2JavaValue value1) {
        this.value1 = value1;
    }
    public Lua2JavaValue getValue2() {
        return value2;
    }
    public void setValue2(Lua2JavaValue value2) {
        this.value2 = value2;
    }

    public Lua2JavaValue getValue3() {
        return value3;
    }
    public void setValue3(Lua2JavaValue value3) {
        this.value3 = value3;
    }

    public Lua2JavaValue getValue4() {
        return value4;
    }
    public void setValue4(Lua2JavaValue value4) {
        this.value4 = value4;
    }

    public Lua2JavaValue getValue5() {
        return value5;
    }
    public void setValue5(Lua2JavaValue value5) {
        this.value5 = value5;
    }
}
