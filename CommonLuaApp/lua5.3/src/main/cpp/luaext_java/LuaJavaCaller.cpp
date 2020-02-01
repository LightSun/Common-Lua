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
#include <android/log.h>

#define TAG "LuaJavaCaller"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

#define STRING_NAME "Ljava/lang/String;"
#define OBJECT_NAME "Ljava/lang/Object;"

#define CALLER_CLASS    "com/heaven7/java/lua/LuaJavaCaller"
#define LUA2JAVA_CLASS  "com/heaven7/java/lua/Lua2JavaValue"
#define FUNC_CLASS      "com/heaven7/java/lua/LuaFunction"
#define TRAVELLER_CLASS "com/heaven7/java/lua/LuaTraveller"

#define MNAME_CREATE "create"
#define MNAME_INVOKE "invoke"

#define SIG_LUA2JAVA_CLASS "L" LUA2JAVA_CLASS ";"
#define SIG_CREATE "(J" STRING_NAME STRING_NAME "[" OBJECT_NAME "[" OBJECT_NAME ")" OBJECT_NAME
#define SIG_INVOKE "(J" OBJECT_NAME STRING_NAME STRING_NAME "[" OBJECT_NAME "[" OBJECT_NAME ")I"
#define SIG_FUNC_EXECUTE "(J)I"
#define SIG_TRAVEL "(J" SIG_LUA2JAVA_CLASS SIG_LUA2JAVA_CLASS ")I"

#define SIG_NEW_LUA2JAVA "(IJ)" "L" LUA2JAVA_CLASS ";"

static jclass __callerClass;
static jclass __objectClass;
static jclass __lua2JavaClass;
static jclass __luaFuncClass;
static jclass __luaTravelClass;

jstring getStringValue(JNIEnv *env, jclass clazz, long ptr);
LuaBridgeCaller* LBCCreator_0(lua_State* L,const char* classname, LuaMediator* holder);

void *newLua2JavaValue0(int type, long long ptrOrIndex) {
    JNIEnv *const env = getJNIEnv();
    jmethodID __mid_create_lua2java = env->GetStaticMethodID(__lua2JavaClass, "of", SIG_NEW_LUA2JAVA);
    auto result = env->CallStaticObjectMethod(__lua2JavaClass, __mid_create_lua2java, type, ptrOrIndex);
    return result;
}

int getType_Lua2Java(jobject obj) {
    JNIEnv *const env = getJNIEnv();
    jmethodID __mid_getType_lua2java = env->GetMethodID(__lua2JavaClass, "getType", "()I");
    auto result = env->CallIntMethod(obj, __mid_getType_lua2java);
    return result;
}

jlong getValuePtr_Lua2Java(jobject obj) {
    JNIEnv *const env = getJNIEnv();
    jmethodID __mid_getValuePtr_lua2java = env->GetMethodID(__lua2JavaClass, "getValuePtr", "()J");
    auto result = env->CallLongMethod(obj, __mid_getValuePtr_lua2java);
    return result;
}
void releaseJavaObject0(void *obj) {
    auto jobj = static_cast<jobject>(obj);
    JNIEnv *const env = getJNIEnv();
    env->DeleteLocalRef(jobj);
}
int executeLuaFunction(jobject obj, lua_State* L){
    auto env = getJNIEnv();
    auto mid = env->GetMethodID(__luaFuncClass, "execute", SIG_FUNC_EXECUTE);
    auto result = env->CallIntMethod(obj, mid, reinterpret_cast<jlong>(L));
    return result;
}
int travelImpl(lua_State* L, jobject t, void* key, void* value){
    auto env = getJNIEnv();
    auto mid = env->GetMethodID(__luaTravelClass, "travel", SIG_TRAVEL);
    return env->CallIntMethod(t, mid, reinterpret_cast<jlong>(L),
            static_cast<jobject>(key), static_cast<jobject>(value));
}

const char *getString(LuaParam *lp) {
    int type = lp->type;
    switch (type) {
        case DTYPE_LUA2JAVA_VALUE: {
            jobject obj = static_cast<jobject>(lp->value);
            auto i = getType_Lua2Java(obj);
            if (i == DTYPE_STRING) {
                auto ptr = getValuePtr_Lua2Java(obj);
                auto pEnv = getJNIEnv();
                auto str = getStringValue(pEnv, nullptr, ptr);
                return pEnv->GetStringUTFChars(str, nullptr);
            }
        }
            break;

        case DTYPE_STRING: {
            return static_cast<const char *>(lp->value);
        }
    }
    return nullptr;
}


void initLuaJavaCaller() {
    JNIEnv *const env = getJNIEnv();
    __callerClass = getGlobalClass(env, CALLER_CLASS);
    __objectClass = getGlobalClass(env, "java/lang/Object");
    __lua2JavaClass = getGlobalClass(env, LUA2JAVA_CLASS);
    __luaFuncClass = getGlobalClass(env, FUNC_CLASS);
    __luaTravelClass = getGlobalClass(env, TRAVELLER_CLASS);
    //set callback
    setLua2JavaValue_Creator(&newLua2JavaValue0);
    setJava_Object_Releaser(&releaseJavaObject0);
    setLuaBridgeCallerCreator(&LBCCreator_0);
}

