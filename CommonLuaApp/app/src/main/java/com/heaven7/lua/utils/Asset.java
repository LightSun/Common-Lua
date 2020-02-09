package com.heaven7.lua.utils;

public final class Asset {

    public static void mustTrue(boolean result){
        if(!result){
            throw new AssertionError();
        }
    }
}
