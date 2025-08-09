/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.command.exception;

public abstract class CommandException
extends Exception
implements ICommandException {
    protected CommandException(String reason) {
        super(reason);
    }

    protected CommandException(String reason, Throwable cause) {
        super(reason, cause);
    }
}

