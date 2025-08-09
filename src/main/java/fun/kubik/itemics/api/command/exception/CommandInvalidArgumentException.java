/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.command.exception;

import fun.kubik.itemics.api.command.argument.ICommandArgument;

public abstract class CommandInvalidArgumentException
extends CommandErrorMessageException {
    public final ICommandArgument arg;

    protected CommandInvalidArgumentException(ICommandArgument arg, String message) {
        super(CommandInvalidArgumentException.formatMessage(arg, message));
        this.arg = arg;
    }

    protected CommandInvalidArgumentException(ICommandArgument arg, String message, Throwable cause) {
        super(CommandInvalidArgumentException.formatMessage(arg, message), cause);
        this.arg = arg;
    }

    private static String formatMessage(ICommandArgument arg, String message) {
        return String.format("Error at argument #%s: %s", arg.getIndex() == -1 ? "<unknown>" : Integer.toString(arg.getIndex() + 1), message);
    }
}

