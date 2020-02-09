package com.heaven7.lua.bean;

import com.heaven7.java.lua.anno.LuaMethod;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return age == person.age &&
                Objects.equals(name, person.name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(age, name);
    }
}
