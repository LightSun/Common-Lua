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
#include "../luaextra/lua_internal.h"
};

//调用测试的lua
void call_testLua(lua_State* L, char* content);

void call_testLuaRegistry(lua_State* L, char* content);

void call_testLuaRegistryWrapper(lua_State* L, char* content);


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

        //创建userdata要用的元表(其名为Foo), 定义__gc方法, 回收内存
        luaL_newmetatable(L, T::className); //创建元表，并放入栈顶
        lua_pushstring(L, "__gc");         //入栈
        lua_pushcfunction(L, &LuaPort::gc_obj);  //入栈
        //相当于t[k] = v 并把栈顶前2个弹出栈顶
        lua_settable(L, -3);
    }

    static int constructor(lua_State* L){

        // 1. 构造c++对象
        T* obj = new T(L);
        // 2. 新建一个表 tt = {}

        lua_newtable(L);

        // 3. 新建一个userdata用来持有c++对象
        T** a = (T** )lua_newuserdata(L, sizeof(T*)); //函数按照指定的大小分配一块内存，将对应的userdata放到栈内，并返回内存块的地址
        *a = obj; //{tab, ud}

        /**
         *  3 table userdata : 第一个3 代表的是栈的索引stk[3]. table在这里是lua_newtable.   userdata是lua_newuserdata 创建的.
         */
       // luaB_dumpStack(L);

        // 4. 设置lua userdata的元表. 将之前Register函数注册的元表取出来 ,然后放入到新对象中。
        luaL_getmetatable(L, T::className); //{tab, ud, tab2}
       // luaB_dumpStack(L);
        lua_setmetatable(L, -2); // 等价于lua内 setmetatable(userdata, meta).将栈顶的 meta设置给 -2位置的table. 并出栈

       // luaB_dumpStack(L); //{tab, ud}

        // 5. tt[0] = userdata
        lua_pushnumber(L, 0);
        lua_insert(L, -2); //move 栈顶 '0'(lua_pushnumber) 到 -2的位置 (-1 是栈顶)
       // luaB_dumpStack(L); //{tab, 0, ud}
        lua_settable(L, -3);//{tab}

        // 6. 向table中注入c++函数 tt[1]
        for (int i = 0; T::Register[i].name; ++i){
            lua_pushstring(L, T::Register[i].name);
            lua_pushnumber(L, i); //放入function id;;
            lua_pushcclosure(L, &LuaPort::proxy, 1);
            lua_settable(L, -3);
        }

        // 7. 把这个表返回给lua
        return 1;
    }

    static int proxy(lua_State* L){
        // 取出function id
        int i = (int)lua_tonumber(L, lua_upvalueindex(1));

        // 取tt[0] 即local userdata = tt[0]
        lua_pushnumber(L, 0);
        /**
    将t[k]元素push到栈顶. 其中t是index处的table, k为当前栈顶元素.
    这个函数可能触发index元方法. (lua_rawget 不会触发index)
    调用完成后弹出栈顶元素(key).
         */
        lua_gettable(L, 1);
        //取出
        T** obj = (T**)luaL_checkudata(L, -1, T::className);
        lua_remove(L, -1); //出栈

        // 实际的调用函数
        return ((*obj)->*(T::Register[i].mfunc))(L);
    }

    static int gc_obj(lua_State* L){
        T** obj = (T**)luaL_checkudata(L, -1, T::className);
        delete (*obj);
        return 0;
    }
};

#endif //COMMONLUAAPP_TEST_H
