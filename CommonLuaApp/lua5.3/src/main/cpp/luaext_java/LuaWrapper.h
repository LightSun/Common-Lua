//
// Created by Administrator on 2019/7/1.
//

#ifndef COMMONLUAAPP_LUASEARCHERM_H
#define COMMONLUAAPP_LUASEARCHERM_H

class WeakObjectM {

protected:
    jweak weak;

public:
    /** set the object to weak reference */
    void setRefObject(jobject obj) {
        weak = getJNIEnv()->NewWeakGlobalRef(obj);
    }

    /**
     * after call this you should call env->DeleteLocalRef().
     * */
    jobject getRefObject() {
        if (weak == NULL) {
            return NULL;
        }
        return getJNIEnv()->NewLocalRef(weak);
    }

    void deleteWeakObject() {
        if (weak != NULL) {
            getJNIEnv()->DeleteWeakGlobalRef(weak);
            weak = NULL;
        }
    }
};



#endif //COMMONLUAAPP_LUASEARCHERM_H
