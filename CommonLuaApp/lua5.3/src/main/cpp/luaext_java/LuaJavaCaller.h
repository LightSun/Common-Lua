//
// Created by Administrator on 2019/7/29.
//

#ifndef COMMONLUAAPP_LUAJAVACALLER_H
#define COMMONLUAAPP_LUAJAVACALLER_H

#include "../luaextra/LuaRegistry.h"

void initLuaJavaCaller();
void deInitLuaJavaCaller();

LuaBridgeCaller* newJavaLBC(jobject jobj, jstring classname);

int getType_Lua2Java(jobject obj);
jlong getValuePtr_Lua2Java(jobject obj);

int travelImpl(lua_State* L, jobject obj, void* key, void* value);

#endif //COMMONLUAAPP_LUAJAVACALLER_H
