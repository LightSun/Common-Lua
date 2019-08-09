//
// Created by Administrator on 2019/7/27.
//

#include "LuaRegistry.h"
#include "../common/map2.h"

#define LUA_WRAP "Lua_dynamic_wrap"
#define LUA_REG_CN "L_Classname"

const char LuaBridge::className[] = "LuaBridge";
const LuaRegistry<LuaBridge>::RegType LuaBridge::Register[] = {
        {"call", &LuaBridge::call},
        {"hasField", &LuaBridge::hasField},
        {"hasMethod", &LuaBridge::hasMethod},
        {nullptr, nullptr}
};

/** the temp lua state. which assigned on start and null on end for constructor and method call. */
lua_State * __tempL;

void setTempLuaState(lua_State * L){
    __tempL = L;
}
lua_State *getTempLuaState(){
    return __tempL;
}

void getLuaParam(lua_State* L, int id_value, LuaParam* lp){
    int type = lua_type(L, id_value);
    switch (type){
        case LUA_TNUMBER:{
            lua_Number* a = new lua_Number();
            *a = lua_tonumber(L, id_value);
            lp->value = a;
            lp->type = DTYPE_NUMBER;
            break;
        }
        case LUA_TBOOLEAN:{
            int* a = new int();
            *a = lua_toboolean(L, id_value);
            lp->value = a;
            lp->type = DTYPE_BOOLEAN;
            break;
        }
        case LUA_TSTRING:{
            const char* str = lua_tostring(L, id_value);
            lp->value = (void *) str;
            lp->type = DTYPE_STRING;
            break;
        }
        case LUA_TNIL:{
            lp->value = nullptr;
            lp->type = DTYPE_NULL;
            break;
        }
        case LUA_TLIGHTUSERDATA:{
            luaL_error(L, "Currently, lua param not support for light-userdata.");
            break;
        }
        case LUA_TUSERDATA: {
            void* data = lua_touserdata(L, id_value);
            //unpack for LuaBridge. only class can be dynamic cast.
            LuaBridge* lbPtr = static_cast<LuaBridge *>(data);
            if(lbPtr != nullptr){
                lp->className = const_cast<char *>(lbPtr->getClassname());
                lp->value = lbPtr->getCObject();
                lp->type = DTYPE_LB_OBJECT;
            } else{
                lp->value = data;
                lp->type = DTYPE_OBJECT;
            }
            break;
        }

        case LUA_TTABLE:{
            //TODO not support now
            //need make the table on the top.
            lua_pushnil(L);
            // 现在的栈：-1 => nil; index => table
            Map<const char*, const char*>* tab = new Map<const char*, const char*>();
            int luaCollType = -1;
            while (lua_next(L, id_value))
            {
                // 现在的栈：-1 => value; -2 => key; index => table
                // 拷贝一份 key 到栈顶，然后对它做 lua_tostring 就不会改变原始的 key 值了
                lua_pushvalue(L, -2);
                // 现在的栈：-1 => key; -2 => value; -3 => key; index => table
                const char* key = lua_tostring(L, -1);
                const char* value = lua_tostring(L, -2);

                int kt = lua_type(L, -1);
                int vt = lua_type(L, -2);
                if( (kt == LUA_TNUMBER || kt == LUA_TBOOLEAN || kt == LUA_TSTRING) &&
                        (vt == LUA_TNUMBER || vt == LUA_TBOOLEAN || vt == LUA_TSTRING)  ){
                    tab->put(key, value);
                } else if(kt == LUA_TFUNCTION && key == "getCollectionType"){ //lua defined collection. by heaven7
                    luaCollType = static_cast<int>(lua_tointeger(L, -2));
                }

                // 弹出 value 和拷贝的 key，留下原始的 key 作为下一次 lua_next 的参数
                lua_pop(L, 2);
                // 现在的栈：-1 => key; index => table
            }
            // 现在的栈：index => table （最后 lua_next 返回 0 的时候它已经把上一次留下的 key 给弹出了）
            // 所以栈已经恢复到进入这个函数时的状态
            /*
             *  m.COLLECTION_TYPE_LIST = 1
                m.COLLECTION_TYPE_SET  = 2
                m.COLLECTION_TYPE_MAP  = 3
             */
            switch (luaCollType){
                case 1:{

                    break;
                }
                case 2:{

                    break;
                }
                case 3:{

                    break;
                }
            }
            break;
        }

        default:
            std::stringstream out;
            out << "getLuaParam >>> not support type = " << type;
            luaL_error(L, out.str().c_str());
            break;
    }
}

