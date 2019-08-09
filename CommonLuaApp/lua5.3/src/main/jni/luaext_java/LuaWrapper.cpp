//
// Created by Administrator on 2019/7/1.
//

#include <jni.h>
#include <string>

// import c.h for cpp often need { extern "C"}
extern  "C" {
    #include "../luaextra/lua_extra.h"
}
#include "java_env.h"
#include "LuaWrapper.h"
// just for test

#define SEARCH_LUA_METHOD "searchLuaModule"
#define SEARCH_C_METHOD "searchCModule"
#define PRINT_METHOD "print"
#define PRINT_METHOD_SIG "(Ljava/lang/String;Z)V"
#define SEARCH_METHOD_SIG "(Ljava/lang/String;)Ljava/lang/String;"

WeakObjectM weakM;
jmethodID mid_lua_search;
jmethodID mid_c_search;
jmethodID mid_print;

extern "C" char* search(const char* moduleName, jmethodID mid){
    JNIEnv * pEnv = getJNIEnv();
    if(pEnv == nullptr){
        pEnv = attachJNIEnv();
    }
    jstring name = pEnv->NewStringUTF(moduleName);
    if(name == nullptr){
        return nullptr;
    }
    jobject obj = weakM.getRefObject();
    jstring result = static_cast<jstring>(pEnv->CallObjectMethod(obj, mid, name));
    pEnv->DeleteLocalRef(name);
    pEnv->DeleteLocalRef(obj);
    if(result == nullptr){
        return nullptr;
    }
    char * str = const_cast<char *>(pEnv->GetStringUTFChars(result, nullptr));
    pEnv->DeleteLocalRef(result);
    return str;
}

extern "C" char* searchLua(const char* moduleName){
    return search(moduleName, mid_lua_search);
}

extern "C" char* searchC(const char* moduleName){
    return search(moduleName, mid_c_search);
}

extern "C" void Lua_printImpl(char* cs, int len, int flag){
    JNIEnv * pEnv = getJNIEnv();
    if(pEnv == nullptr){
        pEnv = attachJNIEnv();
    }
    jobject obj = weakM.getRefObject();
    jstring str = pEnv->NewStringUTF(cs);
    jboolean concat = static_cast<jboolean>(flag != 1); // 1 means end
    pEnv->CallVoidMethod(obj, mid_print, str, concat);

    // recycle
    pEnv->DeleteLocalRef(obj);
    pEnv->DeleteLocalRef(str);
}

extern "C" JNIEXPORT
void JNICALL Java_com_heaven7_java_lua_LuaWrapper_nNativeInit(
        JNIEnv *env,jobject obj){
    weakM.setRefObject(obj);

    jclass clazz = env->GetObjectClass(obj);
    mid_lua_search = env->GetMethodID(clazz, SEARCH_LUA_METHOD, SEARCH_METHOD_SIG);
    mid_c_search = env->GetMethodID(clazz, SEARCH_C_METHOD, SEARCH_METHOD_SIG);
    mid_print = env->GetMethodID(clazz, PRINT_METHOD, PRINT_METHOD_SIG);
    env->DeleteLocalRef(clazz);

    //undefined reference to 'setLuaSearcher(char* (*)(char const*))'
    ext_setLuaSearcher(searchLua);
    ext_setClibSearcher(searchC);
    ext_setLua_print(Lua_printImpl);
}