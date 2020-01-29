package com.heaven7.java.lua;

import android.support.annotation.Keep;

public interface INativeObject {

    /**
     * get the native pointer, this is called in native.
     * @return the native pointer
     */
    @Keep
    long getNativePointer();

    /**
     * this method should called in 'Object.finalize()'.
     */
    void destroyNative();

    abstract class BaseNativeObject implements INativeObject{

        private long ptr;
        private final boolean createNative;

        public BaseNativeObject(long ptr) {
            if(ptr == 0){
                onPreCreate();
                this.ptr = nCreate();
                createNative = true;
            }else {
                this.ptr = ptr;
                createNative = false;
            }
        }
        @Override
        public long getNativePointer() {
            return ptr;
        }
        @Override @Keep
        public void destroyNative() {
            if(ptr != 0){
                nRelease(ptr);
                ptr = 0;
            }
        }
        @Override
        protected void finalize() throws Throwable {
            if(destroyNativeOnRecycle()){
                destroyNative();
            }
            super.finalize();
        }

        /**
         * called before {@linkplain #nCreate()}.
         */
        protected void onPreCreate(){}

        /**
         * called on create native object
         * @return the pointer of native
         */
        protected abstract long nCreate();

        /**
         * called on release native object
         * @param ptr the pointer of native
         */
        protected abstract void nRelease(long ptr);

        protected boolean destroyNativeOnRecycle(){
            return createNative;
        }
    }
}
