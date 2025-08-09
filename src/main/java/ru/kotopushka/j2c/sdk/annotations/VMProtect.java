/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package ru.kotopushka.j2c.sdk.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import ru.kotopushka.j2c.sdk.enums.VMProtectType;

@Retention(value=RetentionPolicy.RUNTIME)
public @interface VMProtect {
    public VMProtectType type();
}

