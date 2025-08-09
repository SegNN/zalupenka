/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.events.main.player;

import fun.kubik.events.api.main.Event;
import lombok.Generated;
import net.minecraft.item.Item;

public class EventCooldown
implements Event {
    public Item itemStack;
    public float cooldown;

    public EventCooldown(Item item) {
        this.itemStack = item;
    }

    @Generated
    public Item getItemStack() {
        return this.itemStack;
    }

    @Generated
    public float getCooldown() {
        return this.cooldown;
    }

    @Generated
    public void setItemStack(Item itemStack) {
        this.itemStack = itemStack;
    }

    @Generated
    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }
}

