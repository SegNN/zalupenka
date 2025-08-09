/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.command.defaults;

import fun.kubik.itemics.api.IItemics;
import fun.kubik.itemics.api.command.Command;
import fun.kubik.itemics.api.command.argument.IArgConsumer;
import fun.kubik.itemics.api.command.exception.CommandException;
import fun.kubik.itemics.api.command.exception.CommandInvalidStateException;
import fun.kubik.itemics.api.process.IItemicsProcess;
import fun.kubik.itemics.api.process.PathingCommand;
import fun.kubik.itemics.api.process.PathingCommandType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ExecutionControlCommands {
    Command pauseCommand;
    Command resumeCommand;
    Command pausedCommand;
    Command cancelCommand;

    public ExecutionControlCommands(final IItemics itemics) {
        final boolean[] paused = new boolean[]{false};
        itemics.getPathingControlManager().registerProcess(new IItemicsProcess(){

            @Override
            public boolean isActive() {
                return paused[0];
            }

            @Override
            public PathingCommand onTick(boolean calcFailed, boolean isSafeToCancel) {
                itemics.getInputOverrideHandler().clearAllKeys();
                return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
            }

            @Override
            public boolean isTemporary() {
                return true;
            }

            @Override
            public void onLostControl() {
            }

            @Override
            public double priority() {
                return 0.0;
            }

            @Override
            public String displayName0() {
                return "Pause/Resume Commands";
            }
        });
        this.pauseCommand = new Command(itemics, new String[]{"pause", "p", "paws"}){

            @Override
            public void execute(String label, IArgConsumer args) throws CommandException {
                args.requireMax(0);
                if (paused[0]) {
                    throw new CommandInvalidStateException("Already paused");
                }
                paused[0] = true;
                this.logDirect("Paused");
            }

            @Override
            public Stream<String> tabComplete(String label, IArgConsumer args) {
                return Stream.empty();
            }

            @Override
            public String getShortDesc() {
                return "Pauses Itemics until you use resume";
            }

            @Override
            public List<String> getLongDesc() {
                return Arrays.asList("The pause command tells Itemics to temporarily stop whatever it's doing.", "", "This can be used to pause pathing, building, following, whatever. A single use of the resume command will start it right back up again!", "", "Usage:", "> pause");
            }
        };
        this.resumeCommand = new Command(itemics, new String[]{"resume", "r", "unpause", "unpaws"}){

            @Override
            public void execute(String label, IArgConsumer args) throws CommandException {
                args.requireMax(0);
                this.itemics.getBuilderProcess().resume();
                if (!paused[0]) {
                    throw new CommandInvalidStateException("Not paused");
                }
                paused[0] = false;
                this.logDirect("Resumed");
            }

            @Override
            public Stream<String> tabComplete(String label, IArgConsumer args) {
                return Stream.empty();
            }

            @Override
            public String getShortDesc() {
                return "Resumes Itemics after a pause";
            }

            @Override
            public List<String> getLongDesc() {
                return Arrays.asList("The resume command tells Itemics to resume whatever it was doing when you last used pause.", "", "Usage:", "> resume");
            }
        };
        this.pausedCommand = new Command(itemics, new String[]{"paused"}){

            @Override
            public void execute(String label, IArgConsumer args) throws CommandException {
                args.requireMax(0);
                this.logDirect(String.format("Itemics is %spaused", paused[0] ? "" : "not "));
            }

            @Override
            public Stream<String> tabComplete(String label, IArgConsumer args) {
                return Stream.empty();
            }

            @Override
            public String getShortDesc() {
                return "Tells you if Itemics is paused";
            }

            @Override
            public List<String> getLongDesc() {
                return Arrays.asList("The paused command tells you if Itemics is currently paused by use of the pause command.", "", "Usage:", "> paused");
            }
        };
        this.cancelCommand = new Command(itemics, new String[]{"cancel", "c", "stop"}){

            @Override
            public void execute(String label, IArgConsumer args) throws CommandException {
                args.requireMax(0);
                if (paused[0]) {
                    paused[0] = false;
                }
                this.itemics.getPathingBehavior().cancelEverything();
                this.logDirect("ok canceled");
            }

            @Override
            public Stream<String> tabComplete(String label, IArgConsumer args) {
                return Stream.empty();
            }

            @Override
            public String getShortDesc() {
                return "Cancel what Itemics is currently doing";
            }

            @Override
            public List<String> getLongDesc() {
                return Arrays.asList("The cancel command tells Itemics to stop whatever it's currently doing.", "", "Usage:", "> cancel");
            }
        };
    }
}

