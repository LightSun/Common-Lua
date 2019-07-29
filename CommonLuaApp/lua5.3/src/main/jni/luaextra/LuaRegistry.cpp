//
// Created by Administrator on 2019/7/27.
//

#include "LuaRegistry.h"

const char LuaBridge::className[] = "LuaBridge";
const LuaRegistry<LuaBridge>::RegType LuaBridge::Register[] = {
        {"call", &LuaBridge::call},
        {nullptr, nullptr}
};

void getLuaParam(lua_State* L, int id_value, LuaParam* lp){
    int type = lua_type(L, id_value);
    lp->type = type;
    switch (type){
        case LUA_TNUMBER:{
            lua_Number* a = new lua_Number();
            *a = lua_tonumber(L, id_value);
            lp->value = a;
            break;
        }
        case LUA_TBOOLEAN:{
            int* a = new int();
            *a = lua_toboolean(L, id_value);
            lp->value = a;
            break;
        }
        case LUA_TSTRING:{
            const char* str = lua_tostring(L, id_value);
            lp->value = (void *) str;
            break;
        }
        case LUA_TNIL:{
            lp->value = nullptr;
            break;
        }
        default:
            std::stringstream out;
            out << "getLuaParam >>> not support type = " << type;
            luaL_error(L, out.str().c_str());
            break;
    }
}

//------------------------
LBCCreator __creator = nullptr;
void initLuaBridge(lua_State* L){
    LuaRegistry<LuaBridge>::Register(L);
}
LuaBridgeCaller* Create(const char* classname, LuaMediator& holder){
    if(__creator == nullptr){
        return nullptr;
    }
    return __creator(classname, holder);
}
void setLuaBridgeCallerCreator(LBCCreator creator){
    __creator = creator;
}



