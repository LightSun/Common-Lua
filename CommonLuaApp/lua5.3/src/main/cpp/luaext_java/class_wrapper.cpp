//
// Created by Administrator on 2020/2/5 0005.
//

#include "java_env.h"
#include "class_wrapper.h"
#include "../luaextra/LuaRegistry.h"

#define TN_JAVA_CLASS "tn_com.heaven7.java_class"

extern "C"{

    static int wc_getClass(lua_State *L){
        // 1 is tab, 2 is name
        lua_pushnumber(L, 0);
        lua_rawget(L, 1); // 3 is val
        auto cn = lua_tostring(L, -1);
        lua_pop(L, 1); //pop class name
        return call_getStaticClass(L, cn, lua_tostring(L, -1));
    }
    static int wc_getField(lua_State *L){
        // 1 is tab, 2 is name
        lua_pushnumber(L, 0);
        lua_rawget(L, 1); // 3 is val
        auto cn = lua_tostring(L, -1);
        lua_pop(L, 1); //pop class name
        return call_getStaticField(L, cn, lua_tostring(L, -1));
    }
}

void lua_wrapClass(lua_State *L, const char *classname, const char *globalKey,
                   int toStack){
    if(globalKey != nullptr){
        auto type = lua_getglobal(L, globalKey);
        if(type != LUA_TNIL){
            //already have value.
            if(!toStack){
                lua_pop(L, 1);
            }
            return;
        }
        lua_pop(L, 1);
    }
    lua_newtable(L);
    if(luaL_newmetatable(L, TN_JAVA_CLASS)){
        lua_pushvalue(L, -1);
        lua_setfield(L, -2, "__index");

        lua_pushstring(L, "getClass");
        lua_pushcclosure(L, &wc_getClass, 0);
        lua_rawset(L, -3);

        lua_pushstring(L, "getField");
        lua_pushcclosure(L, &wc_getField, 0);
        lua_rawset(L, -3);
    }
    lua_setmetatable(L , -2); //{tab}

    lua_pushnumber(L, 0);
    lua_pushstring(L, classname);
    lua_rawset(L, -3);

    //set global if need
    if(globalKey != nullptr){
        if(toStack){
            lua_pushvalue(L, -1);
        }
        lua_setglobal(L, globalKey);
    }
}