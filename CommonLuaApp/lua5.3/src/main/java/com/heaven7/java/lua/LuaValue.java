package com.heaven7.java.lua;

import android.support.annotation.Keep;

public final class LuaValue {

    public static final LuaValue NULL = new LuaValue(LuaValue.TYPE_NULL, 0);

    public static final int TYPE_NULL = 1;
    public static final int TYPE_NUMBER = 2;
    public static final int TYPE_STRING = 3;
    public static final int TYPE_BOOLEAN = 4;
    public static final int TYPE_TABLE_LIKE = 5;
    public static final int TYPE_FUNCTION = 6;

    private long ptr; //value ptr or stack index
    private int type;
    private boolean used;

    private LuaValue(int type, long ptrOrIndex) {
        this.type = type;
        this.ptr = ptrOrIndex;
    }
    private LuaValue() {
    }

    @Keep
    public static LuaValue of(int type, long ptrOrIndex) {
        if (type == TYPE_NULL) {
            return NULL;
        }
        LuaValue value = obtain();
        value.type = type;
        value.ptr = ptrOrIndex;
        return value;
    }

    @Override
    protected void finalize() throws Throwable {
        recycleNative();
        super.finalize();
    }

    @Keep
    public final int getType() {
        return type;
    }

    @Keep
    public final long getValuePtr() {
        return ptr;
    }

    public LuaFunctionProxy toFunction(LuaState state) {
        if (getType() != TYPE_FUNCTION) {
            throw new IllegalStateException("toFunction(). can only called by function type.");
        }
        return new LuaFunctionProxy(state, (int) ptr);
    }

    public TableObject toTableValue(LuaState state) {
        if (getType() == TYPE_TABLE_LIKE) {
            return TableObject.from(state, (int) ptr);
        }
        throw new IllegalStateException("type" + (getType()) + " can't cast to table.");
    }

    public byte toByteValue() {
        return Double.valueOf(toDoubleValue()).byteValue();
    }

    public int toIntValue() {
        return Double.valueOf(toDoubleValue()).intValue();
    }

    public short toShortValue() {
        return Double.valueOf(toDoubleValue()).shortValue();
    }

    public long toLongValue() {
        return Double.valueOf(toDoubleValue()).longValue();
    }

    public float toFloatValue() {
        return Double.valueOf(toDoubleValue()).floatValue();
    }

    public double toDoubleValue() {
        switch (getType()) {
            case TYPE_STRING:
                return Double.valueOf(getString_(ptr));
            case TYPE_NUMBER:
                return getNumber_(ptr);
        }
        throw new IllegalStateException("type must be number");
    }

    public String toStringValue() {
        String prefix;
        switch (getType()) {
            case TYPE_NULL:
                return null;

            case TYPE_BOOLEAN:
                return String.valueOf(getBoolean_(ptr));

            case TYPE_NUMBER: {
                double v = getNumber_(ptr);
                if (((long) v) == v) {
                    return String.valueOf(Double.valueOf(v).longValue());
                } else {
                    return String.valueOf(Double.valueOf(v));
                }
            }
            case TYPE_STRING: {
                return getString_(ptr);
            }
            case TYPE_TABLE_LIKE:
                prefix = "table-like";
                break;
            case TYPE_FUNCTION:
                prefix = "function";
                break;
            default:
                throw new UnsupportedOperationException("unsupport type = " + getType());
        }
        throw new IllegalStateException(prefix + " value can't cast to char.");
    }

    public char toCharValue() {
        String prefix = null;
        switch (getType()) {
            case TYPE_NULL:
                prefix = "null";
                break;

            case TYPE_BOOLEAN:
                prefix = "boolean";
                break;

            case TYPE_NUMBER: {
                double v = getNumber_(ptr);
                if (((long) v) == v) {
                    int i = Double.valueOf(v).intValue();
                    return (char) i;
                } else {
                    prefix = "number";
                    break;
                }
            }
            case TYPE_STRING: {
                String va = getString_(ptr);
                if (va != null && va.length() == 1) {
                    return va.charAt(0);
                }
                prefix = "string";
                break;
            }
            case TYPE_TABLE_LIKE:
                prefix = "table-like";
                break;

            case TYPE_FUNCTION:
                prefix = "function";
                break;

            default:
                throw new UnsupportedOperationException("unsupport type = " + getType());
        }
        throw new IllegalStateException(prefix + " value can't cast to char.");
    }

    public boolean toBooleanValue() {
        switch (getType()) {
            case TYPE_NULL:
                return false;

            case TYPE_BOOLEAN:
                return getBoolean_(ptr);

            case TYPE_NUMBER:
                double v = getNumber_(ptr);
                if (((long) v) == v) {
                    return Double.valueOf(v).longValue() == 1;
                } else {
                    return false;
                }

            case TYPE_STRING: {
                String va = getString_(ptr);
                return Boolean.valueOf(va);
            }

            case TYPE_TABLE_LIKE:
                throw new IllegalStateException("table value can't cast to boolean.");

            case TYPE_FUNCTION:
                throw new IllegalStateException("function value can't cast to boolean.");

            default:
                throw new UnsupportedOperationException("unsupport type = " + getType());
        }
    }

    private static native String getString_(long ptr);
    private static native boolean getBoolean_(long ptr);
    private static native double getNumber_(long ptr);
    private static native void releaseNumber_(long ptr);
    private static native void releaseBoolean_(long ptr);

    //-------------------------------------- help methods ----------------------------
    public void recycle() {
        //null no need recycle
        if(type == TYPE_NULL){
            return;
        }
        if (used) {
            System.err.println("This LuaValue cannot be recycled because it "
                    + "is still in use.");
            return;
        }
        used = true;
        recycleNative();
        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }
    private static LuaValue obtain() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                LuaValue m = sPool;
                sPool = m.next;
                m.next = null;
                m.used = false; // clear in-use flag
                sPoolSize--;
                return m;
            }
        }
        return new LuaValue();
    }
    private void recycleNative(){
        if (ptr != 0) {
            switch (type) {
                case TYPE_NUMBER:
                    releaseNumber_(ptr);
                    ptr = 0;
                    break;
                case TYPE_BOOLEAN:
                    releaseBoolean_(ptr);
                    ptr = 0;
                    break;
            }
        }
    }
    private LuaValue next;
    private static final Object sPoolSync = new Object();
    private static LuaValue sPool;
    private static int sPoolSize = 0;
    private static final int MAX_POOL_SIZE = 50;
}
