//
// Created by Administrator on 2020/2/1 0001.
//

#include "LuaFunc.h"

extern "C"{

static jobject getud(lua_State *L){
    auto classname = lua_tostring(L, lua_upvalueindex(2));
    lua_pushnumber(L, 0);
    lua_gettable(L, lua_upvalueindex(1));
    jobject *ud = static_cast<jobject *>(luaL_checkudata(L, -1, classname));
    lua_pop(L, 1); //pop ud
    return *ud;
}

static int func_gc(lua_State* L){
    auto pEnv = getJNIEnv();
    auto jobj = getud(L);
    pEnv->DeleteGlobalRef(jobj);
    delete(jobj);
    //ext_println("func_gc is called.");
    return 0;
}
static int func_call(lua_State* L){
    //cmp the key world of LIB_LUA_WRAPPER.
    auto key = lua_tostring(L, -1);
    if(key != nullptr){
        if(strcmp(key, LIB_LUA_WRAPPER) == 0){
            lua_pushboolean(L, 1);
            return 1;
        }
    }
    auto jobj = getud(L);
    return executeLuaFunction(jobj, L);
}
}

void pushFunc(JNIEnv *env, jclass clazz, jlong ptr, jobject func, jstring classname,
              jstring globalKey, jboolean toStack){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);

    auto cn = env->GetStringUTFChars(classname, nullptr);

    lua_newtable(L);
    jobject * ud = static_cast<jobject *>(lua_newuserdata(L, sizeof(jobject)));
    *ud = env->NewGlobalRef(func);
    luaL_newmetatable(L, cn); //{tab, ud, meta}

    lua_pushstring(L, "__gc");
    lua_pushvalue(L, -2-2);
    lua_pushstring(L, cn);
    lua_pushcclosure(L, &func_gc, 2);
    lua_rawset(L, -3);

    lua_setmetatable(L, -2);
    //t[0] = ud
    lua_pushnumber(L, 0);
    lua_insert(L, -2);
    lua_rawset(L, -3);
    //call
    lua_pushstring(L, "call");
    lua_pushvalue(L, -2); //push table to up index
    lua_pushstring(L, cn);
    lua_pushcclosure(L, &func_call, 2);
    lua_rawset(L, -3);

    lua_pushstring(L, LIB_LUA_WRAPPER);
    lua_pushboolean(L, 1);
    lua_rawset(L, -3);
    //func
    lua_pushstring(L, "call");
    lua_rawget(L, -2); //{tab, func}

    lua_insert(L, -2);   //{func, tab}
    lua_pop(L, 1);       //{func}
    //set global if need
    if(globalKey != nullptr){
        auto gk = env->GetStringUTFChars(globalKey, nullptr);
        if(toStack){
            lua_pushvalue(L, -1);
        }
        lua_setglobal(L, gk);
        env->ReleaseStringUTFChars(globalKey, gk);
    } else if(!toStack){
        //pop if need
        lua_pop(L, 1);
    }
    env->ReleaseStringUTFChars(classname, cn);
    //luaB_dumpStack(L);
}

void setCollectionTypeAsMeta_(JNIEnv *env, jclass clazz, jlong ptr, jint idx, jint type){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    luaL_checkany(L, idx);

    luaL_newmetatable(L, NAME_COLLECTION_TYPE);//idx -1

    lua_pushstring(L, "__index");
    lua_pushvalue(L, -2);
    lua_rawset(L, -3);

    lua_pushstring(L, NAME_COLLECTION_TYPE);
    lua_pushnumber(L, type);
    lua_rawset(L, -3); //{tab, tab, meta}

    lua_setmetatable(L,  idx < 0 ? idx - 1 : idx); //{ ...t[idx]... }
   // luaB_dumpStack(L);
}
jint getCollectionType_(JNIEnv *env, jclass clazz, jlong ptr, jint idx){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);

    lua_pushstring(L, NAME_COLLECTION_TYPE);
    lua_gettable(L, idx < 0 ? idx - 1 : idx); //{$val}

    //luaB_dumpStack(L);
    if(lua_isnil(L, -1) || lua_type(L, -1) != LUA_TNUMBER){
        lua_pop(L, 1);
        return COLLECTION_TYPE_UNKNOWN;
    }
    auto result = lua_tonumber(L, -1);
    lua_pop(L, 1);
    return static_cast<jint>(result);
}