//
// Created by Administrator on 2020/1/28 0028.
//

#include "lua_bridge.h"

Lua2JavaValue_Creator _lua2java_creator;
Java_Object_Releaser _java_obj_releaser;

void setLua2JavaValue_Creator(Lua2JavaValue_Creator creator){
    _lua2java_creator = creator;
}
void setJava_Object_Releaser(Java_Object_Releaser releaser){
    _java_obj_releaser = releaser;
}

void* newLua2JavaValue(int type, long long ptrOrIndex){
    return _lua2java_creator(type, ptrOrIndex);
}

void releaseJavaObject(void * obj){
    _java_obj_releaser(obj);
}