/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.command.datatypes;

import fun.kubik.itemics.api.command.exception.CommandException;
import java.util.stream.Stream;

public interface IDatatype {
    public Stream<String> tabComplete(IDatatypeContext var1) throws CommandException;
}

