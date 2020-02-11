//
// Created by Administrator on 2019/7/1.
//

#include "jni.h"
#include "java_env.h"
#include "LuaState.h"
#include "LuaJavaCaller.h"
#include "LuaFunc.h"
#include "class_wrapper.h"

extern "C" {
#include "../luaextra/lua_extra.h"
}

#define CN_LUA_STATE  "com/heaven7/java/lua/LuaState"
/* Constant that is used to index the JNI Environment */
#define LUAJAVAJNIENVTAG      "__JNIEnv"

extern "C" {
jlong nCreate_(JNIEnv *env, jclass clazz) {
    lua_State *state = ext_newLuaState();
    return reinterpret_cast<jlong>(state);
}

void nRelease_(JNIEnv *env, jclass clazz, jlong ptr) {
    lua_State *state = reinterpret_cast<lua_State *>(ptr);
    ext_closeLuaState(state);
}

void lua_pushString_(JNIEnv *env, jclass clazz, jlong ptr, jstring str) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);

    const char *uniStr = (env)->GetStringUTFChars(str, NULL);
    auto result = lua_pushstring(L, uniStr);

    (env)->ReleaseStringUTFChars(str, uniStr);
    //return env->NewStringUTF(result);
}
void lua_pushNumber_(JNIEnv *env, jclass clazz, jlong ptr, jdouble val) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_pushnumber(L, (lua_Number) val);
}
void lua_pushBoolean_(JNIEnv *env, jclass clazz, jlong ptr, jboolean val) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_pushboolean(L, val ? 1 : 0);
}
void lua_pushnil_(JNIEnv *env, jclass clazz, jlong ptr) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_pushnil(L);
    //lua_pushcfunction()
}
void lua_pushinteger_(JNIEnv *env, jclass clazz, jlong ptr, jlong val) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_pushinteger(L, val);
}
void pushJavaObject(JNIEnv *env, jclass clazz, jlong ptr, jobject obj, jstring classname,
                    jstring globalKey, jboolean toStack) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);

    auto lbc = newJavaLBC(obj, classname);
    if (globalKey == nullptr) {
        lua_wrapObject(L, lbc, nullptr, nullptr, toStack ? 1 : 0);
    } else {
        const char *key = env->GetStringUTFChars(globalKey, nullptr);
        lua_wrapObject(L, lbc, nullptr, key, toStack ? 1 : 0);
        env->ReleaseStringUTFChars(globalKey, key);
    }
}

