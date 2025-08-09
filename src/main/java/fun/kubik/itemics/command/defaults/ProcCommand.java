/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.command.defaults;

import fun.kubik.itemics.api.IItemics;
import fun.kubik.itemics.api.command.Command;
import fun.kubik.itemics.api.command.argument.IArgConsumer;
import fun.kubik.itemics.api.command.exception.CommandException;
import fun.kubik.itemics.api.command.exception.CommandInvalidStateException;
import fun.kubik.itemics.api.pathing.calc.IPathingControlManager;
import fun.kubik.itemics.api.process.IItemicsProcess;
import fun.kubik.itemics.api.process.PathingCommand;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ProcCommand
extends Command {
    public ProcCommand(IItemics itemics) {
        super(itemics, "proc");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(0);
        IPathingControlManager pathingControlManager = this.itemics.getPathingControlManager();
        IItemicsProcess process = pathingControlManager.mostRecentInControl().orElse(null);
        if (process == null) {
            throw new CommandInvalidStateException("No process in control");
        }
        this.logDirect(String.format("Class: %s\nPriority: %f\nTemporary: %b\nDisplay name: %s\nLast command: %s", process.getClass().getTypeName(), process.priority(), process.isTemporary(), process.displayName(), pathingControlManager.mostRecentCommand().map(PathingCommand::toString).orElse("None")));
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "View process state information";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("The proc command provides miscellaneous information about the process currently controlling Itemics.", "", "You are not expected to understand this if you aren't familiar with how Itemics works.", "", "Usage:", "> proc - View process information, if present");
    }
}

