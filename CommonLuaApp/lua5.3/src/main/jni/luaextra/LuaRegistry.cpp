//
// Created by Administrator on 2019/7/27.
//

#include "LuaRegistry.h"
#include "../common/map2.h"

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
        return 0;
    }
    static int hasField(lua_State* L){
        return 0;
    }
    static int hasMethod(lua_State* L){
        return 0;
    }
    static void luaRegister(lua_State* L, const char* name){
        luaL_newmetatable(L, name);
        lua_pushvalue(L, -1);
        lua_setfield(L, -2, "__index"); //xxx.index = xxx

        lua_pushcfunction(L, hasField);
        lua_setfield(L, -2, "hasField");
    }
}

void lua_wrapObject(lua_State* L, LuaBridgeCaller* caller, const char* name){

}


