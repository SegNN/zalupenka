/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.movement;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.player.EventElytra;
import fun.kubik.events.main.player.EventSync;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.SliderOption;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;

public class ElytraBounce
extends Module {
    private final SliderOption pitch = new SliderOption("Pitch", -45.0f, -90.0f, 90.0f).increment(1.0f);

    public ElytraBounce() {
        super("ElytraBounce", Category.MOVEMENT);
        this.settings(this.pitch);
    }

    @EventHook
    public void update(EventUpdate event) {
        if (ElytraBounce.mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() == Items.ELYTRA) {
            if (ElytraBounce.mc.player.isOnGround()) {
                ElytraBounce.mc.player.jump();
            }
            if (!ElytraBounce.mc.player.isOnGround() && !ElytraBounce.mc.player.isElytraFlying()) {
                ElytraBounce.mc.player.startFallFlying();
                ElytraBounce.mc.player.connection.sendPacket(new CEntityActionPacket(ElytraBounce.mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
            }
        }
    }

    @EventHook
    public void sync(EventSync event) {
        if (ElytraBounce.mc.player.isElytraFlying()) {
            ElytraBounce.mc.player.rotationPitchHead = ((Float)this.pitch.getValue()).floatValue();
            ElytraBounce.mc.player.prevRotationPitchHead = ((Float)this.pitch.getValue()).floatValue();
            event.setPitch(((Float)this.pitch.getValue()).floatValue());
        }
    }

    @EventHook
    public void elytra(EventElytra event) {
        if (ElytraBounce.mc.player.isElytraFlying()) {
            event.setPitch(((Float)this.pitch.getValue()).floatValue());
        }
    }
}

