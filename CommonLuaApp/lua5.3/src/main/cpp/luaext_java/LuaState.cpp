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

jstring lua_pushString_(JNIEnv *env, jclass clazz, jlong ptr, jstring str) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);

    const char *uniStr = (env)->GetStringUTFChars(str, NULL);
    const char *const result = lua_pushstring(L, uniStr);

    (env)->ReleaseStringUTFChars(str, uniStr);
    return env->NewStringUTF(result);
}
void lua_pushNumber_(JNIEnv *env, jclass clazz, jlong ptr, jdouble val) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_pushnumber(L, (lua_Number) val);
}
void lua_pushBoolean_(JNIEnv *env, jclass clazz, jlong ptr, jboolean val){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_pushboolean(L, val ? 1 : 0);
}
void lua_pushnil_(JNIEnv *env, jclass clazz, jlong ptr){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_pushnil(L);
    //lua_pushcfunction()
}
void lua_pushinteger_(JNIEnv *env, jclass clazz, jlong ptr, jlong val){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_pushinteger(L, val);
    //lua_pushcfunction()
}
//------------------ stack ---------------------
jint lua_checkstack_(JNIEnv *env, jclass clazz, jlong ptr, int n){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_checkstack(L, n);
}
jint lua_gettop_(JNIEnv *env, jclass clazz, jlong ptr){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_gettop(L);
}
void lua_insert_(JNIEnv *env, jclass clazz, jlong ptr, jint index){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_insert(L, index);
}
void lua_pushvalue_(JNIEnv *env, jclass clazz, jlong ptr,int i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_pushvalue(L, i);
}
void lua_remove_(JNIEnv *env, jclass clazz, jlong ptr,int i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_remove(L, i);
}
void lua_replace_(JNIEnv *env, jclass clazz, jlong ptr,int i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_replace(L, i);
}
void lua_settop_(JNIEnv *env, jclass clazz, jlong ptr,int i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_settop(L, i);
}
void lua_pop_(JNIEnv *env, jclass clazz, jlong ptr,int n){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_pop(L, n);
}
//-------------------- read from stack -----------------

jstring lua_tostring_(JNIEnv *env, jclass clazz, jlong ptr, jint idx) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *str = lua_tostring(L, idx);
    return (env)->NewStringUTF(str);
}
jint lua_isboolean_(JNIEnv *env, jclass clazz, jlong ptr, int i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_isboolean(L, i);
}
jint lua_isfunction_(JNIEnv *env, jclass clazz, jlong ptr, int i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_isfunction(L, i);
}
jint lua_isnil_(JNIEnv *env, jclass clazz, jlong ptr, int i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_isnil(L, i);
}
jint lua_isnone_(JNIEnv *env, jclass clazz, jlong ptr, int i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_isnone(L, i);
}
jint lua_isnumber_(JNIEnv *env, jclass clazz, jlong ptr, int i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_isnumber(L, i);
}
jint lua_isstring_(JNIEnv *env, jclass clazz, jlong ptr, int i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_isstring(L, i);
}
jint lua_istable_(JNIEnv *env, jclass clazz, jlong ptr, int i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_istable(L, i);
}
jint lua_isuserdata_(JNIEnv *env, jclass clazz, jlong ptr, int i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_isuserdata(L, i);
}

jint lua_toboolean_(JNIEnv *env, jclass clazz, jlong ptr, int i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_toboolean(L, i);
}
jlong lua_tointeger_(JNIEnv *env, jclass clazz, jlong ptr, int i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_tointeger(L, i);
}
jdouble lua_tonumber_(JNIEnv *env, jclass clazz, jlong ptr, int i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_tonumber(L, i);
}
jobject lua_touserdata_(JNIEnv *env, jclass clazz, jlong ptr, int i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    void *val = lua_touserdata(L, i);
    //TODO
    return nullptr;
}

jint lua_type_(JNIEnv *env, jclass clazz, jlong ptr, int i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_type(L, i);
}
jstring lua_typename_(JNIEnv *env, jclass clazz, jlong ptr, int type){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *tn = lua_typename(L, type);
    return (env)->NewStringUTF(tn);
}
jstring luaL_typename_(JNIEnv *env, jclass clazz, jlong ptr, int i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *tn = luaL_typename(L, i);
    return (env)->NewStringUTF(tn);
}

//--------------------- table op -------------------------
jint lua_getglobal_(JNIEnv *env, jclass clazz, jlong ptr, jstring str) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *chz = (env)->GetStringUTFChars(str, nullptr);

    const int result = lua_getglobal(L, chz);
    (env)->ReleaseStringUTFChars(str, chz);
    return result;
}
void lua_newtable_(JNIEnv *env, jclass clazz, jlong ptr){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_newtable(L);
}
void lua_createtable_(JNIEnv *env, jclass clazz, jlong ptr, jint narr , jint nhash){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_createtable(L, narr, nhash);
}
void lua_settable_(JNIEnv *env, jclass clazz, jlong ptr, jint i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_settable(L, i);
}
void lua_setfield_(JNIEnv *env, jclass clazz, jlong ptr, jint i, jstring k){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *chz = (env)->GetStringUTFChars(k, nullptr);
    lua_setfield(L,i ,chz);
    (env)->ReleaseStringUTFChars(k, chz);
}
void lua_rawset_(JNIEnv *env, jclass clazz, jlong ptr, jint i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_rawset(L, i);
}
void lua_rawseti_(JNIEnv *env, jclass clazz, jlong ptr, jint i, jlong n){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_rawseti(L, i, n);
}
jint lua_gettable_(JNIEnv *env, jclass clazz, jlong ptr, jint i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_gettable(L, i);
}
jint lua_getfield_(JNIEnv *env, jclass clazz, jlong ptr, jint i, jstring k){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *chz = (env)->GetStringUTFChars(k, nullptr);
    int result = lua_getfield(L, i, chz);
    (env)->ReleaseStringUTFChars(k, chz);
    return result;
}
jint lua_rawget_(JNIEnv *env, jclass clazz, jlong ptr, jint i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_rawget(L, i);
}
jint lua_rawgeti_(JNIEnv *env, jclass clazz, jlong ptr, jint i, jlong n){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_rawgeti(L, i, n);
}

