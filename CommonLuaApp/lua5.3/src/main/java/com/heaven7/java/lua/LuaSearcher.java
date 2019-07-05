package com.heaven7.java.lua;

/**
 * Created by heaven7 on 2019/7/1.
 */
public interface LuaSearcher {

    String getLuaFilepath(String module);

    String getClibFilepath(String module);
}
