/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.player;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.helpers.module.swap.SwapHelpers;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.CheckboxOption;

public class AutoTool
extends Module {
    private final CheckboxOption silent = new CheckboxOption("Silent", false);
    private final SwapHelpers swap = new SwapHelpers();
    private int oldSlot = -1;
    private boolean active;

    public AutoTool() {
        super("AutoTool", Category.PLAYER);
    }

    @EventHook
    public void update(EventUpdate event) {
        if (AutoTool.mc.objectMouseOver != null && AutoTool.mc.gameSettings.keyBindAttack.isKeyDown()) {
            int slot = this.swap.find();
            if (slot != -1) {
                this.active = true;
                if (this.oldSlot == -1) {
                    this.oldSlot = AutoTool.mc.player.inventory.currentItem;
                }
                AutoTool.mc.player.inventory.currentItem = this.swap.find();
            }
        } else if (this.active) {
            AutoTool.mc.player.inventory.currentItem = this.oldSlot;
            this.oldSlot = -1;
            this.active = false;
        }
    }
}

