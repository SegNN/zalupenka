/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.command.defaults;

import fun.kubik.itemics.api.IItemics;
import fun.kubik.itemics.api.behavior.IPathingBehavior;
import fun.kubik.itemics.api.command.Command;
import fun.kubik.itemics.api.command.argument.IArgConsumer;
import fun.kubik.itemics.api.command.exception.CommandException;
import fun.kubik.itemics.api.command.exception.CommandInvalidStateException;
import fun.kubik.itemics.api.pathing.calc.IPathingControlManager;
import fun.kubik.itemics.api.process.IItemicsProcess;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ETACommand
extends Command {
    public ETACommand(IItemics itemics) {
        super(itemics, "eta");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(0);
        IPathingControlManager pathingControlManager = this.itemics.getPathingControlManager();
        IItemicsProcess process = pathingControlManager.mostRecentInControl().orElse(null);
        if (process == null) {
            throw new CommandInvalidStateException("No process in control");
        }
        IPathingBehavior pathingBehavior = this.itemics.getPathingBehavior();
        double ticksRemainingInSegment = pathingBehavior.ticksRemainingInSegment().orElse(Double.NaN);
        double ticksRemainingInGoal = pathingBehavior.estimatedTicksToGoal().orElse(Double.NaN);
        this.logDirect(String.format("Next segment: %.1fs (%.0f ticks)\nGoal: %.1fs (%.0f ticks)", ticksRemainingInSegment / 20.0, ticksRemainingInSegment, ticksRemainingInGoal / 20.0, ticksRemainingInGoal));
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "View the current ETA";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("The ETA command provides information about the estimated time until the next segment.", "and the goal", "", "Be aware that the ETA to your goal is really unprecise", "", "Usage:", "> eta - View ETA, if present");
    }
}

