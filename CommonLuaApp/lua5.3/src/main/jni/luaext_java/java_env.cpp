//
// Created by Administrator on 2019/7/1.
//

#include <jni.h>
#include <string>
#include <android/log.h>
#include "java_env.h"

extern "C" {
#include "../luajava/luajava.h"
}

#define TAG "JavaReflect"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

JavaVM *g_jvm;


extern "C" JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv* env;
    if(vm->GetEnv((void **)(&env), JNI_VERSION_1_4) != JNI_OK){
        return JNI_ERR;
    }
   // __JNI_OnLoad(env);
    g_jvm = vm;
    return JNI_VERSION_1_4;
}

extern "C" JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {
    LOGD("JNI_OnUnload ------");
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