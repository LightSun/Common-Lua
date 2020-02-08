package com.heaven7.java.lua.internal;

import java.util.HashMap;
import java.util.Map;

/*public*/ final class SigUtils {

    private static final Map<Class<?>, String> sBases = new HashMap<>();

    static {
        sBases.put(boolean.class, "Z");
        sBases.put(byte.class, "B");
        sBases.put(char.class, "C");
        sBases.put(short.class, "S");
        sBases.put(int.class, "I");
        sBases.put(long.class, "J");
        sBases.put(float.class, "F");
        sBases.put(double.class, "D");
        sBases.put(void.class, "V");
    }

    public static String typeToSig(Class<?> type) {
        if(type.isArray()){
            return "["+ typeToSig(type.getComponentType());
        }
        String sig = sBases.get(type);
        if(sig != null){
            return sig;
        }
        String[] strs = type.getName().split("\\.");
        StringBuilder sb = new StringBuilder();
        sb.append("L");
        for (int i = 0; i < strs.length ; i++) {
            sb.append(strs[i]);
            if(i != strs.length - 1){
                sb.append("/");
            }
        }
        sb.append(";");
        return sb.toString();
    }
}
