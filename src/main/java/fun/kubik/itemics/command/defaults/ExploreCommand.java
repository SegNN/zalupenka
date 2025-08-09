/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.command.defaults;

import fun.kubik.itemics.api.IItemics;
import fun.kubik.itemics.api.command.Command;
import fun.kubik.itemics.api.command.argument.IArgConsumer;
import fun.kubik.itemics.api.command.datatypes.RelativeGoalXZ;
import fun.kubik.itemics.api.command.exception.CommandException;
import fun.kubik.itemics.api.pathing.goals.GoalXZ;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ExploreCommand
extends Command {
    public ExploreCommand(IItemics itemics) {
        super(itemics, "explore");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        if (args.hasAny()) {
            args.requireExactly(2);
        } else {
            args.requireMax(0);
        }
        GoalXZ goal = args.hasAny() ? (GoalXZ)args.getDatatypePost(RelativeGoalXZ.INSTANCE, this.ctx.playerFeet()) : new GoalXZ(this.ctx.playerFeet());
        this.itemics.getExploreProcess().explore(goal.getX(), goal.getZ());
        this.logDirect(String.format("Exploring from %s", goal.toString()));
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        if (args.hasAtMost(2)) {
            return args.tabCompleteDatatype(RelativeGoalXZ.INSTANCE);
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Explore things";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Tell Itemics to explore randomly. If you used explorefilter before this, it will be applied.", "", "Usage:", "> explore - Explore from your current position.", "> explore <x> <z> - Explore from the specified X and Z position.");
    }
}

