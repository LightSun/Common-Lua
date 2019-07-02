//
// Created by Administrator on 2019/7/1.
//

#include "jni.h"
#include "java_env.h"
#include "LuaState.h"

extern "C" {
#include "../luaextra/lua_extra.h"
}

#define CN_LUA_STATE  "com/heaven7/java/lua/LuaState"
/* Constant that is used to index the JNI Environment */
#define LUAJAVAJNIENVTAG      "__JNIEnv"
/* Defines wheter the metatable is of a java Object */
#define LUAJAVAOBJECTIND      "__IsJavaObject"


extern "C" {

JNIEXPORT jlong Java_com_heaven7_java_lua_LuaState_nCreate(JNIEnv *env, jobject obj) {
    lua_State *state = ext_newLuaState();
    return reinterpret_cast<jlong>(state);
}

JNIEXPORT void Java_com_heaven7_java_lua_LuaState_nRelease(JNIEnv *env, jobject obj, jlong ptr) {
    lua_State *state = reinterpret_cast<lua_State *>(ptr);
    ext_closeLuaState(state);
}

jint LuaState_doString(JNIEnv *env, jclass clazz, jlong ptr, jstring str) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *utfStr = (env)->GetStringUTFChars(str, NULL);
    return (jint) luaL_dostring(L, utfStr);
}

jint LuaState_getGlobal(JNIEnv *env, jclass clazz, jlong ptr, jstring str) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *chz = (env)->GetStringUTFChars(str, NULL);

    const int result = lua_getglobal(L, chz);
    (env)->ReleaseStringUTFChars(str, chz);
    return result;
}
jstring LuaState_pushString(JNIEnv *env, jclass clazz, jlong ptr, jstring str) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);

    const char *uniStr = (env)->GetStringUTFChars(str, NULL);
    const char *const result = lua_pushstring(L, uniStr);

    (env)->ReleaseStringUTFChars(str, uniStr);
    return env->NewStringUTF(result);
}
void LuaState_pushNumber(JNIEnv *env, jclass clazz, jlong ptr, jdouble val) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_pushnumber(L, (lua_Number) val);
}
jstring LuaState_toString(JNIEnv *env, jclass clazz, jlong ptr, jint idx) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *str = lua_tostring(L, idx);
    return (env)->NewStringUTF(str);
}
jint LuaState_pcall(JNIEnv *env, jclass clazz, jlong ptr, jint nArgs, jint nResults, jint errFunc) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_pcall(L, nArgs, nResults, errFunc);
}
void
LuaState_call(JNIEnv *env, jclass clazz, jlong ptr, jint nArgs, jint nResults) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_call(L, nArgs, nResults);
}

void pushJNIEnv(JNIEnv *env, lua_State *L) {
    JNIEnv **udEnv;

    lua_pushstring(L, LUAJAVAJNIENVTAG);
    lua_rawget(L, LUA_REGISTRYINDEX);

    if (!lua_isnil(L, -1)) {
        udEnv = (JNIEnv **) lua_touserdata(L, -1);
        *udEnv = env;
        lua_pop(L, 1);
    } else {
        lua_pop(L, 1);
        udEnv = (JNIEnv **) lua_newuserdata(L, sizeof(JNIEnv *));
        *udEnv = env;

        lua_pushstring(L, LUAJAVAJNIENVTAG);
        lua_insert(L, -2);
        lua_rawset(L, LUA_REGISTRYINDEX);
    }
}
JNIEnv *getEnvFromState(lua_State *L) {
    JNIEnv **udEnv;

    lua_pushstring(L, LUAJAVAJNIENVTAG);
    lua_rawget(L, LUA_REGISTRYINDEX);

    if (!lua_isuserdata(L, -1)) {
        lua_pop(L, 1);
        return NULL;
    }

    udEnv = (JNIEnv **) lua_touserdata(L, -1);

    lua_pop(L, 1);

    return *udEnv;
}
};

JNINativeMethod lua_state_methods[] = {
       /* {"nCreate",         "()J",                            (void *) LuaState_nCreate},
        {"nRelease",        "(J)V",                           (void *) LuaState_nRelease},*/
        {"_evaluateScript", "(J" SIG_JSTRING ")I",            (void *) LuaState_doString},
        {"_getGlobal",      "(J" SIG_JSTRING ")I",            (void *) LuaState_getGlobal},
        {"_pushString",     "(J" SIG_JSTRING ")" SIG_JSTRING, (void *) LuaState_pushString},
        {"_pushNumber",     "(JD)V",                          (void *) LuaState_pushNumber},
        {"_toString",       "(JI)" SIG_JSTRING,               (void *) LuaState_toString},
        {"_pcall",          "(JIII)I",                        (void *) LuaState_pcall},
        {"_call",           "(JII)V",                         (void *) LuaState_call},
};

Registration getLuaStateRegistration() {
    return createRegistration(CN_LUA_STATE, lua_state_methods, 7);
}
