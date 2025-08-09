/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.events.main.player;

import fun.kubik.events.api.main.Event;
import lombok.Generated;
import net.minecraft.item.ItemStack;

public class EventItemTooltip
implements Event {
    private final ItemStack itemStack;

    @Generated
    public EventItemTooltip(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Generated
    public ItemStack getItemStack() {
        return this.itemStack;
    }
}

