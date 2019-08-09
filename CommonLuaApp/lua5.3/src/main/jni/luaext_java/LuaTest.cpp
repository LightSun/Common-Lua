//
// Created by Administrator on 2019/7/23.
//

#include "jni.h"
#include "../lua_test/tests_all.h"

extern "C" JNIEXPORT
void JNICALL Java_com_heaven7_java_lua_LuaTest_nTestBindCpp1(JNIEnv *env, jclass clazz, long ptr, jstring script){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    char * content = const_cast<char *>(env->GetStringUTFChars(script, nullptr));
    call_testLua(L, content);
}

extern "C" JNIEXPORT
void JNICALL Java_com_heaven7_java_lua_LuaTest_nTestLuaRegistry(JNIEnv *env, jclass clazz, long ptr, jstring script){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    char * content = const_cast<char *>(env->GetStringUTFChars(script, nullptr));
    call_testLuaRegistry(L, content);
}

extern "C" JNIEXPORT
void JNICALL Java_com_heaven7_java_lua_LuaTest_nTestLuaRegistryWrapper(JNIEnv *env, jclass clazz, long ptr, jstring script){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    char * content = const_cast<char *>(env->GetStringUTFChars(script, nullptr));
    call_testLuaRegistryWrapper(L, content);
}

extern "C" JNIEXPORT
void JNICALL Java_com_heaven7_java_lua_LuaTest_nTestAccessCppObjectInLua(JNIEnv *env, jclass clazz, long ptr, jstring script){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    char * content = const_cast<char *>(env->GetStringUTFChars(script, nullptr));
    testAccessCppObjInLua(L, content);
}
