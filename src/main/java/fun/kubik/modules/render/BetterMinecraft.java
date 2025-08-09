/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.render;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.render.EventRender2D;
import fun.kubik.events.main.visual.EventCamera;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;

public class BetterMinecraft
extends Module {
    private final MultiOption elements = new MultiOption("Elements", new MultiOptionValue("Extra Hunger", false), new MultiOptionValue("Camera Through Walls", false));

    public BetterMinecraft() {
        super("BetterMinecraft", Category.RENDER);
        this.settings(this.elements);
    }

    @EventHook
    public void render(EventRender2D.Hunger event) {
        if (this.elements.getSelected("Extra Hunger")) {
            int k1 = BetterMinecraft.mc.ingameGUI.getScaledHeight() - 39;
            int j1 = BetterMinecraft.mc.ingameGUI.getScaledWidth() / 2 + 91;
            for (int k6 = 0; k6 < 10; ++k6) {
                int i7 = k1;
                int k7 = 16;
                boolean i8 = false;
                int k8 = j1 - k6 * 8 - 9;
                BetterMinecraft.mc.ingameGUI.blit(event.getMatrixStack(), k8, i7 - 12, 16, 27, 9, 9);
                if (k6 * 2 + 1 < (int)BetterMinecraft.mc.player.getFoodStats().getSaturationLevel()) {
                    BetterMinecraft.mc.ingameGUI.blit(event.getMatrixStack(), k8, i7 - 12, k7 + 36, 27, 9, 9);
                }
                if (k6 * 2 + 1 != (int)BetterMinecraft.mc.player.getFoodStats().getSaturationLevel()) continue;
                BetterMinecraft.mc.ingameGUI.blit(event.getMatrixStack(), k8, i7 - 12, k7 + 45, 27, 9, 9);
            }
        }
    }

    @EventHook
    public void camera(EventCamera event) {
        if (this.elements.getSelected("Camera Through Walls")) {
            event.setCancelled(true);
        }
    }
}

