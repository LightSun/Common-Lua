//
// Created by Administrator on 2019/7/24.
//

#ifndef COMMONLUAAPP_LUA_INTERNAL_H
#define COMMONLUAAPP_LUA_INTERNAL_H

#include "../lua/lua.h"
#include "stdio.h"

LUALIB_API char * getLuaFilename(const char* moduleName);
LUALIB_API char * getCLibFilename(const char* moduleName);

/**
 * print the string . if success return 1. or else return 0.
 * @param cs
 * @param len
 * @param flag the flag of this print. if 1 means print right now. or else concat str
 */
LUALIB_API void ext_print(char* cs, int len, int flag);

//static 函数代表是内部函数。在c中
LUALIB_API int luaB_dumpStack(lua_State* L);

#define EXIT_PRINT

#ifdef EXIT_PRINT
#define __printImpl(s, l, f) \
        ext_print(s, l, f)
#define __flushPrint()  \
        ext_print("", 0, 1)
#else
#define __printImpl(s, l, f) \
       lua_writestring(s,l)
#define __flushPrint()  \
       lua_writeline();
#endif

#define ext_prints(cs) \
        ext_print(cs, 0, 0)
#define ext_println(cs) \
        ext_print((char* )cs, 0, 1)

#endif //COMMONLUAAPP_LUA_INTERNAL_H
