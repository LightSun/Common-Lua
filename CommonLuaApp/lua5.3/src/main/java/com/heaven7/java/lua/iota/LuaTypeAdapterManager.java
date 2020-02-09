package com.heaven7.java.lua.iota;

import com.heaven7.java.lua.LuaTypeAdapter;
import com.heaven7.java.lua.adapter.ArrayLuaTypeAdapter;
import com.heaven7.java.lua.adapter.BooleanLuaTypeAdapter;
import com.heaven7.java.lua.adapter.ByteLuaTypeAdapter;
import com.heaven7.java.lua.adapter.CharLuaTypeAdapter;
import com.heaven7.java.lua.adapter.CollectionLuaTypeAdapter;
import com.heaven7.java.lua.adapter.DoubleLuaTypeAdapter;
import com.heaven7.java.lua.adapter.FloatLuaTypeAdapter;
import com.heaven7.java.lua.adapter.IntLuaTypeAdapter;
import com.heaven7.java.lua.adapter.ListLuaTypeAdapter;
import com.heaven7.java.lua.adapter.LongLuaTypeAdapter;
import com.heaven7.java.lua.adapter.MapLuaTypeAdapter;
import com.heaven7.java.lua.adapter.ObjectLuaTypeAdapter;
import com.heaven7.java.lua.adapter.SetLuaTypeAdapter;
import com.heaven7.java.lua.adapter.ShortLuaTypeAdapter;
import com.heaven7.java.lua.adapter.StringLuaTypeAdapter;
import com.heaven7.java.lua.internal.$ReflectyTypes;
import com.heaven7.java.lua.iota.obj.Reflecty;
import com.heaven7.java.lua.iota.plugins.SparseArrayIotaPlugin;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * the lua type adapter manager
 * @author heaven7
 */
public class LuaTypeAdapterManager implements ILuaTypeAdapterManager {

    private static final Map<TypeNode, LuaTypeAdapter> sBaseAdapters = new HashMap<>();

    private final Map<TypeNode, LuaTypeAdapter> mAdapterMap = new HashMap<>();
    private final IotaPluginManager mIotaPM = new IotaPluginManager(this);
    private final LuaReflectyContext mContext;

    private final Reflecty<?, ?, ?, ?, ?> mReflecty;

    public LuaTypeAdapterManager(LuaReflectyContext context,
                                 Reflecty<? extends LuaTypeAdapter, ? extends Annotation, ? extends Annotation, ? extends Annotation, ? extends Annotation> reflecty
    ) {
        this.mContext = new GroupLuaReflectyContext(mIotaPM, context);
        this.mReflecty = reflecty;
        registerCoreIotaPlugins();
    }
    protected void registerCoreIotaPlugins(){
        mIotaPM.addIotaPlugin(new SparseArrayIotaPlugin());
    }
    public void addIotaPlugin(IotaPlugin plugin){
        mIotaPM.addIotaPlugin(plugin);
    }
    @Override
    public LuaReflectyContext getReflectyContext() {
        return mContext;
    }
    @Override
    public LuaTypeAdapter getTypeAdapter(TypeNode genericNode) {
        LuaTypeAdapter ta = sBaseAdapters.get(genericNode);
        if(ta != null){
            return ta;
        }
        return mAdapterMap.get(genericNode);
    }
    @Override
    public void registerTypeAdapter(Type type, LuaTypeAdapter adapter) {
        mAdapterMap.put($ReflectyTypes.getTypeNode(type), adapter);
    }
    @Override
    public void registerBasicTypeAdapter(Class<?> baseType, LuaTypeAdapter adapter) {

    }
    @Override
    public LuaTypeAdapter getBasicTypeAdapter(Class<?> baseType) {
        return sBaseAdapters.get($ReflectyTypes.getTypeNode(baseType));
    }

    @Override
    public LuaTypeAdapter getKeyAdapter(Class<?> type) {
        return mIotaPM.getKeyAdapter(type);
    }
    @Override
    public LuaTypeAdapter getValueAdapter(Class<?> type) {
        return mIotaPM.getValueAdapter(type);
    }
    @Override
    public LuaTypeAdapter getSetElementAdapter(Class<?> type) {
        return mIotaPM.getSetElementAdapter(type);
    }
    @Override
    public LuaTypeAdapter getListElementAdapter(Class<?> type) {
        return mIotaPM.getListElementAdapter(type);
    }
    @Override
    public LuaTypeAdapter getCollectionElementAdapter(Class<?> type) {
        return mIotaPM.getCollectionElementAdapter(type);
    }
    @Override
    public LuaTypeAdapter createCollectionTypeAdapter(Class<?> collectionClass, LuaTypeAdapter component) {
        return new CollectionLuaTypeAdapter(mContext, collectionClass, component);
    }
    @Override
    public LuaTypeAdapter createSetTypeAdapter(Class<?> type, LuaTypeAdapter component) {
        return new SetLuaTypeAdapter(mContext, type, component);
    }
    @Override
    public LuaTypeAdapter createListTypeAdapter(Class<?> type, LuaTypeAdapter component) {
        return new ListLuaTypeAdapter(mContext, type, component);
    }
    @Override
    public LuaTypeAdapter createArrayTypeAdapter(Class<?> type, LuaTypeAdapter componentAdapter) {
        return new ArrayLuaTypeAdapter(type, componentAdapter);
    }
    @Override
    public LuaTypeAdapter createMapTypeAdapter(Class<?> mapClazz, LuaTypeAdapter keyAdapter, LuaTypeAdapter valueAdapter) {
        return new MapLuaTypeAdapter(mContext, mapClazz, keyAdapter, valueAdapter);
    }
    @Override @SuppressWarnings("unchecked")
    public LuaTypeAdapter createObjectTypeAdapter(Class<?> objectClazz) {
        return new ObjectLuaTypeAdapter(mReflecty, this, objectClazz);
    }

    private static void putBaseTypeAdapter(Class<?> clazz, LuaTypeAdapter ta){
        sBaseAdapters.put($ReflectyTypes.getTypeNode(clazz), ta);
    }
    static {
        LuaTypeAdapter convertor = new BooleanLuaTypeAdapter();
        putBaseTypeAdapter(boolean.class, convertor);
        putBaseTypeAdapter(Boolean.class, convertor);

        convertor = new ByteLuaTypeAdapter();
        putBaseTypeAdapter(byte.class, convertor);
        putBaseTypeAdapter(Byte.class, convertor);

        convertor = new CharLuaTypeAdapter();
        putBaseTypeAdapter(char.class, convertor);
        putBaseTypeAdapter(Character.class, convertor);

        convertor = new ShortLuaTypeAdapter();
        putBaseTypeAdapter(short.class, convertor);
        putBaseTypeAdapter(Short.class, convertor);

        convertor = new IntLuaTypeAdapter();
        putBaseTypeAdapter(int.class, convertor);
        putBaseTypeAdapter(Integer.class, convertor);

        convertor = new LongLuaTypeAdapter();
        putBaseTypeAdapter(long.class, convertor);
        putBaseTypeAdapter(Long.class, convertor);

        convertor = new FloatLuaTypeAdapter();
        putBaseTypeAdapter(float.class, convertor);
        putBaseTypeAdapter(Float.class, convertor);

        convertor = new DoubleLuaTypeAdapter();
        putBaseTypeAdapter(double.class, convertor);
        putBaseTypeAdapter(Double.class, convertor);

        putBaseTypeAdapter(String.class, new StringLuaTypeAdapter());
    }
}
