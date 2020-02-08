package com.heaven7.java.lua.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LuaExpose {
  
  /**
   * If {@code true}, the field marked with this annotation is written out in the JSON while
   * serializing. If {@code false}, the field marked with this annotation is skipped from the
   * serialized output. Defaults to {@code true}.
   * @since 1.4
   */
   boolean serialize() default true;

  /**
   * If {@code true}, the field marked with this annotation is deserialized from the JSON.
   * If {@code false}, the field marked with this annotation is skipped during deserialization. 
   * Defaults to {@code true}.
   * @since 1.4
   */
   boolean deserialize() default true;
}