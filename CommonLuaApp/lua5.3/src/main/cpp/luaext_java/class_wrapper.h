//
// Created by Administrator on 2020/2/5 0005.
//

#ifndef COMMONLUAAPP_CLASS_WRAPPER_H
#define COMMONLUAAPP_CLASS_WRAPPER_H

#include "../lua/lua.hpp"
#include <jni.h>

//return the type of global key
void lua_wrapClass(lua_State *L, const char *classname, const char *globalKey,
                    int toStack);
jboolean isJavaClass_(JNIEnv *env, jclass clazz, jlong ptr, jint idx);

extern int call_getStaticField(lua_State* L, const char* classname, const char* name);

extern int call_getStaticClass(lua_State* L, const char* classname, const char* name);

extern int call_getStaticMethod(lua_State* L, const char* classname, const char* name, int pCount);

#endif //COMMONLUAAPP_CLASS_WRAPPER_H
