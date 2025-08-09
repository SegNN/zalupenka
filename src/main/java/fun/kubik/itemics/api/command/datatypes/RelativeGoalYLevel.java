/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.command.datatypes;

import fun.kubik.itemics.api.command.argument.IArgConsumer;
import fun.kubik.itemics.api.command.exception.CommandException;
import fun.kubik.itemics.api.pathing.goals.GoalYLevel;
import fun.kubik.itemics.api.utils.BetterBlockPos;
import java.util.stream.Stream;
import net.minecraft.util.math.MathHelper;

public enum RelativeGoalYLevel implements IDatatypePost<GoalYLevel, BetterBlockPos>
{
    INSTANCE;


    @Override
    public GoalYLevel apply(IDatatypeContext ctx, BetterBlockPos origin) throws CommandException {
        if (origin == null) {
            origin = BetterBlockPos.ORIGIN;
        }
        return new GoalYLevel(MathHelper.floor((Double)ctx.getConsumer().getDatatypePost(RelativeCoordinate.INSTANCE, Double.valueOf(origin.y))));
    }

    @Override
    public Stream<String> tabComplete(IDatatypeContext ctx) {
        IArgConsumer consumer = ctx.getConsumer();
        if (consumer.hasAtMost(1)) {
            return consumer.tabCompleteDatatype(RelativeCoordinate.INSTANCE);
        }
        return Stream.empty();
    }
}

