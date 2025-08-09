/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.command.datatypes;

import fun.kubik.itemics.api.command.exception.CommandException;
import fun.kubik.itemics.api.command.helpers.TabCompleteHelper;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.util.Direction;

public enum ForDirection implements IDatatypeFor<Direction>
{
    INSTANCE;


    @Override
    public Direction get(IDatatypeContext ctx) throws CommandException {
        return Direction.valueOf(ctx.getConsumer().getString().toUpperCase(Locale.US));
    }

    @Override
    public Stream<String> tabComplete(IDatatypeContext ctx) throws CommandException {
        return new TabCompleteHelper().append(Stream.of(Direction.values()).map(Direction::getName2).map(String::toLowerCase)).filterPrefix(ctx.getConsumer().getString()).stream();
    }
}

