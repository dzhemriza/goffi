/*
 * org.goffi.my.vault
 *
 * File Name: VaultNodeReflectiveVisitor.java
 *
 * Copyright 2018 Dzhem Riza
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.goffi.my.vault.model;

import org.goffi.my.vault.exceptions.ReflectiveVisitorException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * {@link VaultNode} visitor using reflection. You have to implement {@code
 * public void visit(DerivedFromVaultNode)} method in the derived class.
 */
public abstract class VaultNodeReflectiveVisitor {

    protected void visitDefault(VaultNode vaultNode) {
    }

    public void visit(VaultNode vaultNode) {
        try {
            Method method = this.getClass().getMethod("visit",
                    vaultNode.getClass());
            method.invoke(this, new Object[]{vaultNode});
        } catch (NoSuchMethodException e) {
            visitDefault(vaultNode);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReflectiveVisitorException(e.getMessage(), e);
        }
    }
}
