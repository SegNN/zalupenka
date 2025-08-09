/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.player;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.helpers.module.swap.SwapHelpers;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.SliderOption;
import net.minecraft.item.UseAction;

public class AutoEat
extends Module {
    private final SliderOption hungerValue = new SliderOption("Hungry Value", 5.0f, 1.0f, 20.0f).increment(1.0f);
    private final SwapHelpers swap = new SwapHelpers();
    private int oldSlot;

    public AutoEat() {
        super("AutoEat", Category.PLAYER);
        this.settings(this.hungerValue);
    }

    @EventHook
    public void update(EventUpdate event) {
        int slot = this.swap.find(UseAction.EAT);
        if ((float)AutoEat.mc.player.getFoodStats().getFoodLevel() <= ((Float)this.hungerValue.getValue()).floatValue()) {
            if (AutoEat.mc.player.inventory.currentItem != slot % 9 && slot >= 0 && this.swap.haveHotBar(slot)) {
                this.oldSlot = AutoEat.mc.player.inventory.currentItem;
                AutoEat.mc.player.inventory.currentItem = slot % 9;
            }
            AutoEat.mc.gameSettings.keyBindUseItem.setPressed(true);
        } else {
            AutoEat.mc.gameSettings.keyBindUseItem.setPressed(AutoEat.mc.player.getFoodStats().needFood() || AutoEat.mc.gameSettings.keyBindUseItem.isKeyDown());
        }
        if (!AutoEat.mc.player.getFoodStats().needFood() && this.oldSlot != -1) {
            AutoEat.mc.player.inventory.currentItem = this.oldSlot;
            this.oldSlot = -1;
        }
    }
}

