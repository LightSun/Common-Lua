//
// Created by Administrator on 2020/2/1 0001.
//

#ifndef COMMONLUAAPP_LUAFUNC_H
#define COMMONLUAAPP_LUAFUNC_H

#include <jni.h>
#include "java_env.h"
#include "../lua/lua.hpp"
#include "../luaextra/lua_bridge.h"

extern int executeLuaFunction(jobject obj, lua_State* L);

void pushFunc(JNIEnv *env, jclass clazz, jlong ptr, jobject func, jstring classname,
              jstring globalKey, jboolean toStack);

void setCollectionTypeAsMeta_(JNIEnv *env, jclass clazz, jlong ptr, jint idx, jint type);
jint getCollectionType_(JNIEnv *env, jclass clazz, jlong ptr, jint idx);

#endif //COMMONLUAAPP_LUAFUNC_H
