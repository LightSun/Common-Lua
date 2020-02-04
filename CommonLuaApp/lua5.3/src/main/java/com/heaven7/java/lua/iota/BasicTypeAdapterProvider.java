package com.heaven7.java.lua.iota;

import com.heaven7.java.lua.LuaTypeAdapter;

public interface BasicTypeAdapterProvider {

    /**
     * get the base type adapter
     * @param baseType the base type class. see java basic-types.
     * @return the base {@linkplain LuaTypeAdapter}.
     */
    LuaTypeAdapter getBasicTypeAdapter(Class<?> baseType);
}