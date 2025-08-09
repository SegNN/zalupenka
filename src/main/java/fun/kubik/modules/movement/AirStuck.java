/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.movement;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.packet.EventSendPacket;
import fun.kubik.events.main.player.EventSync;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import net.minecraft.item.BlockItem;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CUseEntityPacket;

public class AirStuck
extends Module {
    public AirStuck() {
        super("AirStuck", Category.MOVEMENT);
    }

    @EventHook
    public void onpacket(EventSendPacket e) {
        IPacket var3;
        AirStuck.mc.player.setVelocity(0.0, 0.0, 0.0);
        if (e.getPacket() instanceof CUseEntityPacket && ((CUseEntityPacket)e.getPacket()).getEntityFromWorld(AirStuck.mc.world).getEntityId() == AirStuck.mc.player.getEntityId()) {
            e.setCancelled(true);
        }
        if (e.getPacket() instanceof CPlayerTryUseItemOnBlockPacket && !(AirStuck.mc.player.inventory.getCurrentItem().getItem() instanceof BlockItem)) {
            e.setCancelled(true);
        }
        if ((var3 = e.getPacket()) instanceof CPlayerPacket) {
            CPlayerPacket p = (CPlayerPacket)var3;
            if (AirStuck.mc.player != null) {
                if (p.isMoving()) {
                    p.setX(AirStuck.mc.player.getPosX());
                    p.setY(AirStuck.mc.player.getPosY());
                    p.setZ(AirStuck.mc.player.getPosZ());
                }
                p.setOnGround(AirStuck.mc.player.isOnGround());
                if (p.isRotating()) {
                    p.setYaw(AirStuck.mc.player.rotationYaw);
                    p.setPitch(AirStuck.mc.player.rotationPitch);
                }
            }
            if (AirStuck.mc.player == null) {
                this.toggle();
            }
        }
    }

    @EventHook
    public void onMotion(EventSync eventMotion) {
        AirStuck.mc.player.setVelocity(0.0, 0.0, 0.0);
        eventMotion.setCancelled(true);
    }
}

