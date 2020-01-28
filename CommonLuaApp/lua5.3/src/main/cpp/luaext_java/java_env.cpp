//
// Created by Administrator on 2019/7/1.
//

#include <jni.h>
#include <string>
#include <android/log.h>
#include "java_env.h"

#define TAG "JavaEnv"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__)

JavaVM *g_jvm;

Registration createRegistration(char *clazz, JNINativeMethod methods[], int len) {
    Registration res;
    res.clazz = clazz;
    res.methods = methods;
    res.len = len;
    return res;
}

int registerMethods(JNIEnv *env, Registration n) {
    LOGD("Registration: class = %s, len = %d", n.clazz, n.len);
    jclass clazz = env->FindClass(n.clazz);
    if (clazz == nullptr) {
        LOGW("can't find class %s", n.clazz);
        return JNI_ERR;
    }
    int result;
    if ((env)->RegisterNatives(clazz, n.methods, n.len) < 0) {
        LOGW("register method failed");
        result = JNI_ERR;
    } else {
        result = JNI_OK;
    }
    env->DeleteLocalRef(clazz);
    return result;
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv((void **) (&env), JNI_VERSION_1_4) != JNI_OK) {
        return JNI_ERR;
    }
    // __JNI_OnLoad(env);
    registerMethods(env, getLuaStateRegistration());
    registerMethods(env, getLua2JavaRegistration());
    g_jvm = vm;
    initLuaJavaCaller();
    return JNI_VERSION_1_4;
}

JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {
    LOGD("JNI_OnUnload ------");
    deInitLuaJavaCaller();
    g_jvm = NULL;
}

JNIEnv *getJNIEnv() {
    JNIEnv *env = NULL;
    if (g_jvm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGD("getJNIEnv failed ! ------");
        return NULL;
    }
    return env;
}

/** if from sub thread . jni-env should attach first. */
JNIEnv *attachJNIEnv() {
    JNIEnv *env = NULL;
    JavaVMAttachArgs args;
    args.version = JNI_VERSION_1_4; // choose your JNI version
    args.name = "N_attachJNIEnv";   // you might want to give the java thread a name
    args.group = NULL;
    if (g_jvm->AttachCurrentThreadAsDaemon(&env, &args) != JNI_OK) {
        LOGD("attachJNIEnv failed ! ------");
        return NULL;
    }
    return env;
}

void detachJNIEnv() {
    g_jvm->DetachCurrentThread();
}