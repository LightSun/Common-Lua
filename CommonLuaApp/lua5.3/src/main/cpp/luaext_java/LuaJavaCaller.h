//
// Created by Administrator on 2019/7/29.
//

#ifndef COMMONLUAAPP_LUAJAVACALLER_H
#define COMMONLUAAPP_LUAJAVACALLER_H

#include "../luaextra/LuaRegistry.h"


void initLuaJavaCaller();
void deInitLuaJavaCaller();

LuaBridgeCaller *newJavaLBC(jobject jobj, jstring classname);

int getType_Lua2Java(jobject obj);
jlong getValuePtr_Lua2Java(jobject obj);

int travelImpl(lua_State *L, jobject obj, void *key, void *value);

int call_getStaticField(lua_State *L, const char *classname, const char *name);

int call_getStaticClass(lua_State *L, const char *classname, const char *name);

int call_getStaticMethod(lua_State *L, const char *classname, const char *name, int pCount);


#endif //COMMONLUAAPP_LUAJAVACALLER_H
