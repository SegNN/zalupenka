/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.command.exception;

import fun.kubik.itemics.api.command.ICommand;
import fun.kubik.itemics.api.command.argument.ICommandArgument;
import fun.kubik.itemics.api.utils.Helper;
import java.util.List;
import net.minecraft.util.text.TextFormatting;

public interface ICommandException {
    public String getMessage();

    default public void handle(ICommand command, List<ICommandArgument> args) {
        Helper.HELPER.logDirect(this.getMessage(), TextFormatting.RED);
    }
}

