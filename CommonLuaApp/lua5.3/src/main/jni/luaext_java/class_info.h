//
// Created by Administrator on 2019/7/31.
//

#ifndef COMMONLUAAPP_CLASS_INFO_H
#define COMMONLUAAPP_CLASS_INFO_H

#include "jni.h"
#include "string"
#include "../common/list.h"
#include "../common/map2.h"

namespace LUA_JAVA {
    class MethodInfo {
    public:
        std::string name;
        std::string sig;
        List<jclass> types;

        MethodInfo() {}

        ~MethodInfo() {
            JNIEnv *const pEnv = getJNIEnv();
            for (int i = 0, size = types.size(); i < size; ++i) {
                pEnv->DeleteLocalRef(types.getAt(i));
            }
            types.clear();
        }
    };

    class ClassInfo {
    private:
        std::string className;
        Map<std::string, MethodInfo> mMethodMap;
        Map<std::string, MethodInfo> mConstructorMap;
    public:
    };
}
#endif //COMMONLUAAPP_CLASS_INFO_H
