package com.heaven7.java.lua;

import android.support.annotation.Keep;

//TODO toArray/toList/set/map
public final class Lua2JavaValue {

    public static final int TYPE_NULL        = 1;
    public static final int TYPE_NUMBER      = 2;
    public static final int TYPE_STRING      = 3;
    public static final int TYPE_BOOLEAN     = 4;
    public static final int TYPE_TABLE_LIKE  = 5;
    public static final int TYPE_FUNCTION    = 6;

    private long ptr; //value ptr
    private int type;

    private Lua2JavaValue(int type, long ptrOrIndex){
        this.type = type;
        this.ptr = ptrOrIndex;
    }

    @Keep
    public static Lua2JavaValue of(int type, long ptrOrIndex){
        return new Lua2JavaValue(type, ptrOrIndex);
    }

    @Override
    protected void finalize() throws Throwable {
        if(getType() != TYPE_TABLE_LIKE && ptr != 0){
            switch (type){
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
        super.finalize();
    }
    @Keep
    public final int getType(){
        return type;
    }
    @Keep
    public final long getValuePtr(){
        return ptr;
    }
    public TableObject toTableValue(){
        if(getType() == TYPE_TABLE_LIKE){
            return TableObject.from((int) ptr);
        }
        throw new IllegalStateException("type" + (getType()) + " can't cast to table.");
    }
    public int toByteValue(){
        return Double.valueOf(toDoubleValue()).byteValue();
    }
    public int toIntValue(){
        return Double.valueOf(toDoubleValue()).intValue();
    }
    public short toShortValue(){
        return Double.valueOf(toDoubleValue()).shortValue();
    }
    public long toLongValue(){
        return Double.valueOf(toDoubleValue()).longValue();
    }
    public float toFloatValue(){
        return Double.valueOf(toDoubleValue()).floatValue();
    }
    public double toDoubleValue(){
        if(getType() != TYPE_NUMBER){
            throw new IllegalStateException("");
        }
        return getNumber_(ptr);
    }
    public String toStringValue(){
        String prefix;
        switch (getType()){
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
            case TYPE_STRING:{
                return getString_(ptr);
            }
            case TYPE_TABLE_LIKE:
                prefix = "table-like";
                break;

            default:
                throw new UnsupportedOperationException("unsupport type = " + getType());
        }
        throw new IllegalStateException(prefix + " value can't cast to char.");
    }
    public char toCharValue(){
        String prefix = null;
        switch (getType()){
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
            case TYPE_STRING:{
                String va = getString_(ptr);
                if(va != null && va.length() == 1){
                    return va.charAt(0);
                }
                prefix = "string";
                break;
            }
            case TYPE_TABLE_LIKE:
                prefix = "table-like";
                break;

            default:
                throw new UnsupportedOperationException("unsupport type = " + getType());
        }
        throw new IllegalStateException(prefix + " value can't cast to char.");
    }
    public boolean toBooleanValue(){
        switch (getType()){
            case TYPE_NULL:
                return false;

            case TYPE_BOOLEAN:
                return getBoolean_(ptr);

            case TYPE_NUMBER:
                double v = getNumber_(ptr);
                if(((long)v) == v){
                    return Double.valueOf(v).longValue() == 1;
                }else {
                    return false;
                }

            case TYPE_STRING:{
                String va = getString_(ptr);
                return Boolean.valueOf(va);
            }

            case TYPE_TABLE_LIKE:
                throw new IllegalStateException("table value can't cast to boolean.");

            default:
                throw new UnsupportedOperationException("unsupport type = " + getType());
        }
    }

    private static native String getString_(long ptr);
    private static native boolean getBoolean_(long ptr);
    private static native double getNumber_(long ptr);
    private static native void releaseNumber_(long ptr);
    private static native void releaseBoolean_(long ptr);
}
