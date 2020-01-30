//
// Created by heaven7 on 2019/7/29.
//
#include "java_env.h"
#include "../luaextra/LuaRegistry.h"
#include "../luaextra/lua_extra.h"
#include "sstream"
#include "class_info.h"

extern "C" {
#include "../lua/lua.h"
}

#define STRING_NAME "Ljava/lang/String;"
#define OBJECT_NAME "Ljava/lang/Object;"
#define CALLER_CLASS "com/heaven7/java/lua/LuaJavaCaller"
#define LUA2JAVA_CLASS "com/heaven7/java/lua/Lua2JavaValue"
#define MNAME_CREATE "create"
#define MNAME_INVOKE "invoke"

//Object create(long luaStatePtr, String className, String name, Object[] args, String[] errorMsg)
#define SIG_CREATE "(J" STRING_NAME STRING_NAME "[" OBJECT_NAME "[" STRING_NAME ")" OBJECT_NAME
//void invoke(long luaStatePtr, Object owner, String className, String method, Object[] args, String[] errorMsg)
#define SIG_INVOKE "(J" OBJECT_NAME STRING_NAME STRING_NAME "[" OBJECT_NAME "[" STRING_NAME ")V"

#define SIG_NEW_LUA2JAVA "(IJ)" "L" LUA2JAVA_CLASS ";"

static jclass __callerClass;
static jclass __objectClass;
static jmethodID __mid_create;
static jmethodID __mid_invoke;

static jmethodID __mid_create_lua2java;
static jmethodID __mid_getType_lua2java;
static jmethodID __mid_getValuePtr_lua2java;
static jclass __lua2JavaClass;

jstring getStringValue(JNIEnv *env, jclass clazz, long ptr);
LuaBridgeCaller* LBCCreator_0(lua_State* L,const char* classname, LuaMediator* holder);

static const char *getString(LuaParam *lp, bool *state) {
    int type = lp->type;
    *state = false;
    switch (type) {
        case DTYPE_LUA2JAVA_VALUE: {
            jobject obj = static_cast<jobject>(lp->value);
            auto i = getType_Lua2Java(obj);
            if (i == DTYPE_STRING) {
                auto ptr = getValuePtr_Lua2Java(obj);
                auto pEnv = getJNIEnv();
                auto str = getStringValue(pEnv, nullptr, ptr);
                *state = true;
                return pEnv->GetStringUTFChars(str, 0);
            }
        }
            break;

        case DTYPE_STRING: {
            return static_cast<const char *>(lp->value);
        }
    }
    return nullptr;
}

void *newLua2JavaValue0(int type, long long ptrOrIndex) {
    JNIEnv *const env = getJNIEnv();
    return env->CallStaticObjectMethod(__lua2JavaClass, __mid_create_lua2java, type, ptrOrIndex);
}

int getType_Lua2Java(jobject obj) {
    JNIEnv *const env = getJNIEnv();
    return env->CallIntMethod(obj, __mid_getType_lua2java);
}

jlong getValuePtr_Lua2Java(jobject obj) {
    JNIEnv *const env = getJNIEnv();
    return env->CallLongMethod(obj, __mid_getValuePtr_lua2java);
}

void releaseJavaObject0(void *obj) {
    auto jobj = static_cast<jobject>(obj);
    JNIEnv *const env = getJNIEnv();
    env->DeleteLocalRef(jobj);
}

void initLuaJavaCaller() {
    JNIEnv *const env = getJNIEnv();
    __callerClass = env->FindClass(CALLER_CLASS);
    __objectClass = env->FindClass("java/lang/Object");
    __mid_create = env->GetStaticMethodID(__callerClass, MNAME_CREATE, SIG_CREATE);
    __mid_invoke = env->GetStaticMethodID(__callerClass, MNAME_INVOKE, SIG_INVOKE);

    __lua2JavaClass = env->FindClass(LUA2JAVA_CLASS);
    __mid_create_lua2java = env->GetStaticMethodID(__lua2JavaClass, "of", SIG_NEW_LUA2JAVA);
    __mid_getType_lua2java = env->GetMethodID(__lua2JavaClass, "getType", "()I");
    __mid_getValuePtr_lua2java = env->GetMethodID(__lua2JavaClass, "getValuePtr", "()J");
    //set callback
    setLua2JavaValue_Creator(&newLua2JavaValue0);
    setJava_Object_Releaser(&releaseJavaObject0);
    setLuaBridgeCallerCreator(&LBCCreator_0);
}

