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

#ifdef __cplusplus
}
#endif

#include "sstream"

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
    virtual void* call(const char* mName,  LuaMediator& holder) = 0;

    //opt impl. used for user data.
    void* getCObject(){
        return nullptr;
    };

    void luaError(const char* msg){
        luaL_error(getTempLuaState(), msg);
    }
};

//=========================
typedef LuaBridgeCaller* (*LBCCreator)(const char* classname, LuaMediator& holder);
void setLuaBridgeCallerCreator(LBCCreator creator);
LuaBridgeCaller *Create(const char *classname, LuaMediator &holder);
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
    const int call(lua_State *L){
        //br.call(method, args..., size)
        setTempLuaState(L);
        const lua_Integer count = lua_tointeger(L, -1);
        const char* mname = luaL_checkstring(L, -1 - count - 1);
        LuaMediator* holder = new LuaMediator(count);
        holder->className = cn;

        getParams(L, holder, count, -1);
        const void* result = obj->call(mname, *holder);

        const int rType = holder->resultType;
        delete holder;
        setTempLuaState(nullptr);

        switch (rType){
            case LUA_TNUMBER:{
                const lua_Number* num = static_cast<const lua_Number*>(result);
                lua_Number n = *num;
                delete num;
                lua_pushnumber(L, n);
                return 1;
            }
            case LUA_TBOOLEAN:{
                const int* num = static_cast<const int *>(result);
                int n = *num;
                delete num;
                lua_pushboolean(L, n == 1);
                return 1;
            }
            case LUA_TSTRING:{
                const char* num = static_cast<const char*>(result);
                lua_pushstring(L, num);
                return 1;
            }
            case LUA_TNIL:{
                return 0;
            }

            case LUA_TLIGHTUSERDATA:{ // for light-userdata .you need managed self.
                lua_pushlightuserdata(L, const_cast<void *>(result));
                return 1;
            }

            default:
                std::stringstream out;
                out << "not support result type = " << rType
                    << " for class = " << cn;
                luaL_error(L, out.str().c_str());
                return LUA_ERRRUN;
        }
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

    void getParams(lua_State *L, LuaMediator* holder, int count, int startIdx){
        //br.call(method, size, args...)
        //luaB_dumpStack(L);
        if(count > 0){
            for (int i = 0; i < count; ++i) { //reverse order
                getLuaParam(L, startIdx - (i + 1), &holder->lp[count - 1 - i]);
            }
        }
    }
};

//===============================================================================
void initLuaBridge(lua_State* L);
#endif
