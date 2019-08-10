//
// Created by Administrator on 2019/7/23.
//

#include "test.h"
extern "C"{
#include "../luaextra/lua_extra.h"
}

#include "../luaextra/LuaRegistry.h"

class Foo{

public:

    Foo(int value){
        _value = value;
      //  ext_getLuaPrint()("Foo Constructor !", 0, 1);
    }

    ~Foo(){
       // ext_getLuaPrint()("Foo Destructor !", 0, 1);
    }

    int add(int a, int b){
        return a + b;
    }

    void setV(int value){
        _value = value;
    }

    int getV(){
        return _value;
    }
    int _value;
};

class FooWrapper : public Foo
{
public:
    static const char className[];
    static const LuaPort<FooWrapper>::RegType Register[];

    FooWrapper(lua_State *L): Foo(luaL_checknumber(L, -1)){
        ext_getLuaPrint()("FooWrapper Constructor !", 0, 1);
    }
    ~FooWrapper(){
        ext_getLuaPrint()("FooWrapper Destructor !", 0, 1);
    }
    const int add(lua_State* L){
        int a = luaL_checknumber(L, -1);
        int b = luaL_checknumber(L, -2);
        int res = Foo::add(a, b);
        lua_pushnumber(L, res);
        return 1; //push的个数
    }

    const int setV(lua_State* L){
        int v = luaL_checknumber(L, -1);
        Foo::setV(v);
        return 0;
    }

    const int getV(lua_State* L){
        lua_pushnumber(L, Foo::getV());
        return 1;
    }
};

const char FooWrapper::className[] = "FooWrapper";
const LuaPort<FooWrapper>::RegType FooWrapper::Register[] = {
        {"add", &FooWrapper::add},
        {"setV", &FooWrapper::setV},
        {"getV", &FooWrapper::getV},
        {nullptr, nullptr}
};


void call_testLua(lua_State* L, char* luacontent){
    LuaPort<FooWrapper>::Register(L); // ok

    int code = luaL_dostring(L, luacontent);
    const char* msg = lua_tostring(L, -1);
    ext_getLuaPrint()(const_cast<char *>(msg), 50, 1);

    char a[50];
    sprintf(a, "call_testLua(). result code is %d", code);
    ext_getLuaPrint()(a, 50, 1);
}

class LuaBridgeCallerImpl : public LuaBridgeCaller{
public:
    LuaBridgeCallerImpl(const char *classname, LuaMediator *holder){
    }
    LuaBridgeCallerImpl(){}
    void *call(const char *cn, const char *mName ,LuaMediator * holder) {
        if(holder->count <= 0){
            lua_Number * a = new lua_Number();
            *a = 10086;
            holder->resultType = LUA_TNUMBER;
            return a;
        } else{
            LuaParam* param = &holder->lp[0];
            holder->resultType = param->type;
            return param->value;
        }
    }
};


extern void initLuaBridge(lua_State* L);

void call_testLuaRegistry(lua_State* L, char* luacontent){
    //c++ 11支持函数表达式
    auto creator = [](const char *classname, LuaMediator *holder){
        return (LuaBridgeCaller*)new LuaBridgeCallerImpl(classname, holder);
    };
    setLuaBridgeCallerCreator(creator);
    initLuaBridge(L);

    int code = luaL_dostring(L, luacontent);
    const char* msg = lua_tostring(L, -1);
    ext_getLuaPrint()(const_cast<char *>(msg), 50, 1);

    char a[50];
    sprintf(a, "call_testLuaRegistry(). result code is %d", code);
    ext_getLuaPrint()(a, 50, 1);
}
void call_testLuaRegistryWrapper(lua_State* L, char* luacontent){
    //c++ 11支持函数表达式
    auto creator = [](const char *classname, LuaMediator *holder){
        return (LuaBridgeCaller*)new LuaBridgeCallerImpl(classname, holder);
    };
    setLuaBridgeCallerCreator(creator);
    //initLuaBridge(L);
   //LuaRegistryWrapper<LuaBridge>::Register(L, new LuaBridge(new LuaBridgeCallerImpl(), "test.LuaBridgeCallerImpl"));

    int code = luaL_dostring(L, luacontent);
    const char* msg = lua_tostring(L, -1);
    ext_getLuaPrint()(const_cast<char *>(msg), 50, 1);

    char a[50];
    sprintf(a, "call_testLuaRegistry(). result code is %d", code);
    ext_getLuaPrint()(a, 50, 1);
}

/*一个lua文件, test.lua, 想用如下方式访问, 问题: 如何实现?

ff = Foo(3)
v = ff:add(1, 4)        // v = 5
ff:foo()
ff:setV(6)
ff2 = Foo(4)
print(ff:getV())       // v = 6
print(ff2:getV())     // v = 4*/


