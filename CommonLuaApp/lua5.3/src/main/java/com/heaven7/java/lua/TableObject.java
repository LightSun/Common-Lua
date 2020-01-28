package com.heaven7.java.lua;

public final class TableObject {

    /** the table index. often is negative. -1 means top */
    private final int index;

    public static TableObject from(int stackIndex){
        return new TableObject(stackIndex);
    }
    private TableObject(int index) {
        this.index = index;
    }
    public long getIndex() {
        return index;
    }
    public Object call(LuaState luaState, String name, Object...params){
        //TODO
        int top = luaState.getTop();
        //result=xx.$name(params)
        //1, get func
        //luaState.pushTable(index);
        luaState.pushString(name);
        luaState.getTable(index);
        //2, push params

        //3, lua_pcall

        //4, restore lua stack
        return null;
    }
    public Object getField(LuaState luaState, String name){
        int top = luaState.getTop();

        return null;
    }
    public void setField(LuaState luaState, String name, Object val){
        int top = luaState.getTop();
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

char* -- string

#define LUA_TNIL		0         ----   null
#define LUA_TBOOLEAN		1     ----   boolean
#define LUA_TLIGHTUSERDATA	2     ----  -----------
#define LUA_TNUMBER		3         ----  byte, short, int, long, float, double
#define LUA_TSTRING		4         ----  string
#define LUA_TTABLE		5         ----  list, set, map.. etc.
#define LUA_TFUNCTION		6     ----  ----------------
#define LUA_TUSERDATA		7     ----  meta-table
#define LUA_TTHREAD		8         ----- -----------

java                   lua
primitive-array       ->  table
list, set, map        ->  table
object                ->  wrap-table


* */
