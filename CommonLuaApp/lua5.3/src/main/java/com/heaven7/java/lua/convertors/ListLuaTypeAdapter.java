package com.heaven7.java.lua.convertors;

import com.heaven7.java.lua.Lua2JavaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTraveller;
import com.heaven7.java.lua.LuaTypeAdapter;
import com.heaven7.java.lua.internal.LuaUtils;

import java.util.ArrayList;
import java.util.List;

public class ListLuaTypeAdapter extends LuaTypeAdapter {

    public Object lua2java(LuaState luaState, Lua2JavaValue arg){
      /*  arg.toTableValue(luaState).travel(new LuaTraveller() {
            @Override
            public int travel(long s, Lua2JavaValue key, Lua2JavaValue value) {
                return 0;
            }
        });*/
        return null;
    }

    public int java2lua(LuaState luaState, Object result){
        luaState.newTable();
        int top = luaState.getTop();
        final List list = (List) result;
        for (int i = 0, size = list.size() ; i < size ; i ++){
            Object ele = list.get(i);
            //must only add one to lua stack
            LuaUtils.java2lua(luaState, ele);
            LuaUtils.checkTopDelta(luaState, top + 1);
            luaState.rawSeti(-2, i + 1); //lua array from 1
        }
        luaState.setCollectionTypeAsMeta(-1, LuaState.COLLECTION_TYPE_LIST);
        return 1;
    }

    private static class ListTraveller extends LuaTraveller{

        final List list = new ArrayList();

        @Override
        public int travel(long luaStatePte, Lua2JavaValue key, Lua2JavaValue value) {
            switch (getCollectionType()){
                case LuaState.COLLECTION_TYPE_LIST:

                    break;
                case LuaState.COLLECTION_TYPE_SET:

                    break;

                case LuaState.COLLECTION_TYPE_MAP:
                    throw new UnsupportedOperationException("can't cast map to list.");

                default:
                    throw new UnsupportedOperationException("wrong collection type = " + getCollectionType());
            }
            return 0;
        }
    }
}