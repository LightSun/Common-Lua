//
// Created by Administrator on 2019/7/23.
//

#include "jni.h"
#include "../lua_test/tests_all.h"

extern "C" JNIEXPORT
void JNICALL Java_com_heaven7_java_lua_LuaTest_nTestBindCpp1(JNIEnv *env, jclass clazz, long ptr, jstring script){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    char * content = const_cast<char *>(env->GetStringChars(script, nullptr));
    call_testLua(L, content);
}

extern "C" JNIEXPORT
void JNICALL Java_com_heaven7_java_lua_LuaTest_nTestLuaRegistry(JNIEnv *env, jclass clazz, long ptr, jstring script){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    char * content = const_cast<char *>(env->GetStringChars(script, nullptr));
    call_testLuaRegistry(L, content);
}

extern "C" JNIEXPORT
void JNICALL Java_com_heaven7_java_lua_LuaTest_nTestLuaRegistryWrapper(JNIEnv *env, jclass clazz, long ptr, jstring script){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    char * content = const_cast<char *>(env->GetStringChars(script, nullptr));
    call_testLuaRegistryWrapper(L, content);
}

extern "C" JNIEXPORT
void JNICALL Java_com_heaven7_java_lua_LuaTest_nTestAccessCppObjectInLua(JNIEnv *env, jclass clazz, long ptr, jstring script){
    lua_State *L = reinterpret_cast<lua_State *>(ptr);
    char * content = const_cast<char *>(env->GetStringChars(script, nullptr));
    testAccessCppObjInLua(L, content);
}

extern "C"
jstring charToJstring(JNIEnv *env, const char *pat, int len) {
    jclass strClass = (env)->FindClass("java/lang/String");
    jmethodID ctorID = (env)->GetMethodID(strClass, "<init>","([BLjava/lang/String;)V");
    jbyteArray bytes = (env)->NewByteArray(len);
    (env)->SetByteArrayRegion(bytes, 0, len, (jbyte *) pat);
    jstring encoding = (env)->NewStringUTF("utf-8");
    return (jstring) (env)->NewObject(strClass, ctorID, bytes, encoding);
}

/*************************** HEADER FILES ***************************/
#include <stdio.h>
#include <memory.h>

/*********************** FUNCTION DEFINITIONS ***********************/
int blowfish_test()
{
    BYTE key1[8]  = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
    BYTE key2[8]  = {0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff};
    BYTE key3[24] = {0xF0,0xE1,0xD2,0xC3,0xB4,0xA5,0x96,0x87,
                     0x78,0x69,0x5A,0x4B,0x3C,0x2D,0x1E,0x0F,
                     0x00,0x11,0x22,0x33,0x44,0x55,0x66,0x77};
    BYTE p1[BLOWFISH_BLOCK_SIZE] = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
    BYTE p2[BLOWFISH_BLOCK_SIZE] = {0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff};
    BYTE p3[BLOWFISH_BLOCK_SIZE] = {0xFE,0xDC,0xBA,0x98,0x76,0x54,0x32,0x10};

    BYTE c1[BLOWFISH_BLOCK_SIZE] = {0x4e,0xf9,0x97,0x45,0x61,0x98,0xdd,0x78};
    BYTE c2[BLOWFISH_BLOCK_SIZE] = {0x51,0x86,0x6f,0xd5,0xb8,0x5e,0xcb,0x8a};
    BYTE c3[BLOWFISH_BLOCK_SIZE] = {0x05,0x04,0x4b,0x62,0xfa,0x52,0xd0,0x80};

    BYTE enc_buf[BLOWFISH_BLOCK_SIZE];
    BLOWFISH_KEY key;
    int pass = 1;

    // Test vector 1.
    blowfish_key_setup(key1, &key, BLOWFISH_BLOCK_SIZE);
    blowfish_encrypt(p1, enc_buf, &key);
    pass = pass && !memcmp(c1, enc_buf, BLOWFISH_BLOCK_SIZE);
    blowfish_decrypt(c1, enc_buf, &key);
    pass = pass && !memcmp(p1, enc_buf, BLOWFISH_BLOCK_SIZE);

    // Test vector 2.
    blowfish_key_setup(key2, &key, BLOWFISH_BLOCK_SIZE);
    blowfish_encrypt(p2, enc_buf, &key);
    pass = pass && !memcmp(c2, enc_buf, BLOWFISH_BLOCK_SIZE);
    blowfish_decrypt(c2, enc_buf, &key);
    pass = pass && !memcmp(p2, enc_buf, BLOWFISH_BLOCK_SIZE);

    // Test vector 3.
    blowfish_key_setup(key3, &key, 24);
    blowfish_encrypt(p3, enc_buf, &key);
    pass = pass && !memcmp(c3, enc_buf, BLOWFISH_BLOCK_SIZE);
    blowfish_decrypt(c3, enc_buf, &key);
    pass = pass && !memcmp(p3, enc_buf, BLOWFISH_BLOCK_SIZE);

    return(pass);
}

