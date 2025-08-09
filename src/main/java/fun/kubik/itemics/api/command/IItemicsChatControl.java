/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.command;

import java.util.UUID;

public interface IItemicsChatControl {
    public static final String FORCE_COMMAND_PREFIX = String.format("<<%s>>", UUID.randomUUID().toString());
}

