/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.movement;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.movement.EventNoSlow;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.managers.module.option.main.SliderOption;
import fun.kubik.utils.player.MoveUtils;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import ru.kotopushka.j2c.sdk.annotations.NativeInclude;

public class NoSlow
extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("ReallyWorld"), new SelectOptionValue("Vanilla"), new SelectOptionValue("Grim"), new SelectOptionValue("Matrix"));
    private final SliderOption speed = new SliderOption("Speed", 1.0f, 0.1f, 1.0f).increment(0.05f).visible(() -> this.mode.getSelected("Vanilla"));

    public NoSlow() {
        super("NoSlow", Category.MOVEMENT);
        this.settings(this.mode, this.speed);
    }

    @EventHook
    public void noSlow(EventNoSlow event) {
        switch (((SelectOptionValue)this.mode.getValue()).getName()) {
            case "ReallyWorld": {
                this.reallyWorld(event);
                break;
            }
            case "Vanilla": {
                this.vanilla(event);
                break;
            }
            case "Grim": {
                this.grim(event);
                break;
            }
            case "Matrix": {
                this.matrix(event);
            }
        }
    }

    @NativeInclude
    private void reallyWorld(EventNoSlow event) {
        if ((NoSlow.mc.player.getItemInUseCount() < 25 || NoSlow.mc.player.getHeldItemOffhand().getItem() == Items.SHIELD && NoSlow.mc.player.getItemInUseCount() < 71993) && NoSlow.mc.player.getActiveHand() == Hand.OFF_HAND) {
            int old = NoSlow.mc.player.inventory.currentItem;
            NoSlow.mc.player.connection.sendPacket(new CHeldItemChangePacket(old + 1 > 8 ? old - 1 : old + 1));
            NoSlow.mc.player.connection.sendPacket(new CHeldItemChangePacket(NoSlow.mc.player.inventory.currentItem));
            event.setCancelled(true);
        }
    }

    private void vanilla(EventNoSlow event) {
        event.setSpeed(((Float)this.speed.getValue()).floatValue());
    }

    @NativeInclude
    private void grim(EventNoSlow event) {
        if (NoSlow.mc.player.getHeldItemOffhand().getUseAction() == UseAction.BLOCK && NoSlow.mc.player.getActiveHand() == Hand.MAIN_HAND || NoSlow.mc.player.getHeldItemOffhand().getUseAction() == UseAction.EAT && NoSlow.mc.player.getActiveHand() == Hand.MAIN_HAND) {
            return;
        }
        if (NoSlow.mc.player.getActiveHand() == Hand.MAIN_HAND) {
            NoSlow.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
        this.sendItemChangePacket();
    }

    @NativeInclude
    private void matrix(EventNoSlow event) {
        boolean isFalling = (double)NoSlow.mc.player.fallDistance > 0.725;
        event.setCancelled(true);
        if (NoSlow.mc.player.isOnGround() && !NoSlow.mc.player.movementInput.jump) {
            if (NoSlow.mc.player.ticksExisted % 2 == 0) {
                boolean isNotStrafing = NoSlow.mc.player.moveStrafing == 0.0f;
                float speedMultiplier = isNotStrafing ? 0.5f : 0.4f;
                NoSlow.mc.player.getMotion().x *= (double)speedMultiplier;
                NoSlow.mc.player.getMotion().z *= (double)speedMultiplier;
            }
        } else if (isFalling) {
            boolean isVeryFastFalling = (double)NoSlow.mc.player.fallDistance > 1.4;
            float speedMultiplier = isVeryFastFalling ? 0.95f : 0.97f;
            NoSlow.mc.player.getMotion().x *= (double)speedMultiplier;
            NoSlow.mc.player.getMotion().z *= (double)speedMultiplier;
        }
    }

    @NativeInclude
    private void sendItemChangePacket() {
        if (MoveUtils.isMoving()) {
            NoSlow.mc.player.connection.sendPacket(new CHeldItemChangePacket(NoSlow.mc.player.inventory.currentItem % 8 + 1));
            NoSlow.mc.player.connection.sendPacket(new CHeldItemChangePacket(NoSlow.mc.player.inventory.currentItem));
        }
    }
}

