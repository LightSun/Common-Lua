//
// Created by Administrator on 2019/7/23.
//

#include "jni.h"
#include "../luaextra/test.h"

extern "C" JNIEXPORT
void JNICALL Java_com_heaven7_java_lua_LuaTest_nTestBindCpp1(JNIEnv *env, jclass clazz, long ptr, jstring script){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    char * content = const_cast<char *>(env->GetStringUTFChars(script, nullptr));
    call_testLua(L, content);
}
