//
// Created by Administrator on 2020/2/1 0001.
//

#ifndef COMMONLUAAPP_LUAFUNC_H
#define COMMONLUAAPP_LUAFUNC_H

#include <jni.h>
#include "java_env.h"
#include "../lua/lua.hpp"

void pushFunc(JNIEnv *env, jclass clazz, jlong ptr, jobject func, jstring classname,
              jstring globalKey, jboolean toStack);

extern int executeLuaFunction(jobject obj, lua_State* L);

#endif //COMMONLUAAPP_LUAFUNC_H
