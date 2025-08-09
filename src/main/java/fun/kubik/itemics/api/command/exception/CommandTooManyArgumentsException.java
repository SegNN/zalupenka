/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.command.exception;

public class CommandTooManyArgumentsException
extends CommandErrorMessageException {
    public CommandTooManyArgumentsException(int maxArgs) {
        super(String.format("Too many arguments (expected at most %d)", maxArgs));
    }
}

