//
// Created by Administrator on 2019/6/29.
//
#include <lapi.h>
#include "lua_extra.h"
#include "../lua/luaconf.h"
LuaFileSearcher searcher;

//flag 1 to global. function. must end with   {NULL, NULL}
LUALIB_API void lua_BindFunctions(lua_State *L, struct luaL_Reg funcs[], int global){
    luaL_newlib(L, funcs);
    const luaL_Reg *lib;
    for (lib = funcs; lib->func; lib++) {
        luaL_requiref(L, lib->name, lib->func, global);
        lua_pop(L, 1); /* remove lib */
    }
}

void ext_setLuaSearcher(LuaFileSearcher s){
    searcher = s;
}

//the full file name
LUALIB_API char * getLuaFilename(const char* moduleName){
    if(searcher != NULL){
        return searcher(moduleName);
    }
    return NULL;
}

int ext_loadLuaFile(lua_State* L, char* filename){
    if(searcher != NULL){
        filename = searcher(filename);
    }
    return luaL_dofile(L, filename);
}
lua_State* ext_newLuaState(){
    lua_State* state = luaL_newstate();
    luaL_openlibs(state);
    return state;
}
void ext_closeLuaState(lua_State* ls){
    lua_close(ls);
}
void ext_closeLuaThread(lua_State *main,lua_State* ls){
    luaE_freethread(main, ls);
}