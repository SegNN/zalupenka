/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.command.defaults;

import fun.kubik.itemics.api.IItemics;
import fun.kubik.itemics.api.command.Command;
import fun.kubik.itemics.api.command.argument.IArgConsumer;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class CommandAlias
extends Command {
    private final String shortDesc;
    public final String target;

    public CommandAlias(IItemics itemics, List<String> names, String shortDesc, String target) {
        super(itemics, names.toArray(new String[0]));
        this.shortDesc = shortDesc;
        this.target = target;
    }

    public CommandAlias(IItemics itemics, String name, String shortDesc, String target) {
        super(itemics, name);
        this.shortDesc = shortDesc;
        this.target = target;
    }

    @Override
    public void execute(String label, IArgConsumer args) {
        this.itemics.getCommandManager().execute(String.format("%s %s", this.target, args.rawRest()));
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return this.itemics.getCommandManager().tabComplete(String.format("%s %s", this.target, args.rawRest()));
    }

    @Override
    public String getShortDesc() {
        return this.shortDesc;
    }

    @Override
    public List<String> getLongDesc() {
        return Collections.singletonList(String.format("This command is an alias, for: %s ...", this.target));
    }
}

