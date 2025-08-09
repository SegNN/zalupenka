/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.command.datatypes;

import fun.kubik.itemics.api.command.argument.IArgConsumer;
import fun.kubik.itemics.api.command.exception.CommandException;
import fun.kubik.itemics.api.pathing.goals.GoalBlock;
import fun.kubik.itemics.api.utils.BetterBlockPos;
import java.util.stream.Stream;
import net.minecraft.util.math.MathHelper;

public enum RelativeGoalBlock implements IDatatypePost<GoalBlock, BetterBlockPos>
{
    INSTANCE;


    @Override
    public GoalBlock apply(IDatatypeContext ctx, BetterBlockPos origin) throws CommandException {
        if (origin == null) {
            origin = BetterBlockPos.ORIGIN;
        }
        IArgConsumer consumer = ctx.getConsumer();
        return new GoalBlock(MathHelper.floor((Double)consumer.getDatatypePost(RelativeCoordinate.INSTANCE, Double.valueOf(origin.x))), MathHelper.floor((Double)consumer.getDatatypePost(RelativeCoordinate.INSTANCE, Double.valueOf(origin.y))), MathHelper.floor((Double)consumer.getDatatypePost(RelativeCoordinate.INSTANCE, Double.valueOf(origin.z))));
    }

    @Override
    public Stream<String> tabComplete(IDatatypeContext ctx) {
        IArgConsumer consumer = ctx.getConsumer();
        if (consumer.hasAtMost(3)) {
            return consumer.tabCompleteDatatype(RelativeCoordinate.INSTANCE);
        }
        return Stream.empty();
    }
}

