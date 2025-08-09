/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.movement;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;
import net.minecraft.item.Items;

public class NoDelay
extends Module {
    private final MultiOption elements = new MultiOption("Elements", new MultiOptionValue("Jump", true), new MultiOptionValue("Right Click", true), new MultiOptionValue("Experience Bottle", true));

    public NoDelay() {
        super("NoDelay", Category.MOVEMENT);
        this.settings(this.elements);
    }

    @EventHook
    public void update(EventUpdate eventUpdate) {
        if (this.elements.getSelected("Jump")) {
            NoDelay.mc.player.setJumpTicks(0);
        }
        if (this.elements.getSelected("Right Click")) {
            mc.setRightClickDelayTimer(1);
        }
        if (this.elements.getSelected("Experience Bottle") && (NoDelay.mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE || NoDelay.mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE) && !NoDelay.mc.player.isHandActive()) {
            mc.setRightClickDelayTimer(1);
        }
    }
}

