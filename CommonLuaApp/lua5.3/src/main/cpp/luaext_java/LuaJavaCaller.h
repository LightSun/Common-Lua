//
// Created by Administrator on 2019/7/29.
//

#ifndef COMMONLUAAPP_LUAJAVACALLER_H
#define COMMONLUAAPP_LUAJAVACALLER_H


void initLuaJavaCaller();
void deInitLuaJavaCaller();

void* newLua2JavaValue(int type, long long ptr);

int getType_Lua2Java(jobject obj);
jlong getValuePtr_Lua2Java(jobject obj);

#endif //COMMONLUAAPP_LUAJAVACALLER_H