void deInitLuaJavaCaller() {
    JNIEnv *const env = getJNIEnv();
    env->DeleteGlobalRef(__callerClass);
    env->DeleteGlobalRef(__objectClass);
    env->DeleteGlobalRef(__lua2JavaClass);
    env->DeleteGlobalRef(__luaFuncClass);
    env->DeleteGlobalRef(__luaTravelClass);
    __callerClass = nullptr;
    __objectClass = nullptr;
    __lua2JavaClass = nullptr;
    __luaFuncClass = nullptr;
    __luaTravelClass = nullptr;
}

class LuaJavaCaller: public LuaBridgeCaller {
private:
    jobject jobj;
    jstring rawStr;

    //must use global ref when used as member
    inline void setJavaObject(JNIEnv * env,jobject obj){
        this->jobj = env->NewGlobalRef(obj);
    }
public:
    ~LuaJavaCaller() {
        JNIEnv *const env = getJNIEnv();
        env->DeleteGlobalRef(jobj);
        if(rawStr != nullptr){
            env->ReleaseStringUTFChars(rawStr, getClassname());
            env->DeleteGlobalRef(rawStr);
        }
        jobj = nullptr;
        rawStr = nullptr;
    }
    LuaJavaCaller(jobject jobj, jstring cn){
        JNIEnv *const env = getJNIEnv();
        auto classname = env->GetStringUTFChars(cn , nullptr);
        rawStr = static_cast<jstring>(env->NewGlobalRef(cn));
        setClassname(classname);

        setJavaObject(env, jobj);
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
                setJavaObject(env, env->AllocObject(jclazz));//default constructor
                env->DeleteLocalRef(jclazz);
            } else {
                env->DeleteLocalRef(jclazz);
                jmethodID __mid_create = env->GetStaticMethodID(__callerClass, MNAME_CREATE, SIG_CREATE);

                const char *name = getString(&holder->lp[0]);
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
                    jobjectArray const msgArr = env->NewObjectArray(1, __objectClass, nullptr);
                    jvalue values[5];
                    values[0].j = (jlong)L;
                    values[1].l = env->NewStringUTF(classname);
                    values[2].l = env->NewStringUTF(name);
                    values[3].l = args;
                    values[4].l = msgArr;
                    //create
                    jobject const result = env->CallStaticObjectMethodA(__callerClass, __mid_create, values);
                    jstring const msg = static_cast<jstring const>(env->GetObjectArrayElement(msgArr,0));
                    //release
                    env->DeleteLocalRef(args);
                    env->DeleteLocalRef(msgArr);
                    if (msg != nullptr) {
                        const char *chs = env->GetStringUTFChars(msg, nullptr);
                        //release
                        luaL_error(L, chs);
                        env->ReleaseStringUTFChars(msg, chs);
                        env->DeleteLocalRef(msg);
                    } else {
                        if (result == nullptr) {
                            luaL_error(L, "create object failed for class(%s)", classname);
                        } else {
                            setJavaObject(env, result);
                        }
                    }
                }
            }
        }
    }

    int call(lua_State *L, const char *cn, const char *mName, LuaMediator *holder) override{
        JNIEnv *const env = getJNIEnv();
        const int size = holder->count;
        //dumpReferenceTables(env);
        jmethodID __mid_invoke = env->GetStaticMethodID(__callerClass, MNAME_INVOKE, SIG_INVOKE);

        jobjectArray args = env->NewObjectArray(size, __objectClass, nullptr);
        for (int i = 0; i < size; ++i) {
            auto param = static_cast<jobject>(holder->lp[i].value);
            env->SetObjectArrayElement(args, i, param);
        }

        //prepare string array
        jobjectArray msgArr = env->NewObjectArray(1, __objectClass, nullptr);
        jvalue values[6];
        values[0].j = reinterpret_cast<jlong>(L);
        values[1].l = jobj;
        values[2].l = env->NewStringUTF(holder->className);
        values[3].l = env->NewStringUTF(mName);
        values[4].l = args;
        values[5].l = msgArr;
        //create
        auto result = env->CallStaticIntMethodA(__callerClass, __mid_invoke, values);
        jstring const msg = static_cast<jstring>(env->GetObjectArrayElement(msgArr, 0));

        env->DeleteLocalRef(args);
        env->DeleteLocalRef(msgArr);
        if (msg != nullptr) {
            const char *chs = env->GetStringUTFChars(msg, nullptr);
            //release
            luaL_error(L, chs);
            env->ReleaseStringUTFChars(msg, chs);
            env->DeleteLocalRef(msg);
        }
        return result;
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