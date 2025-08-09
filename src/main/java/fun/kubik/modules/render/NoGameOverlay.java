/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.render;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.render.EventGameOverlay;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;

public class NoGameOverlay
        extends Module {
    private final MultiOption elements = new MultiOption("Elements", new MultiOptionValue("Hurt", true), new MultiOptionValue("PumpkinOverlay", true), new MultiOptionValue("TotemPop", true), new MultiOptionValue("Fire", true), new MultiOptionValue("BossBar", true), new MultiOptionValue("Scoreboard", true), new MultiOptionValue("Block", true), new MultiOptionValue("WaterFog", true), new MultiOptionValue("LavaFog", true), new MultiOptionValue("Nausea", true), new MultiOptionValue("Blindness", true), new MultiOptionValue("Hologram", true));

    public NoGameOverlay() {
        super("NoGameOverlay", Category.RENDER);
        this.settings(this.elements);
    }

    @EventHook
    public void eventOverlay(EventGameOverlay eventGameOverlay) {
        EventGameOverlay.OverlayType overlayType = eventGameOverlay.getOverlayType();
        boolean cancelOverlay = switch (overlayType) {
            case Hurt -> this.elements.getSelected("Hurt");
            case PumpkinOverlay -> this.elements.getSelected("PumpkinOverlay");
            case TotemPop -> this.elements.getSelected("TotemPop");
            case Fire -> this.elements.getSelected("Fire");
            case BossBar -> this.elements.getSelected("BossBar");
            case Blindness -> this.elements.getSelected("Blindness");
            case Scoreboard -> this.elements.getSelected("Scoreboard");
            case Block -> this.elements.getSelected("Block");
            case WaterFog -> this.elements.getSelected("WaterFog");
            case Nausea -> this.elements.getSelected("Nausea");
            case LavaFog -> this.elements.getSelected("LavaFog");
            case Hologram -> this.elements.getSelected("Hologram");
            case CameraBounds, Light, Fog -> false;
        };
        eventGameOverlay.setCancelled(cancelOverlay);
    }
}
