/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.command.datatypes;

import fun.kubik.itemics.api.IItemics;
import fun.kubik.itemics.api.command.argument.IArgConsumer;

public interface IDatatypeContext {
    public IItemics getItemics();

    public IArgConsumer getConsumer();
}

