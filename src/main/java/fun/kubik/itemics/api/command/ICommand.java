/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.command;

import fun.kubik.itemics.api.command.argument.IArgConsumer;
import fun.kubik.itemics.api.command.exception.CommandException;
import fun.kubik.itemics.api.utils.Helper;
import java.util.List;
import java.util.stream.Stream;

public interface ICommand
extends Helper {
    public void execute(String var1, IArgConsumer var2) throws CommandException;

    public Stream<String> tabComplete(String var1, IArgConsumer var2) throws CommandException;

    public String getShortDesc();

    public List<String> getLongDesc();

    public List<String> getNames();

    default public boolean hiddenFromHelp() {
        return false;
    }
}

