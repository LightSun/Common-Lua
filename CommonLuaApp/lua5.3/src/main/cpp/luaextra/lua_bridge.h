//
// Created by Administrator on 2019/7/30.
//

#ifndef COMMONLUAAPP_LUA_BRIDGE_H
#define COMMONLUAAPP_LUA_BRIDGE_H

#define DTYPE_NULL 1
#define DTYPE_NUMBER 2
#define DTYPE_STRING 3
#define DTYPE_BOOLEAN 4
#define DTYPE_TABLE 5
#define DTYPE_OBJECT 11     //unknown-cpp object. often from user-data.
#define DTYPE_LB_OBJECT 6  //luabridge object. which create by lua
#define DTYPE_LBD_OBJECT 7 //luabridge dynamic. which create by native

#define DTYPE_SET 8
#define DTYPE_LIST 9
#define DTYPE_MAP 10
#define DTYPE_LUA2JAVA_VALUE 12

/**
 * create lua2java value object
 * @param type the value type
 * @param ptrOrIndex the pointer or stack index
 * @return  the java object
 */
typedef void* (*Lua2JavaValue_Creator)(int type, long long ptrOrIndex);
typedef void (*Java_Object_Releaser)(void *obj);

void setLua2JavaValue_Creator(Lua2JavaValue_Creator creator);
void setJava_Object_Releaser(Java_Object_Releaser releaser);

void* newLua2JavaValue(int type, long long ptrOrIndex);
void releaseJavaObject(void * obj);

#endif //COMMONLUAAPP_LUA_BRIDGE_H
