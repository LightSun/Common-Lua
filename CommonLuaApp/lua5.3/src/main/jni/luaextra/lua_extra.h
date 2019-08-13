//
// Created by Administrator on 2019/6/29.
//

#ifndef COMMONLUAAPP_LUA_EXTRA_H
#define COMMONLUAAPP_LUA_EXTRA_H

#include "../lua/lprefix.h"

#include "../lua/lua.h"

#include "../lua/lualib.h"
#include "../lua/lauxlib.h"
#include "../lua/luaconf.h"

#define ext_loadLuaScript(L, S) \
        luaL_dostring(L, S)

//get the lua file path by target module. which often used by 'require'
typedef char* (*FileSearcher)(const char * moduleName);
typedef void (*Lua_print)(char* cs, int len, int flag);


// map function to lua. any like: int (*lua_CFunction) (lua_State *L);
LUALIB_API void lua_BindFunctions(lua_State *L, luaL_Reg funcs[], int flag);

/**
 * set lua searcher which used to search lua file. often called by 'require' module.
 * @param searcher the lua file searcher
 */
void ext_setLuaSearcher(FileSearcher s);

/**
 * set the searcher to search c lib
 * @param s the searcher
 */
void ext_setClibSearcher(FileSearcher s);
void ext_setTmpFileDir(const char* name);

void ext_setLua_print(Lua_print lp);
Lua_print ext_getLuaPrint();

int ext_loadLuaFile(lua_State* L, char* filename);

/**
 * create stand lua state and open stand libs.
 * @return
 */
lua_State* ext_newLuaState();

/**
 * create another lua state from main.
 * @param L the main state
 * @return the new state.
 */
lua_State* ext_lua_newthread(lua_State *L);

void ext_closeLuaState(lua_State* ls);

void ext_closeLuaState(lua_State* ls);

/**
 * close lua thread.
 * @param main
 * @param ls
 */
void ext_closeLuaThread(lua_State *main,lua_State* ls);


//====================================================
/**
 * get filed as string then pop it.
 * @param L the state
 * @param idx the id of userdata
 * @param name the key
 * @return the string value
 */
const char* getFieldAsString(lua_State* L, int idx, const char* name);

#endif //COMMONLUAAPP_LUA_EXTRA_H
