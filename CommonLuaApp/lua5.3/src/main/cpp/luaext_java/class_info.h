//
// Created by Administrator on 2019/7/31.
//

#ifndef COMMONLUAAPP_CLASS_INFO_H
#define COMMONLUAAPP_CLASS_INFO_H

#include "jni.h"
#include "string"
#include "java_env.h"
#include "../common/list.h"
#include "../common/map2.h"

typedef void (*ClassInfoInitializer)(const std::string &cn);

namespace LUA_JAVA {
    class MethodInfo {
    public:
        std::string name;//method name
        std::string sig;
        List<jclass> paramTypes;

        MethodInfo() {}

        ~MethodInfo() {
            JNIEnv *const pEnv = getJNIEnv();
            for (int i = 0, size = paramTypes.size(); i < size; ++i) {
                pEnv->DeleteLocalRef(paramTypes.getAt(i));
            }
            paramTypes.clear();
        }

        const string &getName() const {
            return name;
        }
        void setName(const string &name) {
            MethodInfo::name = name;
        }

        const string &getSig() const {
            return sig;
        }
        void setSig(const string &sig) {
            MethodInfo::sig = sig;
        }

        const List<jclass> &getParameterTypes() const {
            return paramTypes;
        }
        void setParameterTypes(const List<jclass> &types) {
            MethodInfo::paramTypes = types;
        }
    };

    class ClassInfo {
    private:
        std::string className;
        Map<std::string, MethodInfo *> mMethodMap;
        Map<std::string, MethodInfo *> mConstructorMap;
        List<std::string> fieldNames;
        bool recycleMi; //recycle method info or not on de-init

    public:

        ClassInfo(bool recycleMi) {
            this->recycleMi = recycleMi;
        }

        ~ClassInfo() {
            if (recycleMi) {
                auto tr = [](const Map<std::string, MethodInfo *> *map, const std::string &key,
                        MethodInfo * const& value) {
                    delete (value);
                    return false;
                };
            }
            mConstructorMap.clear();
            mMethodMap.clear();
        }
        void addFieldName(const std::string & name){
            fieldNames.add(name);
        }
        bool hasField(const std::string& name){
            return fieldNames.contains(name);
        }
        void setClassName(const std::string &cn){
            this->className = cn;
        }
        const std::string getClassName(){
            return this->className;
        }
        void putMethodInfo(const std::string &name, MethodInfo * const& info) {
            mMethodMap.put(name, info);
        }

        void putConstructorInfo(const std::string &name, MethodInfo * const&info) {
            mConstructorMap.put(name, info);
        }

        const MethodInfo *getMethodInfo(const std::string &name) {
            return mMethodMap.get(name);
        }

        const MethodInfo *getConstructorInfo(const std::string &name) {
            return mConstructorMap.get(name);
        }
    };

    void addClassInfo(ClassInfo* const & info);
    void removeClassInfo(ClassInfo* const & info);
    void clearClassInfos();
    const ClassInfo* getClassInfo(const std::string &cn);
    const MethodInfo* getMethodInfo(const std::string &cn, const std::string &mname);
    const MethodInfo* getConstructorInfo(const std::string &cn, const std::string &mname);

    void setClassInfoInitializer(ClassInfoInitializer cii);
}

#endif //COMMONLUAAPP_CLASS_INFO_H
