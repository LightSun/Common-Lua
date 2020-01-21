package com.heaven7.java.lua;

public final class TableObject {

    /** the table index. often is negative. -1 means top */
    private final int index;

    public TableObject(int index) {
        this.index = index;
    }
    public long getIndex() {
        return index;
    }
    public Object call(LuaState luaState, String name, Object...params){
        //luaState.pushCppObject(pointer);
        /*
         * 1, push table(cpp-object) .set global '__spec_name__'
         * 2, evaluate script. '$__spec_name__.name=a' /  'local c = $__spec_name__.name' / 'local r = $__spec_name__.name(...params)'
         */
        return null;
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
