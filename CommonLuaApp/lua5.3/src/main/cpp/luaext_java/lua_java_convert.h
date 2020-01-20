//
// Created by Administrator on 2020/1/20 0020.
//

#ifndef COMMONLUAAPP_LUA_JAVA_CONVERT_H
#define COMMONLUAAPP_LUA_JAVA_CONVERT_H

#include <jni.h>
#include "../luaextra/LuaRegistry.h"

void luaTable2java();
void javaArray2lua();
void javaList2lua();
void javaMap2lua();

#endif //COMMONLUAAPP_LUA_JAVA_CONVERT_H
