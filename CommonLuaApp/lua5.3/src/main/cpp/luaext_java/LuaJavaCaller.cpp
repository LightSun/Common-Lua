//
// Created by heaven7 on 2019/7/29.
//
#include "java_env.h"
#include "../luaextra/LuaRegistry.h"
#include "../luaextra/lua_extra.h"
#include "sstream"
#include "class_info.h"

extern "C"{
#include "../lua/lua.h"
}

#define STRING_NAME "Ljava/lang/String;"
#define OBJECT_NAME "Ljava/lang/Object;"
#define CALLER_CLASS "com/heaven7/java/lua/LuaJavaCaller"
#define LUA2JAVA_CLASS "com/heaven7/java/lua/Lua2JavaValue"
#define MNAME_CREATE "create"
#define MNAME_INVOKE "invoke"

#define SIG_CREATE "(" STRING_NAME STRING_NAME "[" OBJECT_NAME "[" STRING_NAME ")" OBJECT_NAME
#define SIG_INVOKE "(" OBJECT_NAME STRING_NAME STRING_NAME "[" OBJECT_NAME "[" STRING_NAME ")" OBJECT_NAME

#define SIG_NEW_LUA2JAVA "(IJ)" "L" LUA2JAVA_CLASS ";"

static jclass __callerClass;
static jclass __objectClass;
static jmethodID __mid_create;
static jmethodID __mid_invoke;

static jmethodID  __mid_create_lua2java;
static jmethodID __mid_getType_lua2java;
static jmethodID __mid_getValuePtr_lua2java;
static jclass __lua2JavaClass;

jstring getStringValue(JNIEnv *env, jclass clazz, long ptr);

static const char * getString(LuaParam* lp, bool* state){
    int type = lp->type;
    *state = false;
    switch (type) {
        case DTYPE_LUA2JAVA_VALUE: {
            jobject obj = static_cast<jobject>(lp->value);
            auto i = getType_Lua2Java(obj);
            if(i == DTYPE_STRING){
                auto ptr = getValuePtr_Lua2Java(obj);
                auto pEnv = getJNIEnv();
                auto str = getStringValue(pEnv, nullptr, ptr);
                *state = true;
                return pEnv->GetStringUTFChars(str, 0);
            }
        }break;

        case DTYPE_STRING: {
            return static_cast<const char *>(lp->value);
        }
    }
    return nullptr;
}

void initLuaJavaCaller(){
    JNIEnv *const env = getJNIEnv();
    __callerClass = env->FindClass(CALLER_CLASS);
    __objectClass = env->FindClass("java/lang/Object");
    __mid_create = env->GetStaticMethodID(__callerClass, MNAME_CREATE ,SIG_CREATE);
    __mid_invoke = env->GetStaticMethodID(__callerClass, MNAME_INVOKE ,SIG_INVOKE);

    __lua2JavaClass = env->FindClass(LUA2JAVA_CLASS);
    __mid_create_lua2java = env->GetStaticMethodID(__lua2JavaClass, "of", SIG_NEW_LUA2JAVA);
    __mid_getType_lua2java = env->GetMethodID(__lua2JavaClass, "getType", "()I");
    __mid_getValuePtr_lua2java = env->GetMethodID(__lua2JavaClass, "getValuePtr", "()J");
}

void deInitLuaJavaCaller(){
    JNIEnv *const env = getJNIEnv();
    env->DeleteLocalRef(__callerClass);
    env->DeleteLocalRef(__objectClass);
    env->DeleteLocalRef(__lua2JavaClass);
    __callerClass = nullptr;
    __objectClass = nullptr;
    __mid_create = nullptr;
    __mid_invoke = nullptr;

    __mid_create_lua2java = nullptr;
    __mid_getType_lua2java = nullptr;
    __mid_getValuePtr_lua2java = nullptr;
    __lua2JavaClass = nullptr;
}

void* newLua2JavaValue(int type, long long ptrOrIndex){
    JNIEnv *const env = getJNIEnv();
    return env->CallStaticObjectMethod(__lua2JavaClass, __mid_create_lua2java, type, ptrOrIndex);
}

int getType_Lua2Java(jobject obj){
    JNIEnv *const env = getJNIEnv();
    return env->CallIntMethod(obj, __mid_getType_lua2java);
}
jlong getValuePtr_Lua2Java(jobject obj){
    JNIEnv *const env = getJNIEnv();
    return env->CallLongMethod(obj, __mid_getValuePtr_lua2java);
}
void releaseJavaObject(void * obj){
    auto jobj = static_cast<jobject>(obj);
    JNIEnv *const env = getJNIEnv();
    env->DeleteLocalRef(jobj);
}