// ----------------- global ------------
jboolean removeGlobal(JNIEnv *env, jclass clazz, jlong ptr, jstring jkey) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *key = env->GetStringUTFChars(jkey, nullptr);
    lua_getglobal(L, key);
    bool result = lua_type(L, -1) != LUA_TNIL;
    lua_pop(L, 1);

    lua_pushnil(L);
    lua_setglobal(L, key);
    env->ReleaseStringUTFChars(jkey, key);

    return static_cast<jboolean>(result);
}
jboolean hasGlobal(JNIEnv *env, jclass clazz, jlong ptr, jstring jkey) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *key = env->GetStringUTFChars(jkey, nullptr);
    lua_getglobal(L, key);
    bool result = lua_type(L, -1) != LUA_TNIL;
    lua_pop(L, 1);
    env->ReleaseStringUTFChars(jkey, key);
    return static_cast<jboolean>(result);
}
jint isNativeWrapper_(JNIEnv *env, jclass clazz, jlong ptr, int idx) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    idx = ext_adjustIdx(L, idx);
    jint result = 0;

    switch (lua_type(L, idx)) {
        case LUA_TFUNCTION: {
            lua_pushvalue(L, idx);
            lua_pushstring(L, LIB_LUA_WRAPPER);
            if (lua_pcall(L, 1, 1, 0)) {
                return 0;
            }
        }
            break;

        case LUA_TUSERDATA:
        case LUA_TTABLE:
            lua_pushstring(L, LIB_LUA_WRAPPER);
            lua_gettable(L, idx); // {value}
            //luaB_dumpStack(L);
            break;
        default:
            return 0;
    }
    if (lua_type(L, -1) == LUA_TBOOLEAN) {
        result = lua_toboolean(L, -1);
    }
    lua_pop(L, 1);
    return result;
}
jobject getJavaObject_(JNIEnv *env, jclass clazz, jlong ptr, int idx) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    idx = ext_adjustIdx(L, idx);
    lua_pushstring(L, NAME_GET_CPP);
    lua_gettable(L, idx);
    if (lua_type(L, -1) == LUA_TLIGHTUSERDATA) {
        jobject jobj = static_cast<jobject>(lua_touserdata(L, -1));
        lua_pop(L, 1);
        return jobj;
    }
    lua_pop(L, 1);
    return nullptr;
}
void travel_(JNIEnv *env, jclass clazz, jlong ptr, jint idx, jobject traveller) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    // 1, can't direct access jobject in func callback. or else may cause bug
    //    like 'JNI DETECTED ERROR IN APPLICATION: use of invalid jobject 0x7ff01a0e18'
    //2. here can't use global ref even if delete after call 'travelTable'. or else may cause bug
    //  'access deleted global reference.'
    auto obj = env->NewWeakGlobalRef(traveller);
    auto tt = [=](lua_State *L) {
        auto key = getLuaValue(L, -2);
        auto value = getLuaValue(L, -1);
        // can't direct access jobject in func callback. or else may cause bug
        // like 'JNI DETECTED ERROR IN APPLICATION: use of invalid jobject 0x7ff01a0e18'
        // return  travelImpl(L, traveller, key, value);
        auto pobj = env->NewLocalRef(obj);
        auto result = travelImpl(L, pobj, key, value);
        env->DeleteLocalRef(pobj);
        return result;
    };
    travelTable(L, idx, tt);
}
void wrapClass(JNIEnv *env, jclass clazz, jlong ptr, jstring cn, jstring gk, jboolean toStack) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    auto str_cn = env->GetStringUTFChars(cn, nullptr);
    auto str_gk = env->GetStringUTFChars(gk, nullptr);
    lua_wrapClass(L, str_cn, str_gk, toStack ? 1 : 0);

    env->ReleaseStringUTFChars(cn, str_cn);
    env->ReleaseStringUTFChars(gk, str_gk);
}

//------------------ stack ---------------------
jint lua_checkstack_(JNIEnv *env, jclass clazz, jlong ptr, int n) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_checkstack(L, n);
}
jint lua_gettop_(JNIEnv *env, jclass clazz, jlong ptr) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_gettop(L);
}
void lua_insert_(JNIEnv *env, jclass clazz, jlong ptr, jint index) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_insert(L, index);
}
void lua_pushvalue_(JNIEnv *env, jclass clazz, jlong ptr, int i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_pushvalue(L, i);
}
void lua_remove_(JNIEnv *env, jclass clazz, jlong ptr, int i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_remove(L, i);
}
void lua_replace_(JNIEnv *env, jclass clazz, jlong ptr, int i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_replace(L, i);
}
void lua_settop_(JNIEnv *env, jclass clazz, jlong ptr, int i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_settop(L, i);
}
void lua_pop_(JNIEnv *env, jclass clazz, jlong ptr, int n) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_pop(L, n);
}
//-------------------- read from stack -----------------

