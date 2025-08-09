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

public class SchematicaCommand
extends Command {
    public SchematicaCommand(IItemics itemics) {
        super(itemics, "schematica");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(0);
        this.itemics.getBuilderProcess().buildOpenSchematic();
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Builds the loaded schematic";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Builds the schematic currently open in Schematica.", "", "Usage:", "> schematica");
    }
}

