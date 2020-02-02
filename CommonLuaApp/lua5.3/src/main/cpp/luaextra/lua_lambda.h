//
// Created by Administrator on 2020/2/2 0002.
//

#ifndef COMMONLUAAPP_LAMBDA_H
#define COMMONLUAAPP_LAMBDA_H

#include "../lua/lua.hpp"
#include "lua_extra.h"


template<typename TLambda>
class LambdaWrapper {
public:
    static TLambda *pFuncPtr;

    static int execute(lua_State *L) {
        return (*pFuncPtr)(L);
    }
};

template<typename TLambda>
TLambda *LambdaWrapper<TLambda>::pFuncPtr = NULL;

/**
 * transform lambda to lua stand function pointer.
 * because when lambda use 'capture-local-variable' it can't be cast to 'c-function-pointer', as it is a c++ object.
 * or you may use 'std:function<$return(...$params)>'
 * @tparam TLambda the lambda type
 * @param rFunc the lambda to transform
 * @return the lua stand function
 */
template<typename TLambda>
LuaStdFunc luaTransform(TLambda rFunc)
{
    static TLambda lf(rFunc);
    LambdaWrapper<TLambda>::pFuncPtr = &lf;
    return &LambdaWrapper<TLambda>::execute;
}

#endif //COMMONLUAAPP_LAMBDA_H
