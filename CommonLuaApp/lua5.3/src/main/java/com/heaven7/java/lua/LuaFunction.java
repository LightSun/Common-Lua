package com.heaven7.java.lua;

import android.support.annotation.Keep;

/**
 * Created by heaven7 on 2019/7/4.
 */
public abstract class LuaFunction {

    private final LuaState state;
    private final String name;

    public LuaFunction(LuaState state, String name) {
        this.state = state;
        this.name = name;
    }

    public LuaState getLuaState() {
        return state;
    }
    public String getFunctionName(){
        return name;
    }

    public void register(){
        //TODO
    }
    /**
     * called by lua
     * @return The number of values pushed onto the stack.
     */
    @Keep
    public abstract int execute();

    //int name(luastate*)
}
