package com.heaven7.java.lua;

public abstract class JavaValuer {

    private Object value;

    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    public abstract byte get_byte();
    public abstract int get_int();
    public abstract short get_short();
    public abstract long get_long();
    public abstract float get_float();
    public abstract float get_double();
    public abstract boolean get_boolean();
    public abstract char get_char();

    public abstract Character get_Character();
    public abstract Boolean get_Boolean();
    public abstract Float get_Float();
    public abstract Double get_Double();
    public abstract Byte get_Byte();
    public abstract Integer get_Integer();
    public abstract Short get_Short();
    public abstract Long get_Long();
}
