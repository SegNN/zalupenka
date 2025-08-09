/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.player;

import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.misc.EventPush;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import fun.kubik.managers.module.option.main.MultiOption;
import fun.kubik.managers.module.option.main.MultiOptionValue;

public class NoPush
        extends Module {
    private final MultiOption elements = new MultiOption("Elements", new MultiOptionValue("Blocks", true), new MultiOptionValue("Entities", true), new MultiOptionValue("Water", false));

    public NoPush() {
        super("NoPush", Category.PLAYER);
        this.settings(this.elements);
    }

    @EventHook
    public void push(EventPush eventPush) {
        EventPush.PushType pushType = eventPush.getPushType();
        boolean cancelPush = switch (pushType) {
            case Entities -> this.elements.getSelected("Entities");
            case Blocks -> this.elements.getSelected("Blocks");
            case Water -> this.elements.getSelected("Water");
        };
        eventPush.setCancelled(cancelPush);
    }
}
