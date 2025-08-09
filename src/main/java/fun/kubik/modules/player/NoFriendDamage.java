package fun.kubik.modules.player;

import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.packet.EventSendPacket;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CUseEntityPacket;

public class NoFriendDamage extends Module {
    public NoFriendDamage() {
        super("NoFriendDamage", Category.PLAYER);
    }

    @EventHook
    public void packet(EventSendPacket event) {
        if (event.getPacket() instanceof CUseEntityPacket packet) {
            Entity entity = packet.getEntityFromWorld(mc.world);
            if (entity instanceof PlayerEntity player && packet.getAction() == CUseEntityPacket.Action.ATTACK) {
                boolean isFriend = Load.getInstance().getHooks().getFriendManagers().is(player.getGameProfile().getName());
                if (player instanceof RemoteClientPlayerEntity && isFriend) {
                    event.setCancelled(true);
                }
            }
        }
    }
}