/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.command.datatypes;

import fun.kubik.itemics.api.command.exception.CommandException;

public interface IDatatypePostFunction<T, O> {
    public T apply(O var1) throws CommandException;
}

