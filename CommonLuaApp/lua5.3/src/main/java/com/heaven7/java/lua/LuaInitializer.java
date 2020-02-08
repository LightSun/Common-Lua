package com.heaven7.java.lua;

import com.heaven7.java.lua.anno.LuaAdapter;
import com.heaven7.java.lua.anno.LuaExpose;
import com.heaven7.java.lua.anno.LuaField;
import com.heaven7.java.lua.anno.LuaMethod;
import com.heaven7.java.lua.iota.ILuaTypeAdapterManager;
import com.heaven7.java.lua.iota.LuaTypeAdapterManager;
import com.heaven7.java.lua.iota.SimpleLuaReflectyContext;
import com.heaven7.java.lua.iota.obj.ReflectyBuilder;
import com.heaven7.java.lua.iota.obj.SimpleLuaReflectyDelegate;

public final class LuaInitializer {

    @SuppressWarnings("unchecked")
    private static ILuaTypeAdapterManager sTAM = new LuaTypeAdapterManager(new SimpleLuaReflectyContext(),
            new ReflectyBuilder()
                    .classAnnotation(LuaAdapter.class)
                    .fieldAnnotation(LuaField.class)
                    .methodAnnotation(LuaMethod.class)
                    .inheritAnnotation(LuaExpose.class)
                    .delegate(new SimpleLuaReflectyDelegate())
                    .build());

    public static void setLuaTypeAdapterManager(ILuaTypeAdapterManager tam) {
        sTAM = tam;
    }
    public static ILuaTypeAdapterManager getLuaTypeAdapterManager() {
        return sTAM;
    }

}
