package com.heaven7.java.lua;

import android.support.annotation.Keep;

/**
 * Created by heaven7 on 2019/7/4.
 */
@Keep
public abstract class LuaFunction {

    /**
     * called by lua
     * @return The number of values pushed onto the stack.
     */
    @Keep
    public int execute(long luaStatePtr){
        LuaState state = new LuaState(luaStatePtr);
        return execute(state);
    }

    /**
     * execute this function
     * @param state the lua state
     * @return the result count
     */
    protected abstract int execute(LuaState state);
}
