/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class TypeUtils {
    private TypeUtils() {
    }

    public static Class<?> resolveBaseClass(Type type) {
        return type instanceof Class ? (Class)type : (type instanceof ParameterizedType ? (Class)((ParameterizedType)type).getRawType() : null);
    }
}

