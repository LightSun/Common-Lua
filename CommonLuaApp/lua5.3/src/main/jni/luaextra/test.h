//
// Created by Administrator on 2019/7/23.
//

#ifndef COMMONLUAAPP_TEST_H
#define COMMONLUAAPP_TEST_H

extern "C"{

#include "../lua/lua.h"

#include "../lua/lualib.h"
#include "../lua/lauxlib.h"
#include "../lua/luaconf.h"
};

//调用测试的lua
void call_testLua(lua_State* L, char* file);


template <class T>
class LuaPort{
public:

    /* member function map */
    struct RegType {
        const char* name;
        const int(T::*mfunc)(lua_State*);
    };

    static void Register(lua_State* L)
    {
        // 导出一个方法创建c++, 因为创建c++对象是在lua中发起的
        lua_pushcfunction(L, &LuaPort::constructor);
        lua_setglobal(L, T::className);// pushglobal

        //创建userdata要用的元表(其名为Foo), 起码要定义__gc方法, 以便回收内存
        luaL_newmetatable(L, T::className);
        lua_pushstring(L, "__gc");
        lua_pushcfunction(L, &LuaPort::gc_obj);
        lua_settable(L, -3);
    }

    static int constructor(lua_State* L){

        // 1. 构造c++对象
        T* obj = new T(L);
        // 2. 新建一个表 tt = {}
        lua_newtable(L);

        // 3. 新建一个userdata用来持有c++对象
        T** a = (T** )lua_newuserdata(L, sizeof(T*));
        *a = obj;

        // 4. 设置lua userdata的元表
        luaL_getmetatable(L, T::className);
        lua_setmetatable(L, -2);

        // 5. tt[0] = userdata
        lua_pushnumber(L, 0);
        lua_insert(L, -2);
        lua_settable(L, -3);

        // 6. 向table中注入c++函数 tt[1]
        for (int i = 0; T::Register[i].name; ++i){
            lua_pushstring(L, T::Register[i].name);
            lua_pushnumber(L, i);
            lua_pushcclosure(L, &LuaPort::proxy, 1);
            lua_settable(L, -3);
        }

        // 7. 把这个表返回给lua
        return 1;
    }

    static int proxy(lua_State* L){
        // 取出function id
        int i = (int)lua_tonumber(L, lua_upvalueindex(1));

        // 取tt[0] 及 obj
        lua_pushnumber(L, 0);
        lua_gettable(L, 1);
        T** obj = (T**)luaL_checkudata(L, -1, T::className);
        lua_remove(L, -1);

        // 实际的调用函数
        return ((*obj)->*(T::Register[i].mfunc))(L);
    }

    static int gc_obj(lua_State* L){
        T** obj = (T**)luaL_checkudata(L, -1, T::className);
        delete (*obj);
        return 0;
    }
};


/*static void Register(lua_State* L)

{
    lua_pushcfunction(L, LuaPort::construct);
    lua_setglobal(L,  "Foo");


    luaL_newmetatable(L, "Foo");

    lua_pushstring(L, "__gc");

    lua_pushcfunction(L, &LuaPort::gc_obj);

    lua_settable(L, -3);



    // ----- 不一样的

    // 把方法也注册进userdata的元表里

    for (int i =  0; FooWrapper::Functions[i].name; ++i)

    {

        lua_pushstring(L, FooWrapper::Functions[i].name);

        lua_pushnumber(L, i);

        lua_pushcclosure(L, &LuaPort::porxy, 1);

        lua_settable(L, -3);

    }



    // 注册__index方法

    lua_pushstring(L, "__index");

    lua_pushvalue(L, -2);

    lua_settable(L, -3);

}


static int constructor(lua_State* L)
{
    FooWrapper* obj = new FooWrapper(L);
    FooWrapper** a = (FooWrapper**)lua_newuserdata(L, sizeof(FooWrapper*));
    *a = obj;

    luaL_getmetatable(L, "Foo");
    lua_setmetatable(L, -2);
    return 1;
}



static int porxy(lua_State* L){

    int i = (int)lua_tonumber(L, lua_upvalueindex(1));

    FooPort** obj = (FooPort**)luaL_checkudata(L, 1, "Foo");

    return ((*obj)->*(FooWrapper::FunctionS[i].mfunc))(L);

}*/

#endif //COMMONLUAAPP_TEST_H
