//
// Created by Administrator on 2020/1/28 0028.
//

#ifndef COMMONLUAAPP_LUA2JAVA_H
#define COMMONLUAAPP_LUA2JAVA_H

#include "../lua/lua.hpp"

extern "C"{
    struct Lua2JavaValue{
        int type;
        union Value{
            lua_Number n;
            int b;
            const char* str;
            int stackIndex;
        };
    };

    typedef struct Lua2JavaValue Lua2JavaValue;
};


#endif //COMMONLUAAPP_LUA2JAVA_H
