package com.heaven7.java.lua.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Note that the above example is taken from AdaptAnnotationTest.
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface LuaAdapter {

  /** Either a {@link com.heaven7.java.lua.LuaTypeAdapter}*/
  Class<?> value();

}