void deInitLuaJavaCaller() {
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

class LuaJavaCaller: public LuaBridgeCaller {
private:
    jobject jobj;
    jstring rawStr;
public:
    ~LuaJavaCaller() {
        JNIEnv *const env = getJNIEnv();
        env->DeleteLocalRef(jobj);
        if(rawStr != nullptr){
            env->ReleaseStringUTFChars(rawStr, getClassname());
            env->DeleteLocalRef(rawStr);
        }
    }
    LuaJavaCaller(jobject jobj, jstring cn){
        JNIEnv *const env = getJNIEnv();
        auto classname = env->GetStringUTFChars(cn , nullptr);
        rawStr = cn;

        setClassname(classname);
        this->jobj = jobj;
    }
    LuaJavaCaller(lua_State *L, const char *classname, LuaMediator *holder) {
        rawStr = nullptr;
        setClassname(classname);

        JNIEnv *const env = getJNIEnv();
        jclass jclazz = env->FindClass(classname);
        if (jclazz == nullptr) {
            std::stringstream out;
            out << "can't find class, className = " << classname << " !";
            const char *const msg = out.str().c_str();
            ext_println(msg);
            luaL_error(L, msg);
        } else {
            if (holder->count == 0) {
                jobj = env->AllocObject(jclazz); //default constructor
                env->DeleteLocalRef(jclazz);
            } else {
                env->DeleteLocalRef(jclazz);
                bool success;
                const char *name = getString(&holder->lp[0], &success);
                if (name == nullptr) {
                    luaL_error(L,
                               "the first param of create object must be constructor name. like '<init>'");
                } else{
                    const int size = holder->count - 1;
                    //params
                    const jobjectArray const args = env->NewObjectArray(size, __objectClass, nullptr);
                    for (int i = 0; i < size; ++i) {
                        auto param = static_cast<jobject>(holder->lp[i + 1].value);
                        env->SetObjectArrayElement(args, i, param);
                    }
                    // static Object create(String className, String name, Object[] args, String[] errorMsg)
                    //prepare string array
                    jobjectArray const msgArr = env->NewObjectArray(1,
                                                                    env->FindClass("java/lang/String"),
                                                                    nullptr);
                    jvalue values[5];
                    values[0].j = (jlong)L;
                    values[1].l = env->NewStringUTF(classname);
                    values[2].l = name != nullptr ? env->NewStringUTF(name) : nullptr;
                    values[3].l = args;
                    values[4].l = msgArr;
                    //create
                    jobject const result = env->CallStaticObjectMethodA(__callerClass, __mid_create,
                                                                        values);
                    jstring const msg = static_cast<jstring const>(env->GetObjectArrayElement(msgArr,
                                                                                              0));
                    if (msg != nullptr) {
                        const char *chs = env->GetStringUTFChars(msg, nullptr);
                        //release
                        env->DeleteLocalRef(args);
                        env->DeleteLocalRef(msgArr);
                        luaL_error(L, chs);
                        env->ReleaseStringUTFChars(msg, chs);
                        env->DeleteLocalRef(msg);
                    } else {
                        //release
                        env->DeleteLocalRef(args);
                        env->DeleteLocalRef(msgArr);

                        if (result == nullptr) {
                            luaL_error(L, "create object failed for class(%s)", classname);
                        } else {
                            jobj = result;
                        }
                    }
                }
            }
        }
    }

    void *call(lua_State *L, const char *cn, const char *mName, LuaMediator *holder) {
        JNIEnv *const env = getJNIEnv();
        const int size = holder->count;
        //params
        bool success;
        jobjectArray const args = env->NewObjectArray(size, __objectClass, nullptr);
        for (int i = 0; i < size; ++i) {
            auto param = static_cast<jobject>(holder->lp[i].value);
            env->SetObjectArrayElement(args, i, param);
        }

        //prepare string array
        jobjectArray const msgArr = env->NewObjectArray(1, env->FindClass("java/lang/String"),nullptr);
        jvalue values[6];
        values[0].j = (jlong)L;
        values[1].l = jobj;
        values[2].l = env->NewStringUTF(holder->className);
        values[3].l = env->NewStringUTF(mName);
        values[4].l = args;
        values[5].l = msgArr;
        //create
        env->CallStaticVoidMethodA(__callerClass, __mid_invoke, values);
        jstring const msg = static_cast<jstring const>(env->GetObjectArrayElement(msgArr, 0));
        void *callResult;
        if (msg != nullptr) {
            const char *chs = env->GetStringUTFChars(msg, nullptr);
            //release
            env->DeleteLocalRef(args);
            env->DeleteLocalRef(msgArr);
            luaL_error(L, chs);
            env->ReleaseStringUTFChars(msg, chs);
            env->DeleteLocalRef(msg);
        } else {
            //release
            env->DeleteLocalRef(args);
            env->DeleteLocalRef(msgArr);
        }
        // holder.resultType = LUA_TNUMBER;

        return nullptr;
    }
};
LuaBridgeCaller* newJavaLBC(jobject jobj, jstring classname){
    return reinterpret_cast<LuaBridgeCaller *>(new LuaJavaCaller(jobj, classname));
}
LuaBridgeCaller* LBCCreator_0(lua_State* L, const char* classname, LuaMediator* holder){
    return reinterpret_cast<LuaBridgeCaller *>(new LuaJavaCaller(L, classname , holder));
}
//--------------- lua2java ---------------
jstring getStringValue(JNIEnv *env, jclass clazz, long ptr) {
    const char *ch = reinterpret_cast<const char *>(ptr);
    return env->NewStringUTF(ch);
}

jboolean getBooleanValue(JNIEnv *env, jclass clazz, long ptr) {
    int *ch = reinterpret_cast<int *>(ptr);
    return static_cast<jboolean>(*ch == 1);
}

jdouble getDoubleValue(JNIEnv *env, jclass clazz, long ptr) {
    auto *ch = reinterpret_cast<lua_Number *>(ptr);
    return *ch;
}

void releaseBoolean(JNIEnv *env, jclass clazz, long ptr) {
    auto *ch = reinterpret_cast<int *>(ptr);
    delete ch;
}

void releaseNumber(JNIEnv *env, jclass clazz, long ptr) {
    auto *ch = reinterpret_cast<lua_Number *>(ptr);
    delete ch;
}

static JNINativeMethod lua2java_methods[] = {
        {"getString_",      "(J)" SIG_JSTRING, (void *) getStringValue},
        {"getBoolean_",     "(J)Z",            (void *) getBooleanValue},
        {"getNumber_",      "(J)D",            (void *) getDoubleValue},
        {"releaseNumber_",  "(J)V",            (void *) releaseNumber},
        {"releaseBoolean_", "(J)V",            (void *) releaseBoolean}
};

Registration getLua2JavaRegistration() {
    return createRegistration(LUA2JAVA_CLASS, lua2java_methods,
                              sizeof(lua2java_methods) / sizeof(lua2java_methods[0]));
}