package com.heaven7.java.lua;

public class LuaFunctionProxyTest {

    public static void test1(LuaState state){
        state.pushFunction(new TestFunction());
        LuaFunctionProxy proxy = new LuaFunctionProxy(state, -1);
        proxy.execute1(new LuaParameter[]{new LuaParameter(null, "Google")}, new LuaCallback() {
            @Override
            public void onCallResult(LuaState state, String errorMsg) {
                if(errorMsg != null){
                    System.err.println(errorMsg);
                }else {
                    System.out.println("onCallResult >>> ok. " + state.toString(-1));
                }
            }
        });
    }

    private static class TestFunction extends LuaFunction{
        @Override
        protected int execute(LuaState state) {
            //set name.
            String s = state.toString(-1);
            state.pushString(s + "__heaven7");
            return 1;
        }
    }
}
