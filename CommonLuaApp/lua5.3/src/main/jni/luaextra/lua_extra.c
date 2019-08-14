//
// Created by Administrator on 2019/6/29.
//
#include <lapi.h>
#include "lua_extra.h"
#include "../lua/luaconf.h"

#include "string.h"
#define _concatFilePath(s1, s2, s3) \
    strcpy (str,s1); \
    strcat (str,s2);\
    strcat (str,s3);

FileSearcher luaSearcher = NULL;
FileSearcher clibSearcher = NULL;
Lua_print lua_print= NULL;
CreateTempFile _ctf = NULL;

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

void ext_setCreateTempFile(CreateTempFile ctf){
    _ctf = ctf;
}

LUALIB_API void createTempFile(const char* fn, char** out){
    if(_ctf == NULL){
        *out = NULL;
    } else{
        *out = _ctf(fn);
    }
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
Lua_print ext_getLuaPrint(){
    return lua_print;
}

// flag == 1means end
void ext_print(const char *cs, int len, int flag){
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
    return L;
}
void ext_closeLuaState(lua_State* ls){
    lua_close(ls);
}
void ext_closeLuaThread(lua_State *main,lua_State* ls){
    luaE_freethread(main, ls);
}
const char* getFieldAsString(lua_State* L, int idx, const char* name){
    int result = lua_getfield(L, idx, name);
    if(result != 0){
        ext_print("getFieldAsStringAndPop >>> get field as string failed. name = ", 0 , 0);
        ext_print(name, 0 , 1);
        return NULL;
    }
    const char* value = luaL_checkstring(L, -1);
    lua_pop(L, -1);
    return value;
}
