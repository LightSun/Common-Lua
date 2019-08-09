//
// Created by Administrator on 2019/7/27.
//

#ifndef COMMONLUAAPP_LUAREGISTRY_H
#define COMMONLUAAPP_LUAREGISTRY_H

#ifdef __cplusplus
extern "C"{
#endif

#include "../lua/lua.h"

#include "../lua/lualib.h"
#include "../lua/lauxlib.h"
#include "../lua/luaconf.h"
#include "lua_internal.h"
#include "lua_extra.h"

#ifdef __cplusplus
}
#endif

#include "sstream"
#include "lua_bridge.h"

template <class T>
class LuaRegistry{
        public:

        /* member function map */
        struct RegType {
            const char* name;
            const int(T::*mfunc)(lua_State*);
        };

        static void Register(lua_State* L)
        {
            lua_pushcfunction(L, &LuaRegistry::constructor);
            lua_setglobal(L, T::className);

            luaL_newmetatable(L, T::className);
            lua_pushstring(L, "__gc");
            lua_pushcfunction(L, &LuaRegistry::gc_obj);
            lua_settable(L, -3);
        }

        static int constructor(lua_State* L){
            T* obj = new T(L);
            lua_newtable(L);

            T** a = (T** )lua_newuserdata(L, sizeof(T*));
            *a = obj;

            luaL_getmetatable(L, T::className);
            lua_setmetatable(L, -2);

            lua_pushnumber(L, 0);
            lua_insert(L, -2);
            lua_settable(L, -3);

            for (int i = 0; T::Register[i].name; ++i){
                lua_pushstring(L, T::Register[i].name);
                lua_pushnumber(L, i);
                lua_pushcclosure(L, &LuaRegistry::proxy, 1);
                lua_settable(L, -3);
            }
            return 1;
        }

        static int proxy(lua_State* L){
            int i = (int)lua_tonumber(L, lua_upvalueindex(1));

            lua_pushnumber(L, 0);
            lua_gettable(L, 1);
            T** obj = (T**)luaL_checkudata(L, -1, T::className);
            lua_remove(L, -1);

            return ((*obj)->*(T::Register[i].mfunc))(L);
        }

        static int gc_obj(lua_State* L){
            T** obj = (T**)luaL_checkudata(L, -1, T::className);
            delete (*obj);
            return 0;
        }
};

//=============================================================

class LuaParam{
public:
    LuaParam(){
        value = nullptr;
    }
    ~LuaParam(){
        if(value != nullptr){
            switch (type){
                case LUA_TNUMBER:{
                    lua_Number* a = (lua_Number *) value;
                    delete a;
                }
                case LUA_TBOOLEAN:{
                    int * a = (int *)value;
                    delete a;
                }
                /*  case LUA_TSTRING:
                  case LUA_TNIL:  */
            }
            value = nullptr;
        }
    }
    int type;
    void* value;
    const char* className;
};
void getLuaParam(lua_State* L, int id_value, LuaParam* lp);
void setTempLuaState(lua_State * L);
lua_State *getTempLuaState();

class LuaMediator{
public:
    LuaMediator(int count){
        this->count = count;
        if(count > 0){
            lp = new LuaParam[count];
        } else{
            lp = nullptr;
        }
    }
    ~LuaMediator(){
        if(lp != nullptr && count > 0){
            delete[](lp);
        }
    }
    int count;
    LuaParam* lp;
    int resultType;
    const char* className;
};

class LuaBridgeCaller{
public:
    virtual void *call(const char *cn, const char *mName ,LuaMediator &holder) = 0;

    //opt impl. used for user data.
    void* getCObject(){
        return nullptr;
    };

    bool hasField(const char *className, const char *name) {
        return false;
    }
    bool hasMethod(const char *className, const char *name) {
        return false;
    }
    const void luaError(const char* msg){
        luaL_error(getTempLuaState(), msg);
    }
};

//=========================
typedef LuaBridgeCaller* (*LBCCreator)(const char* classname, LuaMediator& holder);
void setLuaBridgeCallerCreator(LBCCreator creator);
LuaBridgeCaller *Create(const char *classname, LuaMediator &holder);

void getParams(lua_State *L, LuaMediator* holder, int count, int startIdx);
int callImpl(lua_State* L, LuaBridgeCaller* caller, const char* cn);
//=========================

class LuaBridge{
public:
    static const char className[];
    static const LuaRegistry<LuaBridge>::RegType Register[];

    ~LuaBridge(){
        delete(obj);
        //ext_print("LuaBridge is removed.", 0 , 1);
    }

    LuaBridge(lua_State *L){
        setTempLuaState(L);
        const lua_Integer count = lua_tointeger(L, -1);
        const char* mname = luaL_checkstring(L, -1 - count - 1);
        LuaMediator* holder = new LuaMediator(count);
        getParams(L, holder, count, -1);

        obj = Create(mname, *holder);
        cn = mname;
        delete holder;
        setTempLuaState(nullptr);
    }
    const int hasMethod(lua_State *L){
        const char* name = luaL_checkstring(L, -1);
        bool result = obj->hasMethod(cn, name);
        lua_pushboolean(L, result ? 1 : 0);
        return 0;
    }
    const int hasField(lua_State *L){
        const char* name = luaL_checkstring(L, -1);
        bool result = obj->hasField(cn, name);
        lua_pushboolean(L, result ? 1 : 0);
        return 0;
    }
    const int call(lua_State *L){
        return callImpl(L, obj, cn);
    }

    const char* getClassname(){
        return cn;
    }
    void* getCObject(){
        return obj->getCObject();
    }

private:
    LuaBridgeCaller * obj;
    const char * cn;
};

//===============================================================================
void initLuaBridge(lua_State* L);

/**
 * function which can wrap cpp object to lua.
 * this of use LuaBridgeCaller to wrap any cpp object.
 * @param L the lua state
 * @param caller the bridge caller.
 */
// wrap base, base-array, object, object array
void lua_wrapObject(lua_State* L, LuaBridgeCaller* caller, const char* name);
#endif
