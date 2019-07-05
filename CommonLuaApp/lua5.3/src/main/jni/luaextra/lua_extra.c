//
// Created by Administrator on 2019/6/29.
//
#include <lapi.h>
#include "lua_extra.h"
#include "../lua/luaconf.h"

FileSearcher luaSearcher;
FileSearcher clibSearcher;
Lua_print lua_print;

//flag 1 to global. function. must end with   {NULL, NULL}
LUALIB_API void lua_BindFunctions(lua_State *L, struct luaL_Reg funcs[], int global){
    luaL_newlib(L, funcs);
    const luaL_Reg *lib;
    for (lib = funcs; lib->func; lib++) {
        luaL_requiref(L, lib->name, lib->func, global);
        lua_pop(L, 1); /* remove lib */
    }
}

void ext_setLuaSearcher(FileSearcher s){
    luaSearcher = s;
}
void ext_setClibSearcher(FileSearcher s){
    clibSearcher = s;
}

//the full file name
LUALIB_API char * getLuaFilename(const char* moduleName){
    if(luaSearcher != NULL){
        return luaSearcher(moduleName);
    }
    return NULL;
}
LUALIB_API char * getCLibFilename(const char* moduleName){
    if(clibSearcher != NULL){
        return clibSearcher(moduleName);
    }
    return NULL;
}

void ext_setLua_print(Lua_print lp){
    lua_print = lp;
}

void ext_print(char* cs, int len, int flag){
    if(lua_print != NULL){
        lua_print(cs, len, flag);
    }
}

int ext_loadLuaFile(lua_State* L, char* filename){
    if(luaSearcher != NULL){
        filename = luaSearcher(filename);
    }
    return luaL_dofile(L, filename);
}
lua_State* ext_lua_newthread(lua_State *L){
    return lua_newthread(L);
}
lua_State* ext_newLuaState(){
    lua_State* L = luaL_newstate();
    luaL_openlibs(L);
    /*
    * Ok, now here we go: We pass data to the lua script on the stack.
    * That is, we first have to prepare Lua's virtual stack the way we
    * want the script to receive it, then ask Lua to run it.
    */
    lua_newtable(L);    /* We will pass a table */

    //<- [stack bottom] -- table, index, value [top]
    for (int i = 1; i <= 5; i++) {
        lua_pushnumber(L, i);   /* Push the table index */
        lua_pushnumber(L, i*2); /* Push the cell value */
        lua_rawset(L, -3);      /* Stores the pair in the table */
    }
    return L;
}
void ext_closeLuaState(lua_State* ls){
    lua_close(ls);
}
void ext_closeLuaThread(lua_State *main,lua_State* ls){
    luaE_freethread(main, ls);
}
