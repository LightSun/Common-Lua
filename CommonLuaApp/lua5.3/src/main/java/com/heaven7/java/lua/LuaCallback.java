package com.heaven7.java.lua;

public interface LuaCallback {
    /**
     * called on function call result.
     * @param state the lua state to receive normal result.
     * @param errorMsg the error msg. when error occurs.
     */
    void onCallResult(LuaState state, String errorMsg);
}