//
// Created by Administrator on 2019/7/2.
//

#ifndef COMMONLUAAPP_LUASTATE_H
#define COMMONLUAAPP_LUASTATE_H

#include "jni.h"
#include "java_env.h"
extern "C" {
#include "../luaextra/lua_extra.h"

void pushJNIEnv(JNIEnv *env, lua_State *L);

JNIEnv *getEnvFromState(lua_State *L);
}

#endif //COMMONLUAAPP_LUASTATE_H
