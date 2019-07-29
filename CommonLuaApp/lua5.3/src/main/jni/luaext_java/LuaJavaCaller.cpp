//
// Created by Administrator on 2019/7/29.
//
#include "java_env.h"
#include "../luaextra/LuaRegistry.h"
#include "../luaextra/lua_extra.h"
#include "sstream"

// 适配java 8大基本类型和自定义对象？
class LuaJavaCaller: public LuaBridgeCaller{
public:
    LuaJavaCaller(const char *classname, LuaMediator &holder){
        JNIEnv *const env = getJNIEnv();
        jclass const jclazz = env->FindClass(classname);
        if(jclazz == nullptr){
            std::stringstream out;
            out << "can't find class, className = " << classname << " !";
            const char *const msg = out.str().c_str();
            ext_println(msg);
            luaError(msg);
        } else{
            //todo
            env->DeleteLocalRef(jclazz);
        }
    }
    const void* call(const char* mName,  LuaMediator& holder){
        if(holder.count <= 0){
            lua_Number * a = new lua_Number();
            *a = 10086;
            holder.resultType = LUA_TNUMBER;
            return a;
        } else{
            LuaParam* param = &holder.lp[0];
            holder.resultType = param->type;
            return param->value;
        }
    }
};
