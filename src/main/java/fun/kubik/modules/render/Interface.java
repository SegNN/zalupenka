/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.render;

import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.EventUpdate;
import fun.kubik.events.main.render.EventRender2D;
import fun.kubik.helpers.animation.EasingList;
import fun.kubik.managers.draggable.api.Component;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;
import fun.kubik.managers.module.option.main.SelectOption;
import fun.kubik.managers.module.option.main.SelectOptionValue;
import fun.kubik.managers.module.option.main.SliderOption;
import lombok.Generated;

public class Interface
extends Module {
    private final MultiOption elements = new MultiOption("Elements", new MultiOptionValue("KeyBinds", true), new MultiOptionValue("StaffList", true), new MultiOptionValue("TargetHud", true), new MultiOptionValue("PotionList", true), new MultiOptionValue("Information", true), new MultiOptionValue("WaterMark", true), new MultiOptionValue("ArmorHud", true), new MultiOptionValue("Notifications", true));
    private final SelectOption notifDesign = new SelectOption("Notification Design", 0, new SelectOptionValue("Standard"), new SelectOptionValue("Transparent")).visible(() -> this.elements.getSelected("Notifications"));
    private final SliderOption compression = new SliderOption("Compression", 1.0f, 1.0f, 8.0f).visible(() -> this.elements.getSelected("Notifications") && this.notifDesign.getSelected("Transparent")).increment(1.0f);

    public Interface() {
        super("Interface", Category.RENDER);
        this.settings(this.elements, this.notifDesign, this.compression);
    }

    @EventHook
    public void update(EventUpdate event) {
        if (this.elements.getSelected("Notifications")) {
            Load.getInstance().getHooks().getNotificationManagers().update();
        }
        for (Component drag : Load.getInstance().getHooks().getDraggableManagers()) {
            drag.getDraggableOption().getClickAnimation().update(drag.getDraggableOption().isClick());
            drag.update(event);
        }
    }

    @EventHook
    public void render(EventRender2D.Pre event) {
        if (this.elements.getSelected("Notifications")) {
            Load.getInstance().getHooks().getNotificationManagers().render(event);
        }
        for (Component drag : Load.getInstance().getHooks().getDraggableManagers()) {
            drag.getDraggableOption().getClickAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.BACK_OUT, event.getPartialTicks());
            drag.render(event);
        }
    }

    public static float calculateBPS() {
        double distance = Math.sqrt(Math.pow(Interface.mc.player.getPosX() - Interface.mc.player.prevPosX, 2.0) + Math.pow(Interface.mc.player.getPosY() - Interface.mc.player.prevPosY, 2.0) + Math.pow(Interface.mc.player.getPosZ() - Interface.mc.player.prevPosZ, 2.0));
        float bps = (float)(distance * (double)Interface.mc.getTimer().speed * 20.0);
        return (float)Math.round(bps * 10.0f) / 10.0f;
    }

    @Generated
    public MultiOption getElements() {
        return this.elements;
    }

    @Generated
    public SelectOption getNotifDesign() {
        return this.notifDesign;
    }

    @Generated
    public SliderOption getCompression() {
        return this.compression;
    }
}

