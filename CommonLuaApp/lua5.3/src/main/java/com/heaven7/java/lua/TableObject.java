package com.heaven7.java.lua;

//lua table, lua_wrap_java, userdata.
public final class TableObject {

    private static final String M_TRAVEL          = "__travel";
    private static final String M_GET_COLL_TYPE   = "__getCollectionType";

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
    //return true if can travel
    public boolean travel(LuaState luaState, LuaTraveller lt){
        //get collection type as field
        int collType = luaState.getCollectionType(index);
        if(collType == LuaState.COLLECTION_TYPE_UNKNOWN){
            collType = LuaState.COLLECTION_TYPE_LIST;
        }
        lt.setCollectionType(collType);

        //get travel method
        luaState.pushString(M_TRAVEL);
        luaState.getTable(index);

        if(luaState.getType(-1) != LuaState.TYPE_FUNCTION){
            //no travel method
            luaState.pop(1);
            if(luaState.isNativeWrapper(index)){
                return false;
            }
            int type = luaState.getType(index);
            if(type != LuaState.TYPE_TABLE){
                return false;
            }
            luaState.travel(index, lt);
        }else {
            luaState.pushFunction(new LuaTravelFunction(lt));
            //start travel
            int result = luaState.pcall(1, 0, 0);
            if(result == 0){
                System.out.println("travel ok");
            }else {
                System.out.println("travel failed. " + luaState.toString(-1));
                luaState.pop(1);
            }
        }
        return true;
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

    private static class LuaTravelFunction extends LuaFunction{
        final LuaTraveller lt;

        public LuaTravelFunction(LuaTraveller lt) {
            this.lt = lt;
        }
        @Override
        protected int execute(LuaState state) {
            Lua2JavaValue key = state.getLuaValue(-2);
            Lua2JavaValue value = state.getLuaValue(-1);
            return lt.travel(state.getNativePointer(), key, value);
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
