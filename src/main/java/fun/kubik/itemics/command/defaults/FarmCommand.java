/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.command.defaults;

import fun.kubik.itemics.api.IItemics;
import fun.kubik.itemics.api.cache.IWaypoint;
import fun.kubik.itemics.api.command.Command;
import fun.kubik.itemics.api.command.argument.IArgConsumer;
import fun.kubik.itemics.api.command.datatypes.ForWaypoints;
import fun.kubik.itemics.api.command.exception.CommandException;
import fun.kubik.itemics.api.command.exception.CommandInvalidStateException;
import fun.kubik.itemics.api.utils.BetterBlockPos;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class FarmCommand
extends Command {
    public FarmCommand(IItemics itemics) {
        super(itemics, "farm");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(2);
        int range = 0;
        BetterBlockPos origin = null;
        if (args.has(1)) {
            range = args.getAs(Integer.class);
        }
        if (args.has(1)) {
            IWaypoint[] waypoints = (IWaypoint[])args.getDatatypeFor(ForWaypoints.INSTANCE);
            IWaypoint waypoint = null;
            switch (waypoints.length) {
                case 0: {
                    throw new CommandInvalidStateException("No waypoints found");
                }
                case 1: {
                    waypoint = waypoints[0];
                    break;
                }
                default: {
                    throw new CommandInvalidStateException("Multiple waypoints were found");
                }
            }
            origin = waypoint.getLocation();
        }
        this.itemics.getFarmProcess().farm(range, origin);
        this.logDirect("Farming");
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Farm nearby crops";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("The farm command starts farming nearby plants. It harvests mature crops and plants new ones.", "", "Usage:", "> farm - farms every crop it can find.", "> farm <range> - farm crops within range from the starting position.", "> farm <range> <waypoint> - farm crops within range from waypoint.");
    }
}

