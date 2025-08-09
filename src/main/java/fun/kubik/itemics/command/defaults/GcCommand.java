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

public class GcCommand
extends Command {
    public GcCommand(IItemics itemics) {
        super(itemics, "gc");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(0);
        System.gc();
        this.logDirect("ok called System.gc()");
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Call System.gc()";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Calls System.gc().", "", "Usage:", "> gc");
    }
}

