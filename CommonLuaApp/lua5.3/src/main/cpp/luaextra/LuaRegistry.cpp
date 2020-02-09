//
// Created by Administrator on 2019/7/27.
//

#include <jni.h>
#include "LuaRegistry.h"
#include "../common/map2.h"

//----------------- help memory ---------

const char LuaBridge::className[] = "LuaBridge";
const LuaRegistry<LuaBridge>::RegType LuaBridge::Register[] = {
        {"call",      &LuaBridge::call},
        {"hasField",  &LuaBridge::hasField},
        {"hasMethod", &LuaBridge::hasMethod},
        {nullptr,     nullptr}
};

void getLuaParam(lua_State *L, int id_value, LuaParam *lp) {
    int type = lua_type(L, id_value);
    switch (type) {
        case LUA_TNUMBER: {
            auto *a = new lua_Number();
            *a = lua_tonumber(L, id_value);
            lp->value = newLuaValue(DTYPE_NUMBER, reinterpret_cast<long long int>(a));
            lp->type = DTYPE_LUA2JAVA_VALUE;
            break;
        }
        case LUA_TBOOLEAN: {
            auto *a = new int();
            *a = lua_toboolean(L, id_value);
            lp->value = newLuaValue(DTYPE_BOOLEAN, reinterpret_cast<long long int>(a));
            lp->type = DTYPE_LUA2JAVA_VALUE;
            break;
        }
        case LUA_TSTRING: {
            const char *str = lua_tostring(L, id_value);
            lp->value = newLuaValue(DTYPE_STRING, reinterpret_cast<long long int> (str));
            lp->type = DTYPE_LUA2JAVA_VALUE;
            break;
        }
        case LUA_TNIL: {
            lp->value = newLuaValue(DTYPE_NULL, 0);;
            lp->type = DTYPE_LUA2JAVA_VALUE;
            break;
        }
        case LUA_TLIGHTUSERDATA: {
            luaL_error(L, "Currently, lua param not support for 'light-userdata'.");
            break;
        }
        case LUA_TFUNCTION: {
            lp->value = newLuaValue(DTYPE_FUNC, id_value);;
            lp->type = DTYPE_LUA2JAVA_VALUE;
            break;
        }
        case LUA_TTHREAD: {
            luaL_error(L, "Currently, lua param not support for 'thread'.");
            break;
        }
        case LUA_TUSERDATA: {
            luaL_error(L, "Currently, lua param not support for 'userdata'.");
            //RECEIVE_USERDATA(id_value);
            lp->value = newLuaValue(DTYPE_TABLE, id_value);;
            lp->type = DTYPE_LUA2JAVA_VALUE;
            break;
        }
        case LUA_TTABLE: {
            lp->value = newLuaValue(DTYPE_TABLE, id_value);;
            lp->type = DTYPE_LUA2JAVA_VALUE;
            break;
        }

        default:
            std::stringstream out;
            out << "getLuaParam >>> not support type = " << type;
            luaL_error(L, out.str().c_str());
            break;
    }
}

void getParams(lua_State *L, LuaMediator *holder, int count, int startIdx) {
    //br.call(method, args..., size)
    //luaB_dumpStack(L);
    if (count > 0) {
        for (int i = 0; i < count; ++i) { //reverse order
            //getLuaParam(L, startIdx - (i + 1), &holder->lp[i]);
            getLuaParam(L, startIdx - (i + 1), &holder->lp[count - 1 - i]);
        }
    }
}

int callImpl(lua_State *L, LuaBridgeCaller *caller, const char *cn) {
    //br.call(method, args..., size)
    const lua_Integer count = lua_tointeger(L, -1);
    const char *mname = luaL_checkstring(L, -1 - count - 1);
    LuaMediator *holder = new LuaMediator(count);
    holder->className = cn;
    //lua -> java
    getParams(L, holder, count, -1);
    //java - lua
    auto result = caller->call(L, cn, mname, holder);

    delete holder;
    return result;
}

//------------------------
LBCCreator __creator = nullptr;

void initLuaBridge(lua_State *L) {
    LuaRegistry<LuaBridge>::Register(L);
}

