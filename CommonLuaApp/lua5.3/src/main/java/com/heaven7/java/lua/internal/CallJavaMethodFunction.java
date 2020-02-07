package com.heaven7.java.lua.internal;

import com.heaven7.java.lua.LuaFunction;
import com.heaven7.java.lua.LuaState;

import java.lang.reflect.Method;
import java.util.List;

import static com.heaven7.java.lua.LuaJavaCaller.convert;
import static com.heaven7.java.lua.internal.LuaUtils.java2lua;

/**
 * a lua function which invoke java methods
 */
public final class CallJavaMethodFunction extends LuaFunction {

    private final Class<?> clazz;
    private final List<MethodInfo> list;

    public CallJavaMethodFunction(Class<?> clazz, List<MethodInfo> list) {
        this.clazz = clazz;
        this.list = list;
    }

    @Override
    protected int execute(LuaState luaState) {
        final int pCount = luaState.isJavaClass(1) ? luaState.getTop() - 1 : luaState.getTop();
        final Object[] args = new Object[pCount];
        for (int i = 0 ; i < pCount ; i++){
            args[pCount - 1 - i] = luaState.getLuaValue(-1 - i);
        }
        for (int size = list.size(), i = size - 1; i >= 0; i--) {
            MethodInfo mi = list.get(i);
            Object[] out = new Object[mi.getParameterCount()];
            convert(luaState, mi.getTypes(), args, out);
            try {
                Method m = clazz.getMethod(mi.getRawName(), mi.getRawTypes());
                Object result = m.invoke(null, out);
                return java2lua(luaState, m.getGenericReturnType(), result);
            } catch (Exception e) {
                if (i == 0) {
                    //last. still error.
                    luaState.error("execute method error for " + mi);
                    return 1;
                }
            }
        }
        return 0;
    }
}
