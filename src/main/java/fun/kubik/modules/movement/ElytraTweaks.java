/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.movement;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.helpers.module.swap.SwapHelpers;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.managers.module.option.main.SliderOption;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import ru.kotopushka.j2c.sdk.annotations.NativeInclude;

public class ElytraTweaks
extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("New"), new SelectOptionValue("ReallyWorld"), new SelectOptionValue("Old"));
    private final SliderOption cooldown = new SliderOption("Cooldown", 500.0f, 300.0f, 2000.0f).increment(50.0f);
    private final SliderOption cooldownOnElytra = new SliderOption("Cooldown On Elytra", 150.0f, 50.0f, 500.0f).visible(() -> this.mode.getSelected("New")).increment(25.0f);
    private final SwapHelpers swap = new SwapHelpers();
    private int oldSlot = -1;
    private long time = 0L;

    public ElytraTweaks() {
        super("ElytraTweaks", Category.MOVEMENT);
        this.settings(this.mode, this.cooldown, this.cooldownOnElytra);
    }

    @EventHook
    public void update(EventUpdate event) {
        if (this.mode.getSelected("New")) {
            int i;
            int elytraSlot = this.swap.find(Items.ELYTRA);
            int fireworkSlot = this.swap.find(Items.FIREWORK_ROCKET);
            if (elytraSlot >= 0 && this.time <= System.currentTimeMillis() && !ElytraTweaks.mc.player.isOnGround()) {
                if (this.swap.haveHotBar(elytraSlot)) {
                    ElytraTweaks.mc.playerController.windowClick(0, 6, elytraSlot % 9, ClickType.SWAP, ElytraTweaks.mc.player);
                } else {
                    for (i = 0; i < 36; ++i) {
                        if (ElytraTweaks.mc.player.inventory.getStackInSlot(i).getItem() != Items.ELYTRA) continue;
                        ElytraTweaks.mc.playerController.windowClick(0, i, ElytraTweaks.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraTweaks.mc.player);
                        ElytraTweaks.mc.playerController.windowClick(0, 6, ElytraTweaks.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraTweaks.mc.player);
                        ElytraTweaks.mc.playerController.windowClick(0, i, ElytraTweaks.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraTweaks.mc.player);
                        this.oldSlot = i;
                        break;
                    }
                }
                ElytraTweaks.mc.player.startFallFlying();
                ElytraTweaks.mc.player.connection.sendPacket(new CEntityActionPacket(ElytraTweaks.mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                if (this.swap.haveHotBar(Items.FIREWORK_ROCKET)) {
                    if (ElytraTweaks.mc.player.isHandActive()) {
                        ElytraTweaks.mc.playerController.windowClick(0, fireworkSlot, 40, ClickType.SWAP, ElytraTweaks.mc.player);
                        ElytraTweaks.mc.playerController.processRightClick(ElytraTweaks.mc.player, ElytraTweaks.mc.world, Hand.OFF_HAND);
                        ElytraTweaks.mc.playerController.windowClick(0, fireworkSlot, 40, ClickType.SWAP, ElytraTweaks.mc.player);
                    } else {
                        ElytraTweaks.mc.player.connection.sendPacket(new CHeldItemChangePacket(fireworkSlot % 9));
                        ElytraTweaks.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                        ElytraTweaks.mc.player.connection.sendPacket(new CHeldItemChangePacket(ElytraTweaks.mc.player.inventory.currentItem));
                    }
                } else {
                    for (i = 0; i < 36; ++i) {
                        if (ElytraTweaks.mc.player.inventory.getStackInSlot(i).getItem() != Items.FIREWORK_ROCKET) continue;
                        if (ElytraTweaks.mc.player.isHandActive()) {
                            ElytraTweaks.mc.playerController.windowClick(0, i, 40, ClickType.SWAP, ElytraTweaks.mc.player);
                            ElytraTweaks.mc.playerController.processRightClick(ElytraTweaks.mc.player, ElytraTweaks.mc.world, Hand.OFF_HAND);
                            ElytraTweaks.mc.playerController.windowClick(0, i, 40, ClickType.SWAP, ElytraTweaks.mc.player);
                            break;
                        }
                        ElytraTweaks.mc.playerController.windowClick(0, i, ElytraTweaks.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraTweaks.mc.player);
                        ElytraTweaks.mc.player.connection.sendPacket(new CHeldItemChangePacket(ElytraTweaks.mc.player.inventory.currentItem % 8 + 1));
                        ElytraTweaks.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                        ElytraTweaks.mc.player.connection.sendPacket(new CHeldItemChangePacket(ElytraTweaks.mc.player.inventory.currentItem));
                        ElytraTweaks.mc.playerController.windowClick(0, i, ElytraTweaks.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraTweaks.mc.player);
                        break;
                    }
                }
                if (this.oldSlot == -1) {
                    this.oldSlot = elytraSlot;
                }
                this.time = System.currentTimeMillis() + ((Float)this.cooldownOnElytra.getValue()).longValue();
            }
            if (this.time <= System.currentTimeMillis() && this.oldSlot != -1) {
                if (this.swap.haveHotBar(this.oldSlot)) {
                    ElytraTweaks.mc.playerController.windowClick(0, 6, this.oldSlot % 9, ClickType.SWAP, ElytraTweaks.mc.player);
                } else {
                    for (i = 0; i < 36; ++i) {
                        if (i != this.oldSlot) continue;
                        ElytraTweaks.mc.playerController.windowClick(0, i, ElytraTweaks.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraTweaks.mc.player);
                        ElytraTweaks.mc.playerController.windowClick(0, 6, ElytraTweaks.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraTweaks.mc.player);
                        ElytraTweaks.mc.playerController.windowClick(0, i, ElytraTweaks.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraTweaks.mc.player);
                        break;
                    }
                }
                this.time = System.currentTimeMillis() + ((Float)this.cooldown.getValue()).longValue();
                this.oldSlot = -1;
            }
        }
    }

    @Override
    @NativeInclude
    public void onEnabled() {
        if (this.mode.getSelected("New")) {
            this.time = System.currentTimeMillis();
        }
    }

    @Override
    @NativeInclude
    public void onDisabled() {
        if (ElytraTweaks.mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() == Items.ELYTRA && this.oldSlot != -1 && this.mode.getSelected("New")) {
            if (this.swap.haveHotBar(this.oldSlot)) {
                ElytraTweaks.mc.playerController.windowClick(0, 6, this.oldSlot % 9, ClickType.SWAP, ElytraTweaks.mc.player);
            } else {
                for (int i = 0; i < 36; ++i) {
                    if (i != this.oldSlot) continue;
                    ElytraTweaks.mc.playerController.windowClick(0, i, ElytraTweaks.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraTweaks.mc.player);
                    ElytraTweaks.mc.playerController.windowClick(0, 6, ElytraTweaks.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraTweaks.mc.player);
                    ElytraTweaks.mc.playerController.windowClick(0, i, ElytraTweaks.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraTweaks.mc.player);
                    break;
                }
            }
            this.oldSlot = -1;
        }
    }
}

