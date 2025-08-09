package fun.kubik.managers.command.main;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fun.kubik.Load;
import fun.kubik.managers.command.api.Command;
import fun.kubik.managers.module.Module;
import fun.kubik.utils.client.ChatUtils;
import fun.kubik.utils.client.KeyMappings;
import fun.kubik.utils.client.KeyUtils;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TextFormatting;

public class BindCommand extends Command {
    public BindCommand() {
        super("Бинд система", "bind");
    }

    @Override
    public void build(LiteralArgumentBuilder<ISuggestionProvider> builder) {
    }

    @Override
    public void run(String[] args) throws Exception {
        if (args.length < 2) {
            error();
            return;
        }

        switch (args[1].toLowerCase()) {
            case "clear":
                for (Module module : Load.getInstance().getHooks().getModuleManagers()) {
                    module.setCurrentKey(-1);
                }
                ChatUtils.addClientMessage(TextFormatting.GRAY + "Все бинды были очищены");
                break;

            case "list":
                for (Module module : Load.getInstance().getHooks().getModuleManagers()) {
                    if (module.getCurrentKey() != -1) {
                        ChatUtils.addMessage(TextFormatting.WHITE + module.getName() + " " +
                                TextFormatting.GRAY + KeyUtils.getKey(module.getCurrentKey()));
                    }
                }
                break;

            case "add":
                if (args.length < 4) {
                    error();
                    return;
                }

                Module module = Load.getInstance().getHooks().getModuleManagers().findName(args[2]);
                if (module == null) {
                    ChatUtils.addClientMessage(TextFormatting.RED + "Модуль не найден!");
                    return;
                }

                Integer key = KeyMappings.keyMap.get(args[3].toUpperCase());
                if (key == null || key == -1) {
                    ChatUtils.addClientMessage(TextFormatting.RED + "Неверная клавиша!");
                    return;
                }

                module.setCurrentKey(key);
                ChatUtils.addClientMessage(TextFormatting.GRAY + "Бинд " + TextFormatting.WHITE + module.getName() +
                        TextFormatting.GRAY + " установлен на " + TextFormatting.WHITE + args[3].toUpperCase());
                break;

            case "remove":
                if (args.length < 3) {
                    error();
                    return;
                }

                Module moduleToRemove = Load.getInstance().getHooks().getModuleManagers().findName(args[2]);
                if (moduleToRemove == null) {
                    ChatUtils.addClientMessage(TextFormatting.RED + "Модуль не найден!");
                    return;
                }

                moduleToRemove.setCurrentKey(-1);
                ChatUtils.addClientMessage(TextFormatting.GRAY + "Бинд для " +
                        TextFormatting.WHITE + moduleToRemove.getName() + TextFormatting.GRAY + " удален");
                break;

            default:
                error();
                break;
        }
    }

    @Override
    public void error() {
        ChatUtils.addClientMessage(TextFormatting.RED + "Неверное использование команды!");
        ChatUtils.addClientMessage(TextFormatting.GRAY + "Использование:");
        ChatUtils.addClientMessage(TextFormatting.WHITE + ".bind add <module> <key>");
        ChatUtils.addClientMessage(TextFormatting.WHITE + ".bind remove <module>");
        ChatUtils.addClientMessage(TextFormatting.WHITE + ".bind list");
        ChatUtils.addClientMessage(TextFormatting.WHITE + ".bind clear");
        ChatUtils.addClientMessage(TextFormatting.GRAY + "Пример:");
        ChatUtils.addClientMessage(TextFormatting.WHITE + ".bind add speed RIGHT_SHIFT");
    }
}