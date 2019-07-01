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
     * this method should called in {@linkplain Object#finalize()}.
     */
    void destroyNative();

    abstract class BaseNativeObject implements INativeObject{

        private long ptr;

        public BaseNativeObject() {
            onPreCreate();
            this.ptr = nCreate();
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
            destroyNative();
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
    }
}