jstring lua_tostring_(JNIEnv *env, jclass clazz, jlong ptr, jint idx) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *str = lua_tostring(L, idx);
    return (env)->NewStringUTF(str);
}
jint lua_isboolean_(JNIEnv *env, jclass clazz, jlong ptr, int i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_isboolean(L, i);
}
jint lua_isfunction_(JNIEnv *env, jclass clazz, jlong ptr, int i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_isfunction(L, i);
}
jint lua_isnil_(JNIEnv *env, jclass clazz, jlong ptr, int i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_isnil(L, i);
}
jint lua_isnone_(JNIEnv *env, jclass clazz, jlong ptr, int i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_isnone(L, i);
}
jint lua_isnumber_(JNIEnv *env, jclass clazz, jlong ptr, int i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_isnumber(L, i);
}
jint lua_isstring_(JNIEnv *env, jclass clazz, jlong ptr, int i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_isstring(L, i);
}
jint lua_istable_(JNIEnv *env, jclass clazz, jlong ptr, int i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_istable(L, i);
}
jint lua_isuserdata_(JNIEnv *env, jclass clazz, jlong ptr, int i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_isuserdata(L, i);
}

jint lua_toboolean_(JNIEnv *env, jclass clazz, jlong ptr, int i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_toboolean(L, i);
}
jlong lua_tointeger_(JNIEnv *env, jclass clazz, jlong ptr, int i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_tointeger(L, i);
}
jdouble lua_tonumber_(JNIEnv *env, jclass clazz, jlong ptr, int i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_tonumber(L, i);
}

jobject getLuaValue_(JNIEnv *env, jclass clazz, jlong ptr, jint n) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    auto val = getLuaValue(L, n);
    return val != nullptr ? static_cast<jobject>(val) : nullptr;
}
jint lua_type_(JNIEnv *env, jclass clazz, jlong ptr, int i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_type(L, i);
}
jstring lua_typename_(JNIEnv *env, jclass clazz, jlong ptr, int type) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *tn = lua_typename(L, type);
    return (env)->NewStringUTF(tn);
}
jstring luaL_typename_(JNIEnv *env, jclass clazz, jlong ptr, int i) {
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
void lua_newtable_(JNIEnv *env, jclass clazz, jlong ptr) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_newtable(L);
}
void lua_createtable_(JNIEnv *env, jclass clazz, jlong ptr, jint narr, jint nhash) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_createtable(L, narr, nhash);
}
void lua_settable_(JNIEnv *env, jclass clazz, jlong ptr, jint i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_settable(L, i);
}
void lua_setfield_(JNIEnv *env, jclass clazz, jlong ptr, jint i, jstring k) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *chz = (env)->GetStringUTFChars(k, nullptr);
    lua_setfield(L, i, chz);
    (env)->ReleaseStringUTFChars(k, chz);
}
void lua_rawset_(JNIEnv *env, jclass clazz, jlong ptr, jint i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_rawset(L, i);
}
//long ptr, int index, long arrayIndex
void lua_rawseti_(JNIEnv *env, jclass clazz, jlong ptr, jint i, jlong n) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_rawseti(L, i, n);
}
jint lua_gettable_(JNIEnv *env, jclass clazz, jlong ptr, jint i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_gettable(L, i);
}
jint lua_getfield_(JNIEnv *env, jclass clazz, jlong ptr, jint i, jstring k) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *chz = (env)->GetStringUTFChars(k, nullptr);
    int result = lua_getfield(L, i, chz);
    (env)->ReleaseStringUTFChars(k, chz);
    return result;
}
jint lua_rawget_(JNIEnv *env, jclass clazz, jlong ptr, jint i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_rawget(L, i);
}
jint lua_rawgeti_(JNIEnv *env, jclass clazz, jlong ptr, jint i, jlong n) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_rawgeti(L, i, n);
}

