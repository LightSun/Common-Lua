package com.heaven7.java.lua.internal;

import android.support.annotation.RestrictTo;

import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.lua.LuaTypeAdapter;
import com.heaven7.java.lua.iota.LuaReflectyContext;
import com.heaven7.java.lua.iota.ILuaTypeAdapterManager;
import com.heaven7.java.lua.iota.TypeNode;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author heaven7
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class IotaUtils {

    /**
     * get the type adapter from target type node and type adapter manager delegate.
     * @param node the type node
     * @param delegate the type adapter manager delegate
     * @return the type adapter
     */
    public static LuaTypeAdapter getTypeAdapter(TypeNode node, ILuaTypeAdapterManager delegate){
        if(node.isArray()){
            TypeNode subNode = node.getSubNode(0);
            return delegate.createArrayTypeAdapter(subNode.getTypeClass(0),
                    getTypeAdapter(subNode, delegate));
        }
        LuaReflectyContext context = delegate.getReflectyContext();
        Class<?> type = node.getRawClass();
        if(type != null){
            //dynamic register
            LuaTypeAdapter typeAdapter = delegate.getTypeAdapter(node);
            if(typeAdapter != null){
                return typeAdapter;
            }
            //base types
            LuaTypeAdapter adapter = delegate.getBasicTypeAdapter(type);
            if(adapter != null){
                return adapter;
            }
            if(Set.class.isAssignableFrom(type) || context.isSet(type)){
                LuaTypeAdapter ta;
                if(node.getSubNodeCount() == 0){
                    ta = delegate.getSetElementAdapter(type);
                }else {
                    ta = getTypeAdapter(node.getSubNode(0), delegate);
                }
                if(ta == null){
                    throw new IllegalStateException("can't find target element adapter for collection class = " + type.getName());
                }
                return delegate.createSetTypeAdapter(type, ta);

            }else if(List.class.isAssignableFrom(type) || context.isList(type)){
                LuaTypeAdapter ta;
                if(node.getSubNodeCount() == 0){
                    ta = delegate.getListElementAdapter(type);
                }else {
                    ta = getTypeAdapter(node.getSubNode(0), delegate);
                }
                if(ta == null){
                    throw new IllegalStateException("can't find target element adapter for collection class = " + type.getName());
                }
                return delegate.createListTypeAdapter(type, ta);
            }else if(Collection.class.isAssignableFrom(type) || context.isCollection(type)){
                LuaTypeAdapter ta;
                if(node.getSubNodeCount() == 0){
                    ta = delegate.getCollectionElementAdapter(type);
                }else {
                    ta = getTypeAdapter(node.getSubNode(0), delegate);
                }
                if(ta == null){
                    throw new IllegalStateException("can't find target element adapter for collection class = " + type.getName());
                }
                return delegate.createCollectionTypeAdapter(type, ta);

            }else if(Map.class.isAssignableFrom(type) || context.isMap(type)){
                LuaTypeAdapter key, value;

                int count = node.getSubNodeCount();
                switch (count){
                    case 0:
                        key = delegate.getKeyAdapter(type);
                        value = delegate.getValueAdapter(type);
                        break;
                    //often key type can be fixed. but value type not. like sparse array.
                    case 1:
                        key = delegate.getKeyAdapter(type);
                        value = getTypeAdapter(node.getSubNode(0), delegate);
                        break;

                    case 2:
                        key = getTypeAdapter(node.getSubNode(0), delegate);
                        value = getTypeAdapter(node.getSubNode(1), delegate);
                        break;

                    default:
                        throw new UnsupportedOperationException("sub node count for map must <= 2. but is " + count);
                }
                if(key == null){
                    throw new IllegalStateException("can't find target key adapter for map class = " + type.getName());
                }
                if(value == null){
                    throw new IllegalStateException("can't find target value adapter for map class = " + type.getName());
                }
                return delegate.createMapTypeAdapter(type, key, value);
            }else {
                return delegate.createObjectTypeAdapter(type);
            }
        }else {
            List<TypeNode> nodes = node.getVariableNodes();
            if(!Predicates.isEmpty(nodes)){
                return getTypeAdapter(nodes.get(0), delegate);
            }
            throw new UnsupportedOperationException("un-reach here");
        }
    }
}
