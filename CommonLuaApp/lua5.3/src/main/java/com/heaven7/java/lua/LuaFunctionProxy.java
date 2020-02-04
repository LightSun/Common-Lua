package com.heaven7.java.lua;

import com.heaven7.java.lua.convertors.TypeConvertorFactory;
import com.heaven7.java.lua.internal.LuaUtils;

/**
 * the lua function proxy
 */
public final class LuaFunctionProxy {

    private final LuaState mState;
    private final int mFuncIdx;

    /*public*/ LuaFunctionProxy(LuaState mState, int func_idx) {
        this.mState = mState;
        this.mFuncIdx = func_idx;
    }
    /**
     * execute the function by target parameters.
     * @param args the params to push to stack
     * @param resultCount the result count of function
     * @param cb the callback of receive result or error msg
     * @return the result count.
     */
    public final int execute(Object[] args, int resultCount, LuaCallback cb){
        //adjust to positive
        int func_idx = LuaUtils.adjustIdx(mState, mFuncIdx);
        final int k = mState.saveLightly();
        // push error
        mState.pushFunction(new ErrorFunction(cb));
        final int errFunc = mState.getTop();
        //push func
        mState.pushValue(func_idx);
        //push args
        int pCount = args != null ? args.length : 0;
        if(pCount > 0){
            for (Object obj : args){
                TypeConvertorFactory.getTypeConvertor(obj.getClass())
                        .java2lua(mState, obj);
            }
        }
        int result = mState.pcall(pCount, resultCount, errFunc);
        if(result != 0){
            //error
            String msg = mState.toString(-1);
            cb.onCallResult(mState, msg);
        }else {
            cb.onCallResult(mState, null);
        }
        mState.restoreLightly(k);
        return resultCount;
    }

    private static class ErrorFunction extends LuaFunction{
        final LuaCallback callback;
        public ErrorFunction(LuaCallback callback) {
            this.callback = callback;
        }
        @Override
        protected int execute(LuaState state) {
            String s = state.toString(-1);
            callback.onCallResult(state, s);
            return 0;
        }
    }
}
