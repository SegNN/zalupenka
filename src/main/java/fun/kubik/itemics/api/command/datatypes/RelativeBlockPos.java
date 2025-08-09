/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.command.datatypes;

import fun.kubik.itemics.api.command.argument.IArgConsumer;
import fun.kubik.itemics.api.command.exception.CommandException;
import fun.kubik.itemics.api.utils.BetterBlockPos;
import java.util.stream.Stream;

public enum RelativeBlockPos implements IDatatypePost<BetterBlockPos, BetterBlockPos>
{
    INSTANCE;


    @Override
    public BetterBlockPos apply(IDatatypeContext ctx, BetterBlockPos origin) throws CommandException {
        if (origin == null) {
            origin = BetterBlockPos.ORIGIN;
        }
        IArgConsumer consumer = ctx.getConsumer();
        return new BetterBlockPos((Double)consumer.getDatatypePost(RelativeCoordinate.INSTANCE, Double.valueOf(origin.x)), (Double)consumer.getDatatypePost(RelativeCoordinate.INSTANCE, Double.valueOf(origin.y)), (Double)consumer.getDatatypePost(RelativeCoordinate.INSTANCE, Double.valueOf(origin.z)));
    }

    @Override
    public Stream<String> tabComplete(IDatatypeContext ctx) throws CommandException {
        IArgConsumer consumer = ctx.getConsumer();
        if (consumer.hasAny() && !consumer.has(4)) {
            while (consumer.has(2) && consumer.peekDatatypeOrNull(RelativeCoordinate.INSTANCE) != null) {
                consumer.get();
            }
            return consumer.tabCompleteDatatype(RelativeCoordinate.INSTANCE);
        }
        return Stream.empty();
    }
}

