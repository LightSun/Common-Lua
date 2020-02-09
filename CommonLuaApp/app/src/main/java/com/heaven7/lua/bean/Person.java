package com.heaven7.lua.bean;

import com.heaven7.java.lua.anno.LuaMethod;

public final class Person {

    public static final int A1 = 100;

    @LuaMethod("getH")
    public static String getA(String name){
        return "Person_" + name;
    }

    private int age;
    private String name;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