extern "C" JNIEXPORT
void JNICALL Java_com_heaven7_java_lua_LuaTest_nBFDo(JNIEnv *env, jclass clazz,jbyteArray initKey, jbyteArray str, jbyteArray out, jint en){
    jbyte * keyStr;
    jbyte* contentStr;
    jbyte* outStr;

    keyStr = env->GetByteArrayElements(initKey, NULL);
    const int keyLen = env->GetArrayLength(initKey);

    contentStr = env->GetByteArrayElements(str, NULL);
    const int contentLen = env->GetArrayLength(str);

    outStr = env->GetByteArrayElements(out, NULL);

    BLOWFISH_KEY* key = new BLOWFISH_KEY();
    if(en){
       BF_en(key, reinterpret_cast<const char *>(keyStr), keyLen,
               reinterpret_cast<const char *>(contentStr),
               reinterpret_cast<char *>(outStr), contentLen);
    } else{
        BF_de(key, reinterpret_cast<const char *>(keyStr), keyLen,
              reinterpret_cast<const char *>(contentStr),
              reinterpret_cast<char *>(outStr), contentLen);
    }
    delete(key);

    env->ReleaseByteArrayElements(initKey, keyStr, 0);
    env->ReleaseByteArrayElements(str, contentStr, 0);
    env->ReleaseByteArrayElements(out, outStr, 0);
}
extern "C" JNIEXPORT
void JNICALL Java_com_heaven7_java_lua_LuaTest_nBFTest(JNIEnv *env, jclass clazz){
    ext_println("Blowfish tests: %s");
    ext_println(blowfish_test() ? "SUCCEEDED" : "FAILED");
}

#include <sstream>
extern "C" JNIEXPORT
void JNICALL Java_com_heaven7_java_lua_LuaTest_nBfDecodeFile(JNIEnv *env, jclass clazz, jstring str){
    const char * file = env->GetStringUTFChars(str, nullptr);
    FILE* f = fopen(file, "rb");
    size_t contentOffset = BF_HEADER_SIZE + 8;

    size_t len;
    size_t readCount;
    char* buf;
    fseek(f, 0L, SEEK_END);
    len = static_cast<size_t>(ftell(f));
    ext_println("file length = ");
    std::stringstream ss;
    ss << len;
    ext_println(ss.str().c_str());

    size_t contentLen = len - contentOffset;
    buf = static_cast<char *>(malloc(contentLen));
    fseek(f, contentOffset, SEEK_SET);
    readCount = fread(buf, 1, contentLen, f);
    if(readCount != contentLen){
        ext_println("content error. ");
    }
    free(buf);

    fseek(f, 0, SEEK_SET);
    f = ext_decode(f, BF_HEADER_SIZE, NULL);
    if(f == nullptr){
        ext_println("decode failed.");
        return;
    }

    fseek(f, 0, SEEK_SET);
    buf = static_cast<char *>(malloc(contentLen));
    readCount = fread(buf, 1, contentLen, f);
    fclose(f);

}