jint lua_setmetatable_(JNIEnv *env, jclass clazz, jlong ptr, jint i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_setmetatable(L, i);
}
jint lua_getmetatable_(JNIEnv *env, jclass clazz, jlong ptr, jint i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_getmetatable(L, i);
}
jint lua_next_(JNIEnv *env, jclass clazz, jlong ptr, jint i) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return lua_next(L, i);
}
void lua_setglobal_(JNIEnv *env, jclass clazz, jlong ptr, jstring name) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *chz = (env)->GetStringUTFChars(name, nullptr);
    lua_setglobal(L, chz);
    (env)->ReleaseStringUTFChars(name, chz);
}
void lua_concat_(JNIEnv *env, jclass clazz, jlong ptr, jint n) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    lua_concat(L, n);
}
jint lua_rawequal_(JNIEnv *env, jclass clazz, jlong ptr, jint i, jint j) {
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
jint luaL_loadfile_(JNIEnv *env, jclass clazz, jlong ptr, jstring filename) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *chz = (env)->GetStringUTFChars(filename, nullptr);
    int result = luaL_loadfile(L, chz);
    (env)->ReleaseStringUTFChars(filename, chz);
    return result;
}
jint luaL_loadstring_(JNIEnv *env, jclass clazz, jlong ptr, jstring code) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *chz = (env)->GetStringUTFChars(code, nullptr);
    int result = luaL_loadstring(L, chz);
    (env)->ReleaseStringUTFChars(code, chz);
    return result;
}
jint luaL_dofile_(JNIEnv *env, jclass clazz, jlong ptr, jstring filename) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *chz = (env)->GetStringUTFChars(filename, nullptr);
    int result = luaL_dofile(L, chz);
    (env)->ReleaseStringUTFChars(filename, chz);
    return result;
}
jint luaL_dostring_(JNIEnv *env, jclass clazz, jlong ptr, jstring str) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *utfStr = (env)->GetStringUTFChars(str, NULL);
    auto result = (jint) luaL_dostring(L, utfStr);
    (env)->ReleaseStringUTFChars(str, utfStr);
    return result;
}
void luaL_error_(JNIEnv *env, jclass clazz, jlong ptr, jstring msg) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    auto str = env->GetStringUTFChars(msg, nullptr);
    luaL_error(L, str);
    env->ReleaseStringUTFChars(msg, str);
}
void luaL_checkany_(JNIEnv *env, jclass clazz, jlong ptr, jint n) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    luaL_checkany(L, n);
}
jlong luaL_checkinteger_(JNIEnv *env, jclass clazz, jlong ptr, jint n) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return luaL_checkinteger(L, n);
}
jdouble luaL_checknumber_(JNIEnv *env, jclass clazz, jlong ptr, jint n) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    return luaL_checknumber(L, n);
}
jstring luaL_checkstring_(JNIEnv *env, jclass clazz, jlong ptr, jint n) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    const char *result = luaL_checkstring(L, n);
    return env->NewStringUTF(result);
}
void luaL_checktype_(JNIEnv *env, jclass clazz, jlong ptr, jint n, jint tp) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    luaL_checktype(L, n, tp);
}
void dumpLuaStack_(JNIEnv *env, jclass clazz, jlong ptr) {
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    luaB_dumpStack(L);
}
//------------------------- extra ------------------------

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

