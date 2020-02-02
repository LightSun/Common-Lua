package com.heaven7.java.lua;

import android.support.annotation.Keep;

@Keep
public abstract class LuaTraveller {

    public static final int COLLECTION_TYPE_LIST = 1;
    public static final int COLLECTION_TYPE_SET  = 2;
    public static final int COLLECTION_TYPE_MAP  = 3;

    private int collectionType;

    public int getCollectionType() {
        return collectionType;
    }
    public void setCollectionType(int collectionType) {
        this.collectionType = collectionType;
    }

    /**
     * travel lua 'table-like'
     * @param luaStatePte the lua pointer of lua state
     * @param key the lua value as key of table
     * @param value the lua value as value of table
     * @return 0 if ok. or 1 if should break next travel.
     */
    @Keep
    public abstract int travel(long luaStatePte, Lua2JavaValue key, Lua2JavaValue value);

}
