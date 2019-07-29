//
// Created by Administrator on 2019/7/29.
//
#include "java_env.h"
#include "../luaextra/LuaRegistry.h"
#include "../luaextra/lua_extra.h"
#include "sstream"

#define STRING_NAME "Ljava/lang/String;"
#define OBJECT_NAME "Ljava/lang/Object;"
#define CALLER_CLASS "com/heaven7/java/lua/LuaJavaCaller"
#define MNAME_CREATE "create"
#define MNAME_INVOKE "invoke"

#define SIG_CREATE "("##STRING_NAME##STRING_NAME##"["##OBJECT_NAME##"["##STRING_NAME##")"##OBJECT_NAME
#define SIG_INVOKE "("##OBJECT_NAME##STRING_NAME##STRING_NAME##"["##OBJECT_NAME##"["##STRING_NAME##")"##OBJECT_NAME

static jclass __callerClass;
static jclass __objectClass;
static jmethodID __mid_create;
static jmethodID __mid_invoke;

const char * getStringValue(LuaBridgeCaller* owner, LuaParam* lp){
    int type = lp->type;
    switch (type) {
        case LUA_TNUMBER: {
            lua_Number *a = static_cast<lua_Number *>(lp->value);
            std::stringstream out;
            out << *a;
            return out.str().c_str();
        }
        case LUA_TBOOLEAN: {
            int *a = static_cast<int *>(lp->value);
            std::stringstream out;
            out << *a;
            return out.str().c_str();
        }
        case LUA_TSTRING: {
            return static_cast<const char *>(lp->value);
        }
        case LUA_TNIL: {
            return nullptr;
        }

        default:
            std::stringstream out;
            out << "not support type = " << type;
            owner->luaError(out.str().c_str());
            return nullptr;
    }
}

void initLuaJavaCaller(){
    JNIEnv *const env = getJNIEnv();
    __callerClass = env->FindClass(CALLER_CLASS);
    __objectClass = env->FindClass("java/lang/Object");
    __mid_create = env->GetMethodID(__callerClass, MNAME_CREATE ,SIG_CREATE);
    __mid_invoke = env->GetMethodID(__callerClass, MNAME_INVOKE ,SIG_INVOKE);
}

void deInitLuaJavaCaller(){
    JNIEnv *const env = getJNIEnv();
    env->DeleteLocalRef(__callerClass);
    env->DeleteLocalRef(__objectClass);
    __callerClass = nullptr;
    __mid_create = nullptr;
    __mid_invoke = nullptr;
}

// 适配java 8大基本类型和自定义对象？
class LuaJavaCaller: public LuaBridgeCaller{

    jobject jobj;
    jclass jclazz;
public:
    ~LuaJavaCaller(){
        JNIEnv *const env = getJNIEnv();
        env->DeleteLocalRef(jclazz);
        env->DeleteLocalRef(jobj);
    }
    LuaJavaCaller(const char *classname, LuaMediator &holder){
        JNIEnv *const env = getJNIEnv();
        jclazz = env->FindClass(classname);
        if(jclazz == nullptr){
            std::stringstream out;
            out << "can't find class, className = " << classname << " !";
            const char *const msg = out.str().c_str();
            ext_println(msg);
            luaError(msg);
        } else{
            if(holder.count == 0){
                jobj = env->AllocObject(jclazz); //default constructor
            } else{
                const char* name = getStringValue(this, &holder.lp[0]);
                int size = holder.count - 1;
                jobjectArray const arr = env->NewObjectArray(size, __objectClass, nullptr);
                for (int i = 0; i < size; ++i) {
                    const char *const str = getStringValue(this, &holder.lp[i + 1]);
                    //if(str == null . means it is
                    jstring const jstr = env->NewStringUTF(str);
                    env->SetObjectArrayElement(arr, i, jstr);
                }
                
                //prepare string array
                jobjectArray const msgArr = env->NewObjectArray(1, env->FindClass("java/lang/String"), nullptr);
                jvalue values[4];
                values[0].l = env->NewStringUTF(classname);
                values[1].l = env->NewStringUTF(name);
                values[2].l = arr;
                values[3].l = msgArr;

                jobject const result = env->CallStaticObjectMethodA(__callerClass, __mid_create, values);
                jstring const msg = static_cast<jstring const>(env->GetObjectArrayElement(msgArr, 0));
                if(msg != nullptr){
                   const jchar *const chs = env->GetStringChars(msg, nullptr);
                   luaError(reinterpret_cast<const char *>(chs));
                } else{
                    jobj = result;
                }
            }
        }
    }
    void* call(const char* mName,  LuaMediator& holder){
        JNIEnv *const env = getJNIEnv();
       //TODO  env->GetMethodID(jclazz, mName, )
       // holder.resultType = LUA_TNUMBER;
    }
};
