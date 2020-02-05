 /*
  * Copyright 2019
  * heaven7(donshine723@gmail.com)

  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 package com.heaven7.java.lua.iota;


 import com.heaven7.java.lua.LuaTypeAdapter;

 import java.lang.reflect.Type;
 import java.util.Map;

 /**
  * the type adapter manager
  *
  * @author heaven7
  */
 public interface ILuaTypeAdapterManager extends BasicTypeAdapterProvider{

     /**
      * get the type adapter context
      *
      * @return the type adapter context
      */
     LuaReflectyContext getReflectyContext();

     /**
      * get the type adapter for type node.
      *
      * @param genericNode  the type node
      * @return the type adapter
      */
     LuaTypeAdapter getTypeAdapter(TypeNode genericNode);

     /**
      * register type adapter
      *
      * @param type    the type
      * @param adapter the type adapter
      */
     void registerTypeAdapter(Type type, LuaTypeAdapter adapter);

     /**
      * register basic type adapter. for basic types. can include un-change type.
      *
      * @param baseType the basic type
      * @param adapter  the type adapter
      */
     void registerBasicTypeAdapter(Class<?> baseType, LuaTypeAdapter adapter);

     /**
      * get the base type adapter
      *
      * @param baseType the base type class
      * @return the base {@linkplain LuaTypeAdapter}.
      */
     LuaTypeAdapter getBasicTypeAdapter(Class<?> baseType);

     //================================================================================================
     /**
      * get the key adapter for target map class type. this often used when you want to use a self-type map.
      * can non-extend {@linkplain Map}. see {@linkplain LuaReflectyContext#isMap(Class)} and {@linkplain LuaReflectyContext#createMap(Class)}.
      * @param type the class. can be {@linkplain com.heaven7.java.base.util.SparseArrayDelegate}.
      * @return the key type adapter
      */
     LuaTypeAdapter getKeyAdapter(Class<?> type);
     /**
      * get the value adapter for target map class type. this often used when you want to use a self-type map.
      * can non-extend {@linkplain Map}. see {@linkplain LuaReflectyContext#isMap(Class)} and {@linkplain LuaReflectyContext#createMap(Class)}.
      * @param type the class . can be any class like {@linkplain Map}.
      * @return the value type adapter. or null if you haven't use self-type map.
      */
     LuaTypeAdapter getValueAdapter(Class<?> type);

     /**
      * get the element type adapter for collection class type. this often used when you want to use a self-type collection.
      * can non-extend {@linkplain java.util.Collection}. see {@linkplain LuaReflectyContext#isCollection(Class)} and {@linkplain LuaReflectyContext#createCollection(Class)}.
      * @param type the collection class. can be any similar collection. like {@linkplain java.util.Collection}
      * @return the element type adapter. or null if you haven't use self-type collection.
      */
     LuaTypeAdapter getSetElementAdapter(Class<?> type);
     LuaTypeAdapter getListElementAdapter(Class<?> type);
     LuaTypeAdapter getCollectionElementAdapter(Class<?> type);
     //========================================================================

     /**
      * create collection type adapter
      *
      * @param collectionClass  the expect collection class
      * @param component the component adapter
      * @return the type adapter
      */
     LuaTypeAdapter createCollectionTypeAdapter(Class<?> collectionClass, LuaTypeAdapter component);
     LuaTypeAdapter createSetTypeAdapter(Class<?> type, LuaTypeAdapter component);
     LuaTypeAdapter createListTypeAdapter(Class<?> type, LuaTypeAdapter component);
     /**
      * create array type adapter
      *
      * @param componentClass   the component class
      * @param componentAdapter the component adapter
      * @return the array type adapter
      */
     LuaTypeAdapter createArrayTypeAdapter(Class<?> componentClass, LuaTypeAdapter componentAdapter);

     /**
      * create map type adapter
      *
      * @param mapClazz     the map class. can be sparse array
      * @param keyAdapter   the key adapter
      * @param valueAdapter the value adapter
      * @return the map type adapter
      */
     LuaTypeAdapter createMapTypeAdapter(Class<?> mapClazz, LuaTypeAdapter keyAdapter, LuaTypeAdapter valueAdapter);

     /**
      * create the object type adapter
      *
      * @param objectClazz  the object class. not collection or map.
      * @return the type adapter
      */
     LuaTypeAdapter createObjectTypeAdapter(Class<?> objectClazz);





 }
