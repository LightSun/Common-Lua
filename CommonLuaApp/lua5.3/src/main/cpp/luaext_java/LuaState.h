//
// Created by Administrator on 2019/7/2.
//

#ifndef COMMONLUAAPP_LUASTATE_H
#define COMMONLUAAPP_LUASTATE_H

#include "jni.h"
#include "java_env.h"
#include "class_wrapper.h"

extern "C" {
#include "../luaextra/lua_extra.h"

void pushJNIEnv(JNIEnv *env, lua_State *L);

JNIEnv *getEnvFromState(lua_State *L);

//=============== push to stack ================
void lua_pushString_(JNIEnv *env, jclass clazz, jlong ptr, jstring str);
void lua_pushNumber_(JNIEnv *env, jclass clazz, jlong ptr, jdouble val);
void lua_pushBoolean_(JNIEnv *env, jclass clazz, jlong ptr, jboolean val);
void lua_pushnil_(JNIEnv *env, jclass clazz, jlong ptr);
void lua_pushinteger_(JNIEnv *env, jclass clazz, jlong ptr, jlong val);
// LuaState_pushfstring, pushlstring, pushinteger
//lua_pushcfunction ? lua_pushcclousre?

//================ stack manipulation ================
jint lua_checkstack_(JNIEnv *env, jclass clazz, jlong ptr, int n); //检查容量
jint lua_gettop_(JNIEnv *env, jclass clazz, jlong ptr);           //获取栈的大小
void lua_insert_(JNIEnv *env, jclass clazz, jlong ptr, jint i);  //移动top 到 i
void lua_pushvalue_(JNIEnv *env, jclass clazz, jlong ptr,int i); //cp i -> top
void lua_remove_(JNIEnv *env, jclass clazz, jlong ptr,int i);  //rm i
void lua_replace_(JNIEnv *env, jclass clazz, jlong ptr,int i); //rm i, mv top -> i
void lua_settop_(JNIEnv *env, jclass clazz, jlong ptr,int i); //set stack size
void lua_pop_(JNIEnv *env, jclass clazz, jlong ptr,int n);

//=============== read values from stack ====================
jint lua_isboolean_(JNIEnv *env, jclass clazz, jlong ptr, int i); //is stack[i] a bool?
jint lua_isfunction_(JNIEnv *env, jclass clazz, jlong ptr, int i);
jint lua_isnil_(JNIEnv *env, jclass clazz, jlong ptr, int i);
jint lua_isnone_(JNIEnv *env, jclass clazz, jlong ptr, int i);
jint lua_isnumber_(JNIEnv *env, jclass clazz, jlong ptr, int i);
jint lua_isstring_(JNIEnv *env, jclass clazz, jlong ptr, int i);
jint lua_istable_(JNIEnv *env, jclass clazz, jlong ptr, int i);
jint lua_isuserdata_(JNIEnv *env, jclass clazz, jlong ptr, int i);

jint lua_toboolean_(JNIEnv *env, jclass clazz, jlong ptr, int i);
jlong lua_tointeger_(JNIEnv *env, jclass clazz, jlong ptr, int i);
jdouble lua_tonumber_(JNIEnv *env, jclass clazz, jlong ptr, int i);
jstring lua_tostring_(JNIEnv *env, jclass clazz, jlong ptr, int i);
//jobject lua_touserdata_(JNIEnv *env, jclass clazz, jlong ptr, int i); //returns void *

jint lua_type_(JNIEnv *env, jclass clazz, jlong ptr, int i);
jstring lua_typename_(JNIEnv *env, jclass clazz, jlong ptr, int tp);
jstring luaL_typename_(JNIEnv *env, jclass clazz, jlong ptr, int i); // typename(stack[i]))
// lua_tolstring, lua_tointeger
//luaL_optint and etc.

//======================= table op =============
void lua_newtable_(JNIEnv *env, jclass clazz, jlong ptr);
void lua_createtable_(JNIEnv *env, jclass clazz, jlong ptr, jint marr, jint n);  //m,n=arr,rec capacity
void lua_settable_(JNIEnv *env, jclass clazz, jlong ptr, jint i);
void lua_setfield_(JNIEnv *env, jclass clazz, jlong ptr, jint i, jstring k);
void lua_rawset_(JNIEnv *env, jclass clazz, jlong ptr, jint i);
void lua_rawseti_(JNIEnv *env, jclass clazz, jlong ptr, jint i, jlong n);
jint lua_gettable_(JNIEnv *env, jclass clazz, jlong ptr, jint i);            //pop k; push stk[i][k]
jint lua_getfield_(JNIEnv *env, jclass clazz, jlong ptr, jint i, jstring k); //push stk[i][k]
jint lua_rawget_(JNIEnv *env, jclass clazz, jlong ptr, jint i);
jint lua_rawgeti_(JNIEnv *env, jclass clazz, jlong ptr, jint i, jlong n);

jint lua_setmetatable_(JNIEnv *env, jclass clazz, jlong ptr, jint objIndex);
jint lua_getmetatable_(JNIEnv *env, jclass clazz, jlong ptr, jint i);

jint lua_next_(JNIEnv *env, jclass clazz, jlong ptr, jint i);
void lua_setglobal_(JNIEnv *env, jclass clazz, jlong ptr, jstring name);
jint lua_getglobal_(JNIEnv *env, jclass clazz, jlong ptr, jstring name);

//jint lua_objlen_(JNIEnv *env, jclass clazz, jlong ptr, jint i);
//jint lua_rawlen_(JNIEnv *env, jclass clazz, jlong ptr, jint i);
//jint luaL_getmetafield_(JNIEnv *env, jclass clazz, jlong ptr,jint i, jstring str);

//jint lua_equal_(JNIEnv *env, jclass clazz, jlong ptr,jint i, jint j);
//jint lua_lessthan_(JNIEnv *env, jclass clazz, jlong ptr,jint i, jint j);

void lua_concat_(JNIEnv *env, jclass clazz, jlong ptr,jint n);
jint lua_rawequal_(JNIEnv *env, jclass clazz, jlong ptr,jint i, jint j); // no metacalls

//========================== function calls ==================
//void lua_atpanic_(JNIEnv *env, jclass clazz, jlong ptr);
jint lua_pcall_(JNIEnv *env, jclass clazz, jlong ptr, jint nArgs, jint nResults, jint errFunc);
void lua_call_(JNIEnv *env, jclass clazz, jlong ptr, jint nArgs, jint nResults);
//lua_cpcall, luaL_callmeta

//============== running lua code ===========
jint luaL_loadfile_(JNIEnv *env, jclass clazz, jlong ptr, jstring filename);
jint luaL_loadstring_(JNIEnv *env, jclass clazz, jlong ptr, jstring code);
jint luaL_dofile_(JNIEnv *env, jclass clazz, jlong ptr, jstring filename);
jint luaL_dostring_(JNIEnv *env, jclass clazz, jlong ptr, jstring code);
//lua_load

//================= handle error ===============
jint lua_error_(JNIEnv *env, jclass clazz, jlong ptr);
void luaL_checkany_(JNIEnv *env, jclass clazz, jlong ptr, jint n);
jlong luaL_checkinteger_(JNIEnv *env, jclass clazz, jlong ptr, jint n);
jdouble luaL_checknumber_(JNIEnv *env, jclass clazz, jlong ptr, jint n);
jstring luaL_checkstring_(JNIEnv *env, jclass clazz, jlong ptr, jint n);
void luaL_checktype_(JNIEnv *env, jclass clazz, jlong ptr, jint n, jint tp);
//luaL_error(L, str fmt, ...) ,luaL_checklstring
}

#if LUA_VERSION_NUM == 503
#define luaL_checkint(L, arg) (int)(luaL_checkinteger(L, arg))
#define luaL_optint(L, arg, d) (int)(luaL_optinteger(L, arg, d))
#endif

#endif //COMMONLUAAPP_LUASTATE_H
