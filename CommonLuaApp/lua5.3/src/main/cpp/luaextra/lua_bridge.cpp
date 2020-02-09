//
// Created by Administrator on 2020/1/28 0028.
//

#include "lua_bridge.h"
#include "sstream"

LuaValue_Creator _luaValue_creator;
Java_Object_Releaser _java_obj_releaser;

void setLuaValue_Creator(LuaValue_Creator creator){
    _luaValue_creator = creator;
}
void setJava_Object_Releaser(Java_Object_Releaser releaser){
    _java_obj_releaser = releaser;
}

void* newLuaValue(int type, long long ptrOrIndex){
    return _luaValue_creator(type, ptrOrIndex);
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
            return newLuaValue(DTYPE_NUMBER, reinterpret_cast<long long int>(a));
        }
        case LUA_TBOOLEAN: {
            auto *a = new int();
            *a = lua_toboolean(L, id_value);
            return newLuaValue(DTYPE_BOOLEAN, reinterpret_cast<long long int>(a));
        }
        case LUA_TSTRING: {
            const char *str = lua_tostring(L, id_value);
            return newLuaValue(DTYPE_STRING, reinterpret_cast<long long int> (str));
        }
        case LUA_TNIL: {
            return newLuaValue(DTYPE_NULL, 0);;
        }
        case LUA_TFUNCTION: {
            return newLuaValue(DTYPE_FUNC, id_value);;
        }
        case LUA_TUSERDATA: {
           // luaL_error(L, "Currently, lua param not support for 'userdata'.");
            return newLuaValue(DTYPE_TABLE, id_value);;
        }
        case LUA_TTABLE: {
            return newLuaValue(DTYPE_TABLE, id_value);;
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

void travelTable(lua_State* L, int idx, std::function<int(lua_State*)> func){
    if(lua_istable(L, idx) || lua_isuserdata(L, idx)){
        lua_pushnil(L);                            /* first key */
        const int tIdx = idx < 0 ? idx - 1 : idx;  //idx changed now by first key
        // luaB_dumpStack(L);
        while (lua_next(L, tIdx) != 0) {
            //-2 is key, -1(top) is value
            //if return 1 means need break
            if(func(L)){
                lua_pop(L, 2);
                break;
            }
            /* remove value, keep key for next iterate */
            lua_pop(L, 1);
        }
    } else{
        luaL_error(L, "can't travel table for idx = %d", idx);
    }
}