/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.command.main;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fun.kubik.Load;
import fun.kubik.managers.command.api.Command;
import fun.kubik.utils.client.ChatUtils;
import net.minecraft.command.ISuggestionProvider;

public class FriendCommand
extends Command {
    public FriendCommand() {
        super("\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0434\u0440\u0443\u0437\u044c\u044f\u043c\u0438", "friend");
    }

    @Override
    public void build(LiteralArgumentBuilder<ISuggestionProvider> builder) {
    }

    @Override
    public void run(String[] args) throws Exception {
        if (args.length > 1) {
            switch (args[1]) {
                case "add": {
                    if (!Load.getInstance().getHooks().getFriendManagers().is(args[2])) {
                        Load.getInstance().getHooks().getFriendManagers().add(args[2]);
                        ChatUtils.addClientMessage("\u0414\u043e\u0431\u0430\u0432\u0438\u043b \u0434\u0440\u0443\u0433\u0430 \u0441 \u043d\u0438\u043a\u043e\u043c: " + args[2] + ".");
                        break;
                    }
                    ChatUtils.addClientMessage("\u0414\u0430\u043d\u043d\u044b\u0439 \u0434\u0440\u0443\u0433 \u0443\u0436\u0435 \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u0443\u0435\u0442!");
                    break;
                }
                case "remove": {
                    if (Load.getInstance().getHooks().getFriendManagers().is(args[2])) {
                        Load.getInstance().getHooks().getFriendManagers().remove(args[2]);
                        ChatUtils.addClientMessage("\u0423\u0434\u0430\u043b\u0438\u043b \u0434\u0440\u0443\u0433\u0430 \u0441 \u043d\u0438\u043a\u043e\u043c: " + args[2] + ".");
                        break;
                    }
                    ChatUtils.addClientMessage("\u0414\u0430\u043d\u043d\u043e\u0433\u043e \u0438\u0433\u0440\u043e\u043a\u0430 \u043d\u0435\u0442 \u0432 \u0441\u043f\u0438\u0441\u043a\u0435 \u0434\u0440\u0443\u0437\u0435\u0439!");
                    break;
                }
                case "list": {
                    ChatUtils.addClientMessage(Load.getInstance().getHooks().getFriendManagers().get().toString().replace("[", "").replace("]", ""));
                    break;
                }
                case "clear": {
                    if (!Load.getInstance().getHooks().getFriendManagers().isEmpty()) {
                        Load.getInstance().getHooks().getFriendManagers().clears();
                        ChatUtils.addClientMessage("\u041e\u0447\u0438\u0441\u0442\u0438\u043b \u0441\u043f\u0438\u0441\u043e\u043a \u0434\u0440\u0443\u0437\u0435\u0439!");
                        break;
                    }
                    ChatUtils.addClientMessage("\u0421\u043f\u0438\u0441\u043e\u043a \u0434\u0440\u0443\u0437\u0435\u0439 \u043f\u0443\u0441\u0442!");
                }
            }
        } else {
            this.error();
        }
    }

    @Override
    public void error() {
        ChatUtils.addClientMessage("\u041d\u0435\u043f\u0440\u0430\u0432\u0438\u043b\u044c\u043d\u043e\u0435 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435 \u043a\u043e\u043c\u0430\u043d\u0434\u044b!");
        ChatUtils.addClientMessage("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435:");
        ChatUtils.addClientMessage(".friend add <name>");
        ChatUtils.addClientMessage(".friend remove <name>");
        ChatUtils.addClientMessage(".friend clear");
        ChatUtils.addClientMessage(".friend list");
    }
}