LuaBridgeCaller *CreateLBC(lua_State *L, const char *classname, LuaMediator* holder) {
    if (__creator == nullptr) {
        return nullptr;
    }
    return __creator(L, classname, holder);
}

void setLuaBridgeCallerCreator(LBCCreator creator) {
    __creator = creator;
}

extern "C" {

static LuaBridgeCaller* getLBC(lua_State *L){
    auto classname = lua_tostring(L, lua_upvalueindex(2));
    lua_pushnumber(L, 0);
    lua_gettable(L, lua_upvalueindex(1));
    LuaBridgeCaller **ud = static_cast<LuaBridgeCaller **>(luaL_checkudata(L, -1, classname));
    lua_pop(L, 1); //pop ud
    return *ud;
}

static int call(lua_State *L) {
    LuaBridgeCaller *ud = getLBC(L);

    //luaB_dumpStack(L);
    return callImpl(L, ud, (ud)->getClassname());
}
static int hasField(lua_State *L) {
    LuaBridgeCaller *ud = getLBC(L);

    const char *name = luaL_checkstring(L, -1);
    bool result = (ud)->hasField((ud)->getClassname(), name);
    lua_pushboolean(L, result ? 1 : 0);
    return 1;
}
static int hasMethod(lua_State *L) {
    LuaBridgeCaller *ud = getLBC(L);

    const char *name = luaL_checkstring(L, -1);
    bool result = (ud)->hasMethod((ud)->getClassname(), name);
    lua_pushboolean(L, result ? 1 : 0);
    return 1;
}
static int getCppObject(lua_State *L) {
    LuaBridgeCaller *ud = getLBC(L);
    lua_pushlightuserdata(L, ud->getCObject());
    return 1;
}
static int recycle(lua_State *L) {
    delete (getLBC(L));
    //ext_println("java object is recycled by lua gc.");
    return 0;
}
}

void lua_wrapObject(lua_State *L, LuaBridgeCaller *caller, const char *classname, const char *globalKey,
                    int toStack) {
    if(classname != nullptr){
        caller->setClassname(classname);
    } else{
        classname = caller->getClassname();
    }
    lua_newtable(L);

    LuaBridgeCaller** ud = static_cast<LuaBridgeCaller **>(lua_newuserdata(L, sizeof(LuaBridgeCaller*)));
    *ud = caller;

    luaL_newmetatable(L, classname);

    lua_pushstring(L, "__gc");
    lua_pushvalue(L, -2-2);
    lua_pushstring(L, classname);
    lua_pushcclosure(L, &recycle, 2);
    lua_rawset(L, -3);
    //luaL_setmetatable(L, classname);
    lua_setmetatable(L, -2);
    //luaB_dumpStack(L);
    //t[0]=ud
    lua_pushnumber(L, 0);
    lua_insert(L, -2);
    lua_rawset(L, -3);

    //luaB_dumpStack(L);

    lua_pushstring(L, "hasField");
    lua_pushvalue(L, -2);
    lua_pushstring(L, classname);
    lua_pushcclosure(L, &hasField, 2);
    lua_rawset(L, -3);

    lua_pushstring(L, "hasMethod");
    lua_pushvalue(L, -2);
    lua_pushstring(L, classname);
    lua_pushcclosure(L, &hasMethod, 2);
    lua_rawset(L, -3);

    lua_pushstring(L, "call");
    lua_pushvalue(L, -2); //push table to up index
    lua_pushstring(L, classname);
    lua_pushcclosure(L, &call, 2);
    lua_rawset(L, -3);

    lua_pushstring(L, NAME_GET_CPP);
    lua_pushvalue(L, -2); //push table to up index
    lua_pushstring(L, classname);
    lua_pushcclosure(L, &getCppObject, 2);
    lua_rawset(L, -3);

    lua_pushstring(L, LIB_LUA_WRAPPER);
    lua_pushboolean(L, 1);
    lua_rawset(L, -3);
    //luaB_dumpStack(L);

    //set global if need
    if(globalKey != nullptr){
        if(toStack){
            lua_pushvalue(L, -1);
        }
        lua_setglobal(L, globalKey);
    }
}


