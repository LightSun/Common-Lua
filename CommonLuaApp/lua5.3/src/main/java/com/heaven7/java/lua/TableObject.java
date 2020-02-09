package com.heaven7.java.lua;

import com.heaven7.java.lua.internal.LuaUtils;

import java.lang.ref.WeakReference;

//lua table, lua_wrap_java, userdata.
public final class TableObject {

    private static final String M_TRAVEL          = "__travel";

    /** the table index. often is negative. -1 means top */
    private final int index;
    private WeakReference<LuaState> mWeakState;

    /*public*/ static TableObject from(LuaState state, int stackIndex){
        return new TableObject(state, stackIndex);
    }
    private TableObject(LuaState state, int index) {
        this.mWeakState = new WeakReference<>(state);
        this.index = index;
    }
    public long getIndex() {
        return index;
    }
    //return true if can travel
    public boolean travel(LuaTraveller lt){
        final LuaState luaState = getLuaState();
        if(luaState == null){
            throw new IllegalStateException("you can't save this object all the time.");
        }
        final int idx = LuaUtils.adjustIdx(luaState, index);
        //get collection type as field
        int collType = luaState.getCollectionType(idx);
        if(collType == LuaState.COLLECTION_TYPE_UNKNOWN){
            collType = LuaState.COLLECTION_TYPE_LIST;
        }
        lt.setCollectionType(collType);
        final int k = luaState.saveLightly();

        //get travel method
        luaState.pushString(M_TRAVEL);
        luaState.getTable(idx);

        if(luaState.getType(-1) != LuaState.TYPE_FUNCTION){
            //no travel method
            luaState.pop(1);
            if(luaState.isNativeWrapper(idx)){
                return false;
            }
            int type = luaState.getType(idx);
            if(type != LuaState.TYPE_TABLE){
                return false;
            }
            luaState.travel(idx, lt);
        }else {
            luaState.pushFunction(new LuaTravelFunction(lt));
            //start travel
            int result = luaState.pcall(1, 0, 0);
            if(result == 0){
                System.out.println("travel ok");
            }else {
                System.err.println("travel failed. " + luaState.toString(-1));
            }
            luaState.restoreLightly(k);
        }
        return true;
    }
    public LuaValue call1(String name, LuaParameter... params){
        LuaResult result = call(name, 1, params);
        if(result != null){
            return result.getValue1();
        }
        return null;
    }
    public LuaResult call(String name, int resultCount, LuaParameter... params){
        if(resultCount > LuaResult.MAX_RESULT_COUNT){
            throw new UnsupportedOperationException("only support max lua result is 5.");
        }
        final LuaState luaState = getLuaState();
        final int idx = LuaUtils.adjustIdx(luaState, index);
        final int k = luaState.saveLightly();
        //result=xx.$name(params)
        //1, get func
        luaState.pushString(name);
        luaState.getTable(idx);
        try {
            if(luaState.getType(-1) != LuaState.TYPE_FUNCTION){
                throw new IllegalStateException("can't find method name = " + name);
            }
            //2, push params
            final int pCount = params != null ? params.length : 0;
            if(pCount > 0){
                for (LuaParameter p : params){
                    p.java2lua(luaState);
                }
            }
            //3, for lua_pcall need result count. we here just make a correct count. like 5.
            int result = luaState.pcall(pCount, resultCount, 0);
            if(result != 0){
                System.err.println("call method error. name = " + name + " ,error msg = " + luaState.toString(-1));
            }else {
                 return LuaResult.of(luaState, resultCount);
            }
        }finally {
            //4, restore lua stack
            luaState.restoreLightly(k);
        }
        return null;
    }
    public LuaValue getField(String name){
        final LuaState luaState = getLuaState();
        final int idx = LuaUtils.adjustIdx(luaState, index);
        luaState.pushString(name);
        luaState.getTable(idx);
        LuaValue result = luaState.getLuaValue(-1);
        luaState.pop(1);
        return result;
    }
    public void setField(String name, LuaParameter val){
        final LuaState luaState = getLuaState();
        final int idx = LuaUtils.adjustIdx(luaState, index);
        luaState.pushString(name);
        val.java2lua(luaState);
        luaState.setTable(idx);
    }

    private LuaState getLuaState(){
        final LuaState luaState = mWeakState.get();
        if(luaState == null){
            throw new IllegalStateException("you can't save this object all the time.");
        }
        return luaState;
    }

    private static class LuaTravelFunction extends LuaFunction{
        final LuaTraveller lt;

        public LuaTravelFunction(LuaTraveller lt) {
            this.lt = lt;
        }
        @Override
        protected int execute(LuaState state) {
            LuaValue key = state.getLuaValue(-2);
            LuaValue value = state.getLuaValue(-1);
            int result = lt.travel(state.getNativePointer(), key, value);
            if(key != null){
                key.recycle();
            }
            if(value != null){
                value.recycle();
            }
            return result;
        }
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
