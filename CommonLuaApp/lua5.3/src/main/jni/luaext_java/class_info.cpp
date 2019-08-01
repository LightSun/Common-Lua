//
// Created by Administrator on 2019/8/1.
//

#include "class_info.h"

Map<std::string, LUA_JAVA::ClassInfo*> _classMap;
ClassInfoInitializer _cii = nullptr;

namespace LUA_JAVA {

    const ClassInfo* getClassInfoInternal(const std::string &cn){
        const ClassInfo* info = _classMap.get(cn);
        if(info == nullptr){
            if(_cii != nullptr){
                _cii(cn);
            }
            return _classMap.get(cn);
        }
        return nullptr;
    }

    void addClassInfo(ClassInfo* const & info){
        _classMap.put(info->getClassName(), info);
    }
    void removeClassInfo(ClassInfo* const & info){
        _classMap.remove(info->getClassName());
    }
    void clearClassInfos(){
        _classMap.clear();
    }
    const ClassInfo* getClassInfo(const std::string &cn){
        return _classMap.get(cn);
    }
    const MethodInfo* getMethodInfo(const std::string &cn, const std::string &mname){
        ClassInfo* info = const_cast<ClassInfo *>(getClassInfoInternal(cn));
        if(info == nullptr){
            return nullptr;
        }
        return info->getMethodInfo(mname);
    }
    const MethodInfo* getConstructorInfo(const std::string &cn, const std::string &mname){
        ClassInfo* info = const_cast<ClassInfo *>(getClassInfoInternal(cn));
        if(info == nullptr){
            return nullptr;
        }
        return info->getConstructorInfo(mname);
    }
    void setClassInfoInitializer(ClassInfoInitializer cii){
        _cii = cii;
    }
}