class LuaJavaCaller: public LuaBridgeCaller{
    jobject jobj;
    jclass jclazz;
public:
    ~LuaJavaCaller(){
        JNIEnv *const env = getJNIEnv();
        env->DeleteLocalRef(jclazz);
        env->DeleteLocalRef(jobj);
    }
    LuaJavaCaller(const char *classname, LuaMediator *holder){
        JNIEnv *const env = getJNIEnv();
        jclazz = env->FindClass(classname);
        if(jclazz == nullptr){
            std::stringstream out;
            out << "can't find class, className = " << classname << " !";
            const char *const msg = out.str().c_str();
            ext_println(msg);
            luaError(msg);
        } else{
            if(holder->count == 0){
                jobj = env->AllocObject(jclazz); //default constructor
            } else{
                bool success;
                const char* name = getString(&holder->lp[0], &success);
                if(name == nullptr){
                   luaError("the first param of create object must be constructor name. like '<init>'");
                } 
                int size = holder->count - 1;
                //params
                const jobjectArray const arr = env->NewObjectArray(size, __objectClass, nullptr);
                for (int i = 0; i < size; ++i) {
                    auto param = static_cast<jobject>(holder->lp[i + 1].value);
                    env->SetObjectArrayElement(arr, i, param);
                }
                // static Object create(String className, String name, Object[] args, String[] errorMsg)
                //prepare string array
                jobjectArray const msgArr = env->NewObjectArray(1, env->FindClass("java/lang/String"), nullptr);
                jvalue values[4];
                values[0].l = env->NewStringUTF(classname);
                values[1].l = name != nullptr ? env->NewStringUTF(name) : nullptr;
                values[2].l = arr;
                values[3].l = msgArr;
                //create
                jobject const result = env->CallStaticObjectMethodA(__callerClass, __mid_create, values);
                jstring const msg = static_cast<jstring const>(env->GetObjectArrayElement(msgArr, 0));
                if(msg != nullptr){
                   const jchar *const chs = env->GetStringChars(msg, nullptr);
                   luaError(reinterpret_cast<const char *>(chs));
                } else{
                   jobj = result;
                }
                //TODO release
            }
        }
    }
    void *call(const char *cn, const char *mName ,LuaMediator *holder) {
        JNIEnv *const env = getJNIEnv();
        int size = holder->count;
        //params
        bool success;
        jobjectArray const arr = env->NewObjectArray(size, __objectClass, nullptr);
        for (int i = 0; i < size; ++i) {
            auto param = static_cast<jobject>(holder->lp[i].value);
            env->SetObjectArrayElement(arr, i, param);
        }

        //prepare string array
        jobjectArray const msgArr = env->NewObjectArray(1, env->FindClass("java/lang/String"), nullptr);
        jvalue values[4];
        values[0].l = env->NewStringUTF(holder->className);
        values[1].l = env->NewStringUTF(mName);
        values[2].l = arr;
        values[3].l = msgArr;
        //create
        jobject const result = env->CallStaticObjectMethodA(__callerClass, __mid_invoke, values);
        jstring const msg = static_cast<jstring const>(env->GetObjectArrayElement(msgArr, 0));
        if(msg != nullptr){
            const jchar *const chs = env->GetStringChars(msg, nullptr);
            luaError(reinterpret_cast<const char *>(chs));
            return nullptr;
        } else{
            //TODO need convert java object to lua.
            if(result == nullptr){
               // return
            }
            return result;
        }
       // holder.resultType = LUA_TNUMBER;
    }
};

//--------------- lua2java ---------------
jstring getStringValue(JNIEnv *env, jclass clazz, long ptr){
    const char* ch = reinterpret_cast<const char *>(ptr);
    return env->NewStringUTF(ch);
}
jboolean getBooleanValue(JNIEnv *env, jclass clazz, long ptr){
    int* ch = reinterpret_cast<int *>(ptr);
    return static_cast<jboolean>(*ch == 1);
}

jdouble getDoubleValue(JNIEnv *env, jclass clazz, long ptr){
    auto* ch = reinterpret_cast<lua_Number *>(ptr);
    return *ch;
}
void releaseBoolean(JNIEnv *env, jclass clazz, long ptr){
    auto* ch = reinterpret_cast<int *>(ptr);
    delete ch;
}
void releaseNumber(JNIEnv *env, jclass clazz, long ptr){
    auto* ch = reinterpret_cast<lua_Number *>(ptr);
    delete ch;
}

JNINativeMethod lua2java_methods[] = {
        {"getString_", "(J)" SIG_JSTRING,            (void *) getStringValue},
        {"getBoolean_", "(J)Z",            (void *) getBooleanValue},
        {"getNumber_", "(J)D",            (void *) getDoubleValue},
        {"releaseNumber_", "(J)V",            (void *) releaseNumber},
        {"releaseBoolean_", "(J)V",            (void *) releaseBoolean}
};

Registration getLua2JavaRegistration(){
    return createRegistration(LUA2JAVA_CLASS, lua2java_methods, sizeof(lua2java_methods)/ sizeof(lua2java_methods[0]));
}