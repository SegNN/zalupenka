/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.utils;

import fun.kubik.itemics.api.pathing.goals.Goal;
import fun.kubik.itemics.api.process.PathingCommand;
import fun.kubik.itemics.api.process.PathingCommandType;
import fun.kubik.itemics.pathing.movement.CalculationContext;

public class PathingCommandContext
extends PathingCommand {
    public final CalculationContext desiredCalcContext;

    public PathingCommandContext(Goal goal, PathingCommandType commandType, CalculationContext context) {
        super(goal, commandType);
        this.desiredCalcContext = context;
    }
}

