/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.player;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.player.EventSync;
import fun.kubik.helpers.module.swap.SwapHelpers;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.CheckboxOption;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;
import fun.kubik.utils.time.TimerUtils;
import java.util.function.Supplier;
import lombok.Generated;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;

public class AutoPotion
extends Module {
    private static final MultiOption potions = new MultiOption("Potions", new MultiOptionValue("Strength", true), new MultiOptionValue("Speed", true), new MultiOptionValue("Fire Resistance", true));
    private final CheckboxOption onlyGround = new CheckboxOption("Only Ground", true);
    private final TimerUtils timer = new TimerUtils();
    private final TimerUtils backSwapTimer = new TimerUtils();
    private final SwapHelpers swap = new SwapHelpers();
    private ItemStack oldItem;

    public AutoPotion() {
        super("AutoPotion", Category.PLAYER);
        this.settings(potions, this.onlyGround);
    }

    @EventHook
    public void update(EventUpdate event) {
        boolean ground;
        boolean bl = ground = (Boolean)this.onlyGround.getValue() == false || AutoPotion.mc.player.isOnGround();
        if (this.timer.hasTimeElapsed(500L) && ground && !AutoPotion.mc.player.isOnLadder() && !AutoPotion.mc.player.isElytraFlying()) {
            for (Potions potions : Potions.values()) {
                if (!this.nonActive(potions) || !potions.active.get().booleanValue()) continue;
                int slot = this.swap.find(potions.potionId);
                if (slot != -1 && this.swap.haveHotBar(slot)) {
                    AutoPotion.mc.player.connection.sendPacket(new CHeldItemChangePacket(slot - 36));
                    AutoPotion.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    AutoPotion.mc.player.connection.sendPacket(new CHeldItemChangePacket(AutoPotion.mc.player.inventory.currentItem));
                    continue;
                }
                if (slot == -1) continue;
                if (this.oldItem == null) {
                    ItemStack old = AutoPotion.mc.player.inventory.getStackInSlot(8);
                    this.oldItem = old.copy();
                    this.backSwapTimer.reset();
                }
                AutoPotion.mc.playerController.windowClick(0, slot, 8, ClickType.SWAP, AutoPotion.mc.player);
                AutoPotion.mc.player.connection.sendPacket(new CHeldItemChangePacket(8));
                AutoPotion.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                AutoPotion.mc.player.connection.sendPacket(new CHeldItemChangePacket(AutoPotion.mc.player.inventory.currentItem));
            }
            if (this.oldItem != null && this.backSwapTimer.hasTimeElapsed(900L)) {
                int oldSlot = this.swap.find(this.oldItem.getItem());
                if (oldSlot != -1) {
                    AutoPotion.mc.playerController.windowClick(0, oldSlot, 8, ClickType.SWAP, AutoPotion.mc.player);
                }
                this.oldItem = null;
                this.backSwapTimer.reset();
            }
            this.timer.reset();
        }
    }

    @EventHook
    public void sync(EventSync event) {
        boolean ground;
        boolean bl = ground = (Boolean)this.onlyGround.getValue() == false || AutoPotion.mc.player.isOnGround();
        if (this.timer.hasTimeElapsed(400L) && ground && !AutoPotion.mc.player.isOnLadder() && !AutoPotion.mc.player.isElytraFlying()) {
            for (Potions potions : Potions.values()) {
                int slot;
                if (!this.nonActive(potions) || !potions.active.get().booleanValue() || (slot = this.swap.find(potions.potionId)) == -1) continue;
                AutoPotion.mc.player.rotationPitchHead = 90.0f;
                event.setPitch(90.0f);
            }
        }
    }

    private boolean nonActive(Potions potions) {
        return !AutoPotion.mc.player.isPotionActive(potions.potion);
    }

    @Override
    public void onEnabled() {
        this.timer.reset();
        this.backSwapTimer.reset();
    }

    @Generated
    public CheckboxOption getOnlyGround() {
        return this.onlyGround;
    }

    @Generated
    public TimerUtils getTimer() {
        return this.timer;
    }

    @Generated
    public TimerUtils getBackSwapTimer() {
        return this.backSwapTimer;
    }

    @Generated
    public SwapHelpers getSwap() {
        return this.swap;
    }

    @Generated
    public ItemStack getOldItem() {
        return this.oldItem;
    }

    public static enum Potions {
        STRENGTH(Effects.STRENGTH, 5, () -> potions.getSelected("Strength")),
        SPEED(Effects.SPEED, 1, () -> potions.getSelected("Speed")),
        FIRE_RESIST(Effects.FIRE_RESISTANCE, 12, () -> potions.getSelected("Fire Resistance"));

        private final Effect potion;
        private final int potionId;
        private final Supplier<Boolean> active;

        private Potions(Effect potion, int potionId, Supplier<Boolean> active) {
            this.potion = potion;
            this.potionId = potionId;
            this.active = active;
        }
    }
}

