//
// Created by Administrator on 2019/7/30.
//

#ifndef COMMONLUAAPP_LUA_BRIDGE_H
#define COMMONLUAAPP_LUA_BRIDGE_H

#include "lua.hpp"

#define LIB_LUA_WRAPPER "__lib_common_lua_wrap_"
#define NAME_COLLECTION_TYPE  "__GetCollectionType"

#define COLLECTION_TYPE_LIST 1;
#define COLLECTION_TYPE_SET 2;
#define COLLECTION_TYPE_MAP 3;
#define COLLECTION_TYPE_UNKNOWN -1;

#define DTYPE_NULL 1
#define DTYPE_NUMBER 2
#define DTYPE_STRING 3
#define DTYPE_BOOLEAN 4
#define DTYPE_TABLE 5
#define DTYPE_FUNC 6

#define DTYPE_SET 8
#define DTYPE_LIST 9
#define DTYPE_MAP 10
#define DTYPE_LUA2JAVA_VALUE 12

/*
#include "../lua/lua.hpp"
extern "C"{
    struct LuaValue{
        union Value{
            lua_Number n;
            int b;
            const char* str;
            int stackIndex;
        };
        int type;
        Value value;
    };

    typedef struct LuaValue LuaValue;
};
 */

/**
 * create lua2java value object
 * @param type the value type
 * @param ptrOrIndex the pointer or stack index
 * @return  the java object
 */
typedef void* (*Lua2JavaValue_Creator)(int type, long long ptrOrIndex);
typedef void (*Java_Object_Releaser)(void *obj);

void setLua2JavaValue_Creator(Lua2JavaValue_Creator creator);
void setJava_Object_Releaser(Java_Object_Releaser releaser);

void* newLua2JavaValue(int type, long long ptrOrIndex);
void releaseJavaObject(void * obj);

void* getLuaValue(lua_State *L, int id_value);
#endif //COMMONLUAAPP_LUA_BRIDGE_H