jint lua_setmetatable_(JNIEnv *env, jclass clazz, jlong ptr, jint i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_setmetatable(L, i);
}
jint lua_getmetatable_(JNIEnv *env, jclass clazz, jlong ptr, jint i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_getmetatable(L, i);
}
jint lua_next_(JNIEnv *env, jclass clazz, jlong ptr, jint i){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_next(L, i);
}
void lua_setglobal_(JNIEnv *env, jclass clazz, jlong ptr, jstring name){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *chz = (env)->GetStringUTFChars(name, nullptr);
    lua_setglobal(L, chz);
    (env)->ReleaseStringUTFChars(name, chz);
}
void lua_concat_(JNIEnv *env, jclass clazz, jlong ptr,jint n){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_concat(L, n);
}
jint lua_rawequal_(JNIEnv *env, jclass clazz, jlong ptr,jint i, jint j){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_rawequal(L, i, j);
}

//========================== function calls ==================
jint lua_pcall_(JNIEnv *env, jclass clazz, jlong ptr, jint nArgs, jint nResults, jint errFunc) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_pcall(L, nArgs, nResults, errFunc);
}
void
lua_call_(JNIEnv *env, jclass clazz, jlong ptr, jint nArgs, jint nResults) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_call(L, nArgs, nResults);
}

//============== running lua code ===========
jint luaL_loadfile_(JNIEnv *env, jclass clazz, jlong ptr, jstring filename){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *chz = (env)->GetStringUTFChars(filename, nullptr);
    int result = luaL_loadfile(L, chz);
    (env)->ReleaseStringUTFChars(filename, chz);
    return result;
}
jint luaL_loadstring_(JNIEnv *env, jclass clazz, jlong ptr, jstring code){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *chz = (env)->GetStringUTFChars(code, nullptr);
    int result = luaL_loadstring(L, chz);
    (env)->ReleaseStringUTFChars(code, chz);
    return result;
}
jint luaL_dofile_(JNIEnv *env, jclass clazz, jlong ptr, jstring filename){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *chz = (env)->GetStringUTFChars(filename, nullptr);
    int result = luaL_dofile(L, chz);
    (env)->ReleaseStringUTFChars(filename, chz);
    return result;
}
jint luaL_dostring_(JNIEnv *env, jclass clazz, jlong ptr, jstring str) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *utfStr = (env)->GetStringUTFChars(str, NULL);
    return (jint) luaL_dostring(L, utfStr);
}
jint lua_error_(JNIEnv *env, jclass clazz, jlong ptr){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_error(L);
}
void luaL_checkany_(JNIEnv *env, jclass clazz, jlong ptr, jint n){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    luaL_checkany(L, n);
}
jlong luaL_checkinteger_(JNIEnv *env, jclass clazz, jlong ptr, jint n){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return luaL_checkinteger(L, n);
}
jdouble luaL_checknumber_(JNIEnv *env, jclass clazz, jlong ptr, jint n){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return luaL_checknumber(L, n);
}
jstring luaL_checkstring_(JNIEnv *env, jclass clazz, jlong ptr, jint n){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *result = luaL_checkstring(L, n);
    return env->NewStringUTF(result);
}
void luaL_checktype_(JNIEnv *env, jclass clazz, jlong ptr, jint n, jint tp){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    luaL_checktype(L, n, tp);
}
//------------------------- env --------------------------

void pushJNIEnv(JNIEnv *env, lua_State *L) {
    //t
    JNIEnv **udEnv;

    lua_pushstring(L, LUAJAVAJNIENVTAG);
    lua_rawget(L, LUA_REGISTRYINDEX); // ele = stack[i],  push ele.

    if (!lua_isnil(L, -1)) {
        udEnv = (JNIEnv **) lua_touserdata(L, -1);
        *udEnv = env;
        lua_pop(L, 1);
    } else {
        lua_pop(L, 1);
        udEnv = (JNIEnv **) lua_newuserdata(L, sizeof(JNIEnv *));
        *udEnv = env;  // { t, userdara }

        lua_pushstring(L, LUAJAVAJNIENVTAG); // { t, userdara, tag }
        lua_insert(L, -2);                   // { t, tag, userdara }
        lua_rawset(L, LUA_REGISTRYINDEX);  // t[tag]= userdata.  {t}
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
        {"_evaluateScript", "(J" SIG_JSTRING ")I",            (void *) luaL_dostring_},
        {"_getGlobal",      "(J" SIG_JSTRING ")I",            (void *) lua_getglobal_},
        {"_pushString",     "(J" SIG_JSTRING ")" SIG_JSTRING, (void *) lua_pushString_},
        {"_pushNumber",     "(JD)V",                          (void *) lua_pushNumber_},
        {"_toString",       "(JI)" SIG_JSTRING,               (void *) lua_tostring_},
        {"_pcall",          "(JIII)I",                        (void *) lua_pcall_},
        {"_call",           "(JII)V",                         (void *) lua_call_},
};

Registration getLuaStateRegistration() {
    return createRegistration(CN_LUA_STATE, lua_state_methods, 7);
}
