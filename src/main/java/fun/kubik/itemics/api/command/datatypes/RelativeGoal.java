/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.command.datatypes;

import fun.kubik.itemics.api.command.argument.IArgConsumer;
import fun.kubik.itemics.api.command.exception.CommandException;
import fun.kubik.itemics.api.pathing.goals.Goal;
import fun.kubik.itemics.api.pathing.goals.GoalBlock;
import fun.kubik.itemics.api.pathing.goals.GoalXZ;
import fun.kubik.itemics.api.pathing.goals.GoalYLevel;
import fun.kubik.itemics.api.utils.BetterBlockPos;
import java.util.stream.Stream;

public enum RelativeGoal implements IDatatypePost<Goal, BetterBlockPos>
{
    INSTANCE;


    @Override
    public Goal apply(IDatatypeContext ctx, BetterBlockPos origin) throws CommandException {
        IArgConsumer consumer;
        GoalBlock goalBlock;
        if (origin == null) {
            origin = BetterBlockPos.ORIGIN;
        }
        if ((goalBlock = (GoalBlock)(consumer = ctx.getConsumer()).peekDatatypePostOrNull(RelativeGoalBlock.INSTANCE, origin)) != null) {
            return goalBlock;
        }
        GoalXZ goalXZ = (GoalXZ)consumer.peekDatatypePostOrNull(RelativeGoalXZ.INSTANCE, origin);
        if (goalXZ != null) {
            return goalXZ;
        }
        GoalYLevel goalYLevel = (GoalYLevel)consumer.peekDatatypePostOrNull(RelativeGoalYLevel.INSTANCE, origin);
        if (goalYLevel != null) {
            return goalYLevel;
        }
        return new GoalBlock(origin);
    }

    @Override
    public Stream<String> tabComplete(IDatatypeContext ctx) {
        return ctx.getConsumer().tabCompleteDatatype(RelativeCoordinate.INSTANCE);
    }
}

