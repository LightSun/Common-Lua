package com.heaven7.java.lua;

import android.os.SystemClock;

public final class CppObjectMember {

    private static final String PREFIX = "__cpp__";
    private String field;
    private String methodPtr;

    public String getField() {
        return field;
    }
    public void setField(String field) {
        this.field = field;
    }

    public String getMethodPtr() {
        return methodPtr;
    }
    public void setMethodPtr(String methodPtr) {
        this.methodPtr = methodPtr;
    }

    public JavaValuer invoke(LuaState luaState, Object cppObj, Object...params){
        return null;
    }

    public void setField(LuaState luaState,Object cppObj,Object param){
        if(field == null){
            throw new IllegalStateException("no filed");
        }
        String alias = PREFIX + SystemClock.elapsedRealtime();
        luaState.pushCppObject(alias, cppObj);
        luaState.pushString(field);
        pushParam(luaState, param);
        //luaState.lua_settable(-3);
        //luaState.setField
        //String scipt = alias + "." + field + "=" +
        //luaState.LdoString()
    }
    public JavaValuer getField(LuaState luaState,Object cppObj){
        if(field == null){
            throw new IllegalStateException("no filed");
        }

        return null;
    }
    private void pushParam(LuaState luaState, Object param) {
        Class<?> clazz = param.getClass();
        //TODO
    }
}

/*
* byte, short, int, long, float, double, boolean, char.
*
* typedef uint8_t  jboolean;       /* unsigned 8 bits
typedef int8_t   jbyte;            /* signed 8 bits
        typedef uint16_t jchar;    /* unsigned 16 bits
        typedef int16_t  jshort;   /* signed 16 bits
        typedef int32_t  jint;     /* signed 32 bits
        typedef int64_t  jlong;    /* signed 64 bits
        typedef float    jfloat;   /* 32-bit IEEE 754
        typedef double   jdouble;  /* 64-bit IEEE 754

       lua ---------------- java
#define DTYPE_NULL 1   --  null
#define DTYPE_NUMBER 2 -- byte, short, int, long, float, double
#define DTYPE_STRING 3 -- string
#define DTYPE_BOOLEAN 4 -- boolean

char* -- string


#define LUA_TNIL		0         ----   null
#define LUA_TBOOLEAN		1     ----   boolean
#define LUA_TLIGHTUSERDATA	2     ----
#define LUA_TNUMBER		3         ----  byte, short, int, long, float, double
#define LUA_TSTRING		4         ----  string
#define LUA_TTABLE		5         ----  list, set , map.. etc.
#define LUA_TFUNCTION		6     ----
#define LUA_TUSERDATA		7     ----  LuaBridge
#define LUA_TTHREAD		8         ----- no support.
* */
