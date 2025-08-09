/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.command.defaults;

import fun.kubik.itemics.api.IItemics;
import fun.kubik.itemics.api.command.Command;
import fun.kubik.itemics.api.command.argument.IArgConsumer;
import fun.kubik.itemics.api.command.exception.CommandException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ReloadAllCommand
extends Command {
    public ReloadAllCommand(IItemics itemics) {
        super(itemics, "reloadall");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(0);
        this.ctx.worldData().getCachedWorld().reloadAllFromDisk();
        this.logDirect("Reloaded");
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Reloads Itemics's cache for this world";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("The reloadall command reloads Itemics's world cache.", "", "Usage:", "> reloadall");
    }
}

