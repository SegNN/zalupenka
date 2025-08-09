package fun.kubik.managers.command.main;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fun.kubik.Load;
import fun.kubik.managers.command.api.Command;
import fun.kubik.utils.client.ChatUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TextFormatting;

public class BlockESPCommand extends Command {
    public BlockESPCommand() {
        super("BlocksEsp", "blockesp");
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
            case "add":
                if (args.length < 4) {
                    error();
                    return;
                }

                String blockName = args[2].toLowerCase();
                try {
                    // Поддержка разных форматов цвета: 0xFFFFFF, #FFFFFF или просто FFFFFF
                    String colorStr = args[3].replace("#", "").replace("0x", "").replace("0X", "");
                    long parsed = Long.parseLong(colorStr, 16);
                    int color = (colorStr.length() <= 6) ? (int)(0xFF000000L | parsed) : (int) parsed;

                    Block block = Registry.BLOCK.getOrDefault(new ResourceLocation(blockName));

                    if (block == Blocks.AIR) {
                        ChatUtils.addClientMessage(TextFormatting.RED + "Этот блок не существует!");
                        return;
                    }

                    if (!Load.getInstance().getHooks().getBlockESPManagers().addBlock(block, color)) {
                        ChatUtils.addClientMessage(TextFormatting.GRAY + "Блок " + TextFormatting.WHITE + blockName +
                                TextFormatting.GRAY + " уже добавлен в список");
                        return;
                    }

                    ChatUtils.addClientMessage(TextFormatting.GRAY + "Блок " + TextFormatting.WHITE + blockName +
                            TextFormatting.GRAY + " с цветом " + TextFormatting.WHITE + "#" + String.format("%06X", (color & 0xFFFFFF)) +
                            TextFormatting.GRAY + " успешно добавлен");
                } catch (NumberFormatException e) {
                    ChatUtils.addClientMessage(TextFormatting.RED + "Неверный формат цвета! Используйте HEX-формат (например, FFFFFF, #FFFFFF или 0xFFFFFF)");
                }
                break;

            case "remove":
            case "del":
            case "delete":
                if (args.length < 3) {
                    error();
                    return;
                }
                {
                    String blockNameToRemove = args[2].toLowerCase();
                    Block blockToRemove = Registry.BLOCK.getOrDefault(new ResourceLocation(blockNameToRemove));
                    if (blockToRemove == Blocks.AIR) {
                        ChatUtils.addClientMessage(TextFormatting.RED + "Этот блок не существует!");
                        return;
                    }
                    boolean removed = Load.getInstance().getHooks().getBlockESPManagers().removeBlock(blockToRemove);
                    if (removed) {
                        ChatUtils.addClientMessage(TextFormatting.GRAY + "Блок " + TextFormatting.WHITE + blockNameToRemove + TextFormatting.GRAY + " удалён из списка");
                    } else {
                        ChatUtils.addClientMessage(TextFormatting.GRAY + "Блок " + TextFormatting.WHITE + blockNameToRemove + TextFormatting.GRAY + " не был в списке");
                    }
                }
                break;

            case "list":
                if (Load.getInstance().getHooks().getBlockESPManagers().isEmpty()) {
                    ChatUtils.addClientMessage(TextFormatting.GRAY + "Список пуст");
                } else {
                    ChatUtils.addClientMessage(TextFormatting.GRAY + "Подсвечиваемые блоки:");
                    Load.getInstance().getHooks().getBlockESPManagers().forEach((b, c) -> {
                        String id = Registry.BLOCK.getKey(b).toString();
                        ChatUtils.addClientMessage(TextFormatting.WHITE + id + TextFormatting.GRAY + " #" + String.format("%06X", (c & 0xFFFFFF)));
                    });
                }
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
        ChatUtils.addClientMessage(TextFormatting.WHITE + ".blockesp add <block> <color>");
        ChatUtils.addClientMessage(TextFormatting.WHITE + ".blockesp remove <block>");
        ChatUtils.addClientMessage(TextFormatting.WHITE + ".blockesp list");
        ChatUtils.addClientMessage(TextFormatting.GRAY + "Пример:");
        ChatUtils.addClientMessage(TextFormatting.WHITE + ".blockesp add diamond_block FFFFFF");
        ChatUtils.addClientMessage(TextFormatting.WHITE + ".blockesp add diamond_block #00FF00");
        ChatUtils.addClientMessage(TextFormatting.WHITE + ".blockesp add diamond_block 0xFF0000");
    }
}