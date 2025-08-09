/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.managers.command.main;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fun.kubik.managers.command.api.Command;
import fun.kubik.utils.client.ChatUtils;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerPacket;

public class TeleportCommand
extends Command {
    public TeleportCommand() {
        super("\u041f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f", "teleport", "tp");
    }

    @Override
    public void build(LiteralArgumentBuilder<ISuggestionProvider> builder) {
    }

    @Override
    public void run(String[] args) throws Exception {
        if (args.length > 1) {
            PlayerEntity target = TeleportCommand.mc.world.getPlayers().stream().filter(player -> player.getName().getString().equalsIgnoreCase(args[1])).findFirst().orElse(null);
            if (target == null) {
                ChatUtils.addClientMessage("\u0414\u0430\u043d\u043d\u043e\u0433\u043e \u0438\u0433\u0440\u043e\u043a\u0430 \u043d\u0435\u0442 \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435!");
                return;
            }
            if (args[1].equals(target.getName().getString())) {
                int i;
                double x = target.getPosX();
                double y = target.getPosY();
                double z = target.getPosZ();
                TeleportCommand.mc.player.connection.sendPacket(new CEntityActionPacket(TeleportCommand.mc.player, CEntityActionPacket.Action.START_SPRINTING));
                TeleportCommand.mc.player.connection.sendPacket(new CEntityActionPacket(TeleportCommand.mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
                for (i = 0; i < 10; ++i) {
                    TeleportCommand.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(TeleportCommand.mc.player.getPosX(), TeleportCommand.mc.player.getPosY(), TeleportCommand.mc.player.getPosZ(), false));
                }
                for (i = 0; i < 10; ++i) {
                    TeleportCommand.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(TeleportCommand.mc.player.getPosX(), y, TeleportCommand.mc.player.getPosZ(), false));
                    TeleportCommand.mc.player.setPosition(TeleportCommand.mc.player.getPosX(), y, TeleportCommand.mc.player.getPosZ());
                }
                TeleportCommand.mc.player.connection.sendPacket(new CEntityActionPacket(TeleportCommand.mc.player, CEntityActionPacket.Action.START_SPRINTING));
                TeleportCommand.mc.player.connection.sendPacket(new CEntityActionPacket(TeleportCommand.mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
                ChatUtils.addClientMessage("\u0412\u044b \u0431\u044b\u043b\u0438 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u043d\u044b \u043a \u0438\u0433\u0440\u043e\u043a\u0443: " + target.getName().getString() + "!");
            }
        } else {
            this.error();
        }
    }

    @Override
    public void error() {
        ChatUtils.addClientMessage("\u041d\u0435\u043f\u0440\u0430\u0432\u0438\u043b\u044c\u043d\u043e\u0435 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435 \u043a\u043e\u043c\u0430\u043d\u0434\u044b!");
        ChatUtils.addClientMessage("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435:");
        ChatUtils.addClientMessage(".tp <name>");
    }
}

