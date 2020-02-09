package com.heaven7.java.lua.adapter;

import com.heaven7.java.lua.LuaValue;
import com.heaven7.java.lua.LuaState;
import com.heaven7.java.lua.LuaTypeAdapter;
import com.heaven7.java.lua.TableObject;
import com.heaven7.java.lua.internal.LuaUtils;
import com.heaven7.java.lua.iota.ILuaTypeAdapterManager;
import com.heaven7.java.lua.iota.obj.FieldProxy;
import com.heaven7.java.lua.iota.obj.MemberProxy;
import com.heaven7.java.lua.iota.obj.Reflecty;

import java.lang.annotation.Annotation;
import java.util.List;

import static com.heaven7.java.lua.internal.IotaUtils.getTypeAdapter;


public final class ObjectLuaTypeAdapter<PR extends LuaTypeAdapter,
        CD extends Annotation,F extends Annotation,
        M extends Annotation, I extends Annotation> extends LuaTypeAdapter {

    private final ILuaTypeAdapterManager mTAM;
    private final Class<?> mClazz;
    private final Reflecty<PR, CD, F, M, I> mReflecty;

    public ObjectLuaTypeAdapter(Reflecty<PR, CD ,F , M , I> mReflecty, ILuaTypeAdapterManager mTAM, Class<?> mClazz) {
        this.mReflecty = mReflecty;
        this.mTAM = mTAM;
        this.mClazz = mClazz;
    }

    public Object readFromLua(LuaState luaState, LuaValue arg){
        PR ta = mReflecty.performReflectClass(mClazz);
        if(ta != null) {
            return ta.readFromLua(luaState, arg);
        }
        TableObject tab = arg.toTableValue(luaState);
        final Object obj = mTAM.getReflectyContext().createObject(mClazz);

        List<MemberProxy> proxies = mReflecty.getMemberProxies(mClazz);
        LuaValue tempVal;
        try {
            for (MemberProxy proxy : proxies){
                if(proxy instanceof FieldProxy){
                    tempVal = tab.getField(proxy.getPropertyName());
                }else {
                    tempVal = tab.call1(proxy.getPropertyName());
                }
                if(tempVal != null){
                    LuaTypeAdapter lta = getTypeAdapter(proxy.getTypeNode(), mTAM);
                    proxy.setValue(obj, lta.readFromLua(luaState, tempVal));
                    tempVal.recycle();
                }
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return obj;
    }
    @Override
    public int writeToLua(LuaState luaState, Object result) {
        //luaState.push(result);
        PR ta = mReflecty.performReflectClass(mClazz);
        if(ta != null){
            return ta.writeToLua(luaState, result);
        }else {
            luaState.newTable();
            final int top = luaState.getTop();
            //last is use member
            List<MemberProxy> proxies = mReflecty.getMemberProxies(mClazz);
            try {
                for (MemberProxy proxy : proxies){
                    luaState.pushString(proxy.getPropertyName());
                    getTypeAdapter(proxy.getTypeNode(), mTAM).writeToLua(luaState, proxy.getValue(result));
                    LuaUtils.checkTopDelta(luaState, top + 2);
                    luaState.rawSet(-3);
                }
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        return 1;
    }
}
