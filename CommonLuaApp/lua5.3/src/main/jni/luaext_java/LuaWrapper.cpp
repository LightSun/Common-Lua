//
// Created by Administrator on 2019/7/1.
//

#include <jni.h>
#include <string>

// c++ 导入c的最好都加上 extern
extern  "C" {
    #include "../luaextra/lua_extra.h"
}
#include "java_env.h"
#include "LuaWrapper.h"

#define SEARCH_METHOD "searchModule"
#define SEARCH_METHOD_SIG "(Ljava/lang/String;)Ljava/lang/String;"

WeakObjectM weakM;
jmethodID mid_search;

extern "C" char* search(const char* moduleName){
    JNIEnv * pEnv = getJNIEnv();
    if(pEnv == nullptr){
        pEnv = attachJNIEnv();
    }
    jstring name = pEnv->NewStringUTF(moduleName);
    jobject obj = weakM.getRefObject();
    jstring result = static_cast<jstring>(pEnv->CallObjectMethod(obj, mid_search, name));
    pEnv->DeleteLocalRef(obj);
    if(result == nullptr){
        return nullptr;
    }
    return const_cast<char *>(pEnv->GetStringUTFChars(result, nullptr));
}

extern "C" JNIEXPORT
void JNICALL Java_com_heaven7_java_lua_LuaWrapper_nNativeInit(
        JNIEnv *env,jobject obj){
    weakM.setRefObject(obj);

    jclass clazz = env->GetObjectClass(obj);
    mid_search = env->GetMethodID(clazz, SEARCH_METHOD, SEARCH_METHOD_SIG );
    env->DeleteLocalRef(clazz);

    //undefined reference to 'setLuaSearcher(char* (*)(char const*))'
    ext_setLuaSearcher(search);
}