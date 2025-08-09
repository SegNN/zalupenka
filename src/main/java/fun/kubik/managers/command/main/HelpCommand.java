/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.command.main;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fun.kubik.Load;
import fun.kubik.managers.command.api.Command;
import fun.kubik.utils.client.ChatUtils;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class HelpCommand
extends Command {
    public HelpCommand() {
        super("Помощь по командам", "help");
    }

    @Override
    public void build(LiteralArgumentBuilder<ISuggestionProvider> builder) {
    }

    @Override
    public void run(String[] args) throws Exception {
        for (Command cmd : Load.getInstance().getHooks().getCommandManagers()) {
            if (cmd instanceof HelpCommand) continue;
            String name = cmd.getName().get(0).toString().replace("[", "").replace("]", "");
            StringTextComponent message = new StringTextComponent(String.valueOf((Object)TextFormatting.GRAY) + "[" + String.valueOf((Object)TextFormatting.RED) + name + String.valueOf((Object)TextFormatting.GRAY) + "] " + String.valueOf((Object)TextFormatting.GRAY) + "\u00bb " + String.valueOf((Object)TextFormatting.RESET) + cmd.getDesk());
            ChatUtils.addClientMessage(message.getString());
        }
    }

    @Override
    public void error() {
    }
}

