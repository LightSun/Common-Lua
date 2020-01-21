//
// Created by Administrator on 2019/7/30.
//

#ifndef COMMONLUAAPP_LUA_BRIDGE_H
#define COMMONLUAAPP_LUA_BRIDGE_H

#define DTYPE_NULL 1
#define DTYPE_NUMBER 2
#define DTYPE_STRING 3
#define DTYPE_BOOLEAN 4
#define DTYPE_OBJECT 5     //unknown-cpp object. often from user-data.
#define DTYPE_LB_OBJECT 6  //luabridge object. which create by lua
#define DTYPE_LBD_OBJECT 7 //luabridge dynamic. which create by native

#define DTYPE_SET 8
#define DTYPE_LIST 9
#define DTYPE_MAP 10
#define DTYPE_TABLE 11


#endif //COMMONLUAAPP_LUA_BRIDGE_H
