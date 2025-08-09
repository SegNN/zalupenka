/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.command.defaults;

import fun.kubik.itemics.api.IItemics;
import fun.kubik.itemics.api.command.Command;
import fun.kubik.itemics.api.command.argument.IArgConsumer;
import fun.kubik.itemics.api.command.exception.CommandException;
import fun.kubik.itemics.api.pathing.goals.GoalAxis;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class AxisCommand
extends Command {
    public AxisCommand(IItemics itemics) {
        super(itemics, "axis", "highway");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(0);
        GoalAxis goal = new GoalAxis();
        this.itemics.getCustomGoalProcess().setGoal(goal);
        this.logDirect(String.format("Goal: %s", ((Object)goal).toString()));
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Set a goal to the axes";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("The axis command sets a goal that tells Itemics to head towards the nearest axis. That is, X=0 or Z=0.", "", "Usage:", "> axis");
    }
}

