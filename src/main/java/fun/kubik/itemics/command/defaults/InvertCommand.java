/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.command.defaults;

import fun.kubik.itemics.api.IItemics;
import fun.kubik.itemics.api.command.Command;
import fun.kubik.itemics.api.command.argument.IArgConsumer;
import fun.kubik.itemics.api.command.exception.CommandException;
import fun.kubik.itemics.api.command.exception.CommandInvalidStateException;
import fun.kubik.itemics.api.pathing.goals.Goal;
import fun.kubik.itemics.api.pathing.goals.GoalInverted;
import fun.kubik.itemics.api.process.ICustomGoalProcess;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class InvertCommand
extends Command {
    public InvertCommand(IItemics itemics) {
        super(itemics, "invert");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(0);
        ICustomGoalProcess customGoalProcess = this.itemics.getCustomGoalProcess();
        Goal goal = customGoalProcess.getGoal();
        if (goal == null) {
            throw new CommandInvalidStateException("No goal");
        }
        goal = goal instanceof GoalInverted ? ((GoalInverted)goal).origin : new GoalInverted(goal);
        customGoalProcess.setGoalAndPath(goal);
        this.logDirect(String.format("Goal: %s", goal.toString()));
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Run away from the current goal";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("The invert command tells Itemics to head away from the current goal rather than towards it.", "", "Usage:", "> invert - Invert the current goal.");
    }
}

