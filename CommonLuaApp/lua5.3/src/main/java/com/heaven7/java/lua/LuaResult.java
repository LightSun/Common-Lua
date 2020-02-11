package com.heaven7.java.lua;

public final class LuaResult {

    public static final int MAX_RESULT_COUNT = 5;

    /*public*/ int k;
    private LuaValue value1;
    private LuaValue value2;
    private LuaValue value3;
    private LuaValue value4;
    private LuaValue value5;

    private LuaState luaState;

    private LuaResult(){}

    /*public*/ static LuaResult of(LuaState luaState, int resultCount) {
        LuaResult result = new LuaResult();
        result.luaState = luaState;
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

    /**
     * release all lua values
     */
    public void release(){
        if(value1 != null){
            value1.recycle();
            value1 = null;
        }
        if(value2 != null){
            value2.recycle();
            value2 = null;
        }
        if(value3 != null){
            value3.recycle();
            value3 = null;
        }
        if(value4 != null){
            value4.recycle();
            value4 = null;
        }
        if(value5 != null){
            value5.recycle();
            value5 = null;
        }
        luaState.restoreLightly(k);
    }

    public LuaValue getValue1() {
        return value1;
    }
    public void setValue1(LuaValue value1) {
        this.value1 = value1;
    }
    public LuaValue getValue2() {
        return value2;
    }
    public void setValue2(LuaValue value2) {
        this.value2 = value2;
    }

    public LuaValue getValue3() {
        return value3;
    }
    public void setValue3(LuaValue value3) {
        this.value3 = value3;
    }

    public LuaValue getValue4() {
        return value4;
    }
    public void setValue4(LuaValue value4) {
        this.value4 = value4;
    }

    public LuaValue getValue5() {
        return value5;
    }
    public void setValue5(LuaValue value5) {
        this.value5 = value5;
    }
}
