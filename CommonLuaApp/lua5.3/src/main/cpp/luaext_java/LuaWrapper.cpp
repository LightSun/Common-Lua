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
#define CREATE_TEMP_FILE_METHOD "createTempFile"

#define PRINT_METHOD_SIG "(Ljava/lang/String;Z)V"
#define SEARCH_METHOD_SIG "(Ljava/lang/String;)Ljava/lang/String;"
#define CREATE_TEMP_FILE_SIG "(Ljava/lang/String;)Ljava/lang/String;"

WeakObjectM weakM;
jmethodID mid_lua_search;
jmethodID mid_c_search;
jmethodID mid_print;
jmethodID mid_createTempFile;

jstring _tojString(JNIEnv* env,const char *input) {
    std::string in = std::string(input); // has a bit of a code smell, there is probably a better way.
    // cite: http://stackoverflow.com/questions/27303316/c-stdstring-to-jstring-with-a-fixed-length
    jbyteArray array = env->NewByteArray(in.length());
    env->SetByteArrayRegion(array, 0, in.length(),(jbyte*)in.c_str());

    // cite: http://discuss.cocos2d-x.org/t/jni-return-string/9982/3
    jclass class_str = env->FindClass((const char*)"java/lang/String");
    jmethodID mid = env->GetMethodID(class_str, "<init>", "([BLjava/lang/String;)V");
    jstring code = env->NewStringUTF("utf-8");
    jstring str = (jstring)env->NewObject(class_str, mid, array, code);
    env->DeleteLocalRef(array);
    env->DeleteLocalRef(class_str);
    env->DeleteLocalRef(code);
    return str;
}

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
    //may have bug. because c or c++ char may not be utf-8.
    //jstring str = pEnv->NewStringUTF(fn);
    jstring str = _tojString(pEnv, cs);
    jboolean concat = static_cast<jboolean>(flag != 1); // 1 means end
    pEnv->CallVoidMethod(obj, mid_print, str, concat);

    // recycle
    pEnv->DeleteLocalRef(obj);
    pEnv->DeleteLocalRef(str);
}

extern "C" char* createTempFileImpl(const char* fn){
    JNIEnv * pEnv = getJNIEnv();
    if(pEnv == nullptr){
        pEnv = attachJNIEnv();
    }
    jobject obj = weakM.getRefObject();
    //may have bug. because c or c++ char may not be utf-8.
    //jstring str = pEnv->NewStringUTF(fn);
    jstring str = _tojString(pEnv, fn);

    jstring result = static_cast<jstring>(pEnv->CallObjectMethod(obj, mid_createTempFile, str));
    const char * act = pEnv->GetStringUTFChars(result, nullptr);
    // recycle
    pEnv->DeleteLocalRef(obj);
    pEnv->DeleteLocalRef(str);
    pEnv->DeleteLocalRef(result);
    return (char *)act;
}

extern "C" JNIEXPORT
void JNICALL Java_com_heaven7_java_lua_LuaWrapper_nNativeInit(
        JNIEnv *env,jobject obj){
    weakM.setRefObject(obj);

    jclass clazz = env->GetObjectClass(obj);
    mid_lua_search = env->GetMethodID(clazz, SEARCH_LUA_METHOD, SEARCH_METHOD_SIG);
    mid_c_search = env->GetMethodID(clazz, SEARCH_C_METHOD, SEARCH_METHOD_SIG);
    mid_print = env->GetMethodID(clazz, PRINT_METHOD, PRINT_METHOD_SIG);
    mid_createTempFile = env->GetMethodID(clazz, CREATE_TEMP_FILE_METHOD, CREATE_TEMP_FILE_SIG);
    env->DeleteLocalRef(clazz);

    //undefined reference to 'setLuaSearcher(char* (*)(char const*))'
    ext_setLuaSearcher(searchLua);
    ext_setClibSearcher(searchC);
    ext_setLua_print(Lua_printImpl);
    ext_setCreateTempFile(createTempFileImpl);
}