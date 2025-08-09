/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.command.exception;

import fun.kubik.itemics.api.command.ICommand;
import fun.kubik.itemics.api.command.argument.ICommandArgument;
import fun.kubik.itemics.api.utils.Helper;
import java.util.List;

public class CommandNotFoundException
extends CommandException {
    public final String command;

    public CommandNotFoundException(String command) {
        super(String.format("Command not found: %s", command));
        this.command = command;
    }

    @Override
    public void handle(ICommand command, List<ICommandArgument> args) {
        Helper.HELPER.logDirect(this.getMessage());
    }
}