void getParams(lua_State *L, LuaMediator* holder, int count, int startIdx){
    //br.call(method, size, args...)
    //luaB_dumpStack(L);
    if(count > 0){
        for (int i = 0; i < count; ++i) { //reverse order
            getLuaParam(L, startIdx - (i + 1), &holder->lp[count - 1 - i]);
        }
    }
}

int callImpl(lua_State* L, LuaBridgeCaller* caller, const char* cn){
    //br.call(method, args..., size)
    setTempLuaState(L);
    const lua_Integer count = lua_tointeger(L, -1);
    const char* mname = luaL_checkstring(L, -1 - count - 1);
    LuaMediator* holder = new LuaMediator(count);
    holder->className = cn;

    getParams(L, holder, count, -1);
    const void* result = caller->call(cn, mname, *holder);

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

        case LUA_TTABLE:{

            break;
        }

        default:
            std::stringstream out;
            out << "not support result type = " << rType
                << " for class = " << cn;
            luaL_error(L, out.str().c_str());
            return LUA_ERRRUN;
    }
}

//------------------------
LBCCreator __creator = nullptr;
void initLuaBridge(lua_State* L){
    LuaRegistry<LuaBridge>::Register(L);
}
LuaBridgeCaller *Create(const char *classname, LuaMediator &holder) {
    if(__creator == nullptr){
        return nullptr;
    }
    return __creator(classname, holder);
}
void setLuaBridgeCallerCreator(LBCCreator creator){
    __creator = creator;
}

extern "C"{
    static int call(lua_State* L){
        LuaBridgeCaller ** ud = static_cast<LuaBridgeCaller **>(luaL_checkudata(L, 1, LUA_WRAP));
        const char *const className = getFieldAsStringAndPop(L, LUA_REG_CN);
        return callImpl(L, *ud, className);
    }
    static int hasField(lua_State* L){
        setTempLuaState(L);
        LuaBridgeCaller ** ud = static_cast<LuaBridgeCaller **>(luaL_checkudata(L, 1, LUA_WRAP));
        const char* name = luaL_checkstring(L, -1);
        const char *const className = getFieldAsStringAndPop(L, LUA_REG_CN);
        bool result = (*ud)->hasField(className, name);
        lua_pushboolean(L, result ? 1 : 0);
        setTempLuaState(nullptr);
        return 0;
    }
    static int hasMethod(lua_State* L){
        setTempLuaState(L);
        LuaBridgeCaller ** ud = static_cast<LuaBridgeCaller **>(luaL_checkudata(L, 1, LUA_WRAP));
        const char* name = luaL_checkstring(L, -1);
        const char *const className = getFieldAsStringAndPop(L, LUA_REG_CN);
        bool result = (*ud)->hasMethod(className, name);
        lua_pushboolean(L, result ? 1 : 0);
        setTempLuaState(nullptr);
        return 0;
    }
    static int recycle(lua_State* L){
        LuaBridgeCaller ** ud = static_cast<LuaBridgeCaller **>(luaL_checkudata(L, 1, LUA_WRAP));
        delete(*ud);
        return 0;
    }
}

void lua_wrapObject(lua_State* L, LuaBridgeCaller* caller, const char* classname){
    if(luaL_newmetatable(L, LUA_WRAP)){
        lua_pushvalue(L, -1);
        lua_setfield(L, -2, "__index"); //xxx.__index = xxx

        lua_pushcfunction(L, hasField);
        lua_setfield(L, -2, "hasField");

        lua_pushcfunction(L, hasMethod);
        lua_setfield(L, -2, "hasMethod");

        lua_pushcfunction(L, call);
        lua_setfield(L, -2, "call");

        lua_pushcfunction(L, recycle);
        lua_setfield(L, -2, "__gc");

        lua_pushstring(L, classname);
        lua_setfield(L, -2, LUA_REG_CN);
    }
    LuaBridgeCaller** ud = static_cast<LuaBridgeCaller **>(lua_newuserdata(L, sizeof(LuaBridgeCaller*)));
    *ud = caller;
    luaL_setmetatable(L, LUA_WRAP);
}