static JNINativeMethod lua_state_methods[] = {
        {"_nCreate",                 "()J",                                         (void *) nCreate_},
        {"_nRelease",                "(J)V",                                        (void *) nRelease_},
        {"_setGlobal",               "(J" SIG_JSTRING ")V",                         (void *) lua_setglobal_},
        {"_doFile",                  "(J" SIG_JSTRING ")I",                         (void *) luaL_dofile_},
        {"_loadFile",                "(J" SIG_JSTRING ")I",                         (void *) luaL_loadfile_},
        {"_evaluateScript",          "(J" SIG_JSTRING ")I",                         (void *) luaL_dostring_},
        {"_loadScript",              "(J" SIG_JSTRING ")I",                         (void *) luaL_loadstring_},
        {"_getGlobal",               "(J" SIG_JSTRING ")I",                         (void *) lua_getglobal_},
        {"_getTable",                "(JI)I",                                       (void *) lua_gettable_},
        {"_setTable",                "(JI)V",                                       (void *) lua_settable_},
        {"_getType",                 "(JI)I",                                       (void *) lua_type_},
        {"_getLuaValue",             "(JI)" SIG_OBJECT,                             (void *) getLuaValue_},
        {"_removeGlobal",            "(J" SIG_JSTRING ")Z",                         (void *) removeGlobal},
        {"_hasGlobal",               "(J" SIG_JSTRING ")Z",                         (void *) hasGlobal},

        {"_pushValue",               "(JI)V",                                       (void *) lua_pushvalue_},
        {"_pushString",              "(J" SIG_JSTRING ")V",                         (void *) lua_pushString_},
        {"_pushLong",                "(JJ)V",                                       (void *) lua_pushinteger_},
        {"_pushNumber",              "(JD)V",                                       (void *) lua_pushNumber_},
        {"_pushnil",                 "(J)V",                                        (void *) lua_pushnil_},
        {"_pushBoolean",             "(JZ)V",                                       (void *) lua_pushBoolean_},
        {"_pushJavaObject",          "(J" SIG_OBJECT SIG_JSTRING SIG_JSTRING "Z)V", (void *) pushJavaObject},
        {"_pushFunction",            "(J" SIG_OBJECT SIG_JSTRING SIG_JSTRING "Z)V", (void *) pushFunc},
        {"_pushClass",               "(J" SIG_JSTRING SIG_JSTRING "Z)V",            (void *) wrapClass},

        {"_toString",                "(JI)" SIG_JSTRING,                            (void *) lua_tostring_},
        {"_toNumber",                "(JI)D",                                       (void *) lua_tonumber_},
        {"_toLong",                  "(JI)J",                                       (void *) lua_tointeger_},
        {"_toBoolean",               "(JI)I",                                       (void *) lua_toboolean_},
        {"_pcall",                   "(JIII)I",                                     (void *) lua_pcall_},
        {"_call",                    "(JII)V",                                      (void *) lua_call_},
        {"_getTop",                  "(J)I",                                        (void *) lua_gettop_},

        {"_newTable",                "(J)V",                                        (void *) lua_newtable_},
        {"_rawseti",                 "(JIJ)V",                                      (void *) lua_rawseti_},
        {"_rawset",                  "(JI)V",                                       (void *) lua_rawset_},
        {"_rawGet",                  "(JI)I",                                       (void *) lua_rawget_},
        {"_rawGeti",                 "(JIJ)I",                                      (void *) lua_rawgeti_},
        {"_setField",                "(JI" SIG_JSTRING ")V",                        (void *) lua_setfield_},
        {"_getField",                "(JI" SIG_JSTRING ")I",                        (void *) lua_getfield_},

        {"_dumpLuaStack",            "(J)V",                                        (void *) dumpLuaStack_},
        {"_pop",                     "(JI)V",                                       (void *) lua_pop_},
        {"_setTop",                  "(JI)V",                                       (void *) lua_settop_},
        {"_isNativeWrapper",         "(JI)I",                                       (void *) isNativeWrapper_},
        {"_getJavaObject",           "(JI)" SIG_OBJECT,                             (void *) getJavaObject_},
        {"_travel",                  "(JI" SIG_OBJECT ")V",                         (void *) travel_},
        {"_error",                   "(J" SIG_JSTRING ")V",                         (void *) luaL_error_},
        {"_setCollectionTypeAsMeta", "(JII)V",                                      (void *) setCollectionTypeAsMeta_},
        {"_getCollectionType",       "(JI)I",                                       (void *) getCollectionType_},
        {"_isJavaClass",             "(JI)Z",                                       (void *) isJavaClass_},
};

Registration getLuaStateRegistration() {
    return createRegistration(const_cast<char *>(CN_LUA_STATE), lua_state_methods,
                              sizeof(lua_state_methods) / sizeof(lua_state_methods[0]));
}
