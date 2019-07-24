//
// Created by Administrator on 2019/7/24.
//

#include "lua_internal.h"

LUALIB_API int luaB_dumpStack(lua_State* L){
    __printImpl("\nbegin dump lua stack: ", 0, 1);
    int i = 0;
    int top = lua_gettop(L);

    char buf[20];
    for (i = 1; i <= top; ++i) {
        int t = lua_type(L, i);
        switch (t) {
            case LUA_TSTRING:
            {
                sprintf(buf, "'%s' ", lua_tostring(L, i));
                __printImpl(buf, 0, 0);
            }
                break;
            case LUA_TBOOLEAN:
            {
                __printImpl(lua_toboolean(L, i) ? "true " : "false ", 0, 0);
            }
                break;

            case LUA_TNUMBER:
            {
                sprintf(buf, "%g ", lua_tonumber(L, i));
                __printImpl(buf, 0, 0);
            }
                break;
            default:
            {
                sprintf(buf, "%s ", lua_typename(L, t));
                __printImpl(buf, 0, 0);
            }
                break;
        }
    }
    __flushPrint();
    return 0;
}