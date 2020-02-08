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
package com.heaven7.java.lua.iota.obj;


import com.heaven7.java.lua.internal.$ReflectyTypes;
import com.heaven7.java.lua.iota.TypeNode;

import java.lang.reflect.Type;

/**
 * @author heaven7
 */
public abstract class BaseMemberProxy implements MemberProxy {

    private final Class<?> mOwnerClass;
    private final TypeNode mNode;

    public BaseMemberProxy(Class<?> ownerClass, Type genericType) {
        this.mNode = $ReflectyTypes.getTypeNode(ownerClass, genericType);
        this.mOwnerClass = ownerClass;
    }

    @Override
    public final TypeNode getTypeNode() {
        return mNode;
    }

    @Override
    public final Class<?> getOwnerClass() {
        return mOwnerClass;
    }

}
