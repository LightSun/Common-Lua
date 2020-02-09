//
// Created by Administrator on 2019/7/30.
//

#ifndef COMMONLUAAPP_LUA_BRIDGE_H
#define COMMONLUAAPP_LUA_BRIDGE_H

#include <functional>
#include "lua.hpp"

#define LIB_LUA_WRAPPER "__LIB_common_lua_WRAP__"
#define NAME_COLLECTION_TYPE  "__GetCollectionType"
#define NAME_GET_CPP "getCppObject"

#define COLLECTION_TYPE_UNKNOWN -1;

#define DTYPE_NULL 1
#define DTYPE_NUMBER 2
#define DTYPE_STRING 3
#define DTYPE_BOOLEAN 4
#define DTYPE_TABLE 5
#define DTYPE_FUNC 6
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
typedef void* (*LuaValue_Creator)(int type, long long ptrOrIndex);
typedef void (*Java_Object_Releaser)(void *obj);

void setLuaValue_Creator(LuaValue_Creator creator);
void setJava_Object_Releaser(Java_Object_Releaser releaser);

void* newLuaValue(int type, long long ptrOrIndex);
void releaseJavaObject(void * obj);

void* getLuaValue(lua_State *L, int id_value);


/**
 * travel the lua table.
 * @param L  he lua state
 * @param idx the table idx
 * @param tt the traveller to travel table. return true means need break travel.
 */
void travelTable(lua_State* L, int idx, std::function<int(lua_State*)> func);

#endif //COMMONLUAAPP_LUA_BRIDGE_H
