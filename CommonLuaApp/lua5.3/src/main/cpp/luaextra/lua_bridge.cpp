//
// Created by Administrator on 2020/1/28 0028.
//

#include "lua_bridge.h"
#include "sstream"

Lua2JavaValue_Creator _lua2java_creator;
Java_Object_Releaser _java_obj_releaser;

void setLua2JavaValue_Creator(Lua2JavaValue_Creator creator){
    _lua2java_creator = creator;
}
void setJava_Object_Releaser(Java_Object_Releaser releaser){
    _java_obj_releaser = releaser;
}

void* newLua2JavaValue(int type, long long ptrOrIndex){
    return _lua2java_creator(type, ptrOrIndex);
}

void releaseJavaObject(void * obj){
    _java_obj_releaser(obj);
}

void* getLuaValue(lua_State *L, int id_value) {
    int type = lua_type(L, id_value);
    switch (type) {
        case LUA_TNUMBER: {
            auto *a = new lua_Number();
            *a = lua_tonumber(L, id_value);
            return newLua2JavaValue(DTYPE_NUMBER, reinterpret_cast<long long int>(a));
        }
        case LUA_TBOOLEAN: {
            auto *a = new int();
            *a = lua_toboolean(L, id_value);
            return newLua2JavaValue(DTYPE_BOOLEAN, reinterpret_cast<long long int>(a));
        }
        case LUA_TSTRING: {
            const char *str = lua_tostring(L, id_value);
            return newLua2JavaValue(DTYPE_STRING, reinterpret_cast<long long int> (str));
        }
        case LUA_TNIL: {
            return newLua2JavaValue(DTYPE_NULL, 0);;
        }
        case LUA_TFUNCTION: {
            return newLua2JavaValue(DTYPE_FUNC, id_value);;
        }
        case LUA_TUSERDATA: {
            luaL_error(L, "Currently, lua param not support for 'userdata'.");
            return newLua2JavaValue(DTYPE_TABLE, id_value);;
        }
        case LUA_TTABLE: {
            return newLua2JavaValue(DTYPE_TABLE, id_value);;
        }
        //-----------------------------------
        case LUA_TLIGHTUSERDATA: {
            luaL_error(L, "Currently, lua param not support for 'light-userdata'.");
            break;
        }
        case LUA_TTHREAD: {
            luaL_error(L, "Currently, lua param not support for 'thread'.");
            break;
        }
        default:
            std::stringstream out;
            out << "getLuaValue >>> not support type = " << type;
            luaL_error(L, out.str().c_str());
            break;
    }

    return nullptr;
}