/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.events.main.misc;

import fun.kubik.events.api.main.callables.EventCancellable;
import lombok.Generated;
import net.minecraft.entity.Entity;

public class AttackEvent
extends EventCancellable {
    public Entity entity;

    @Generated
    public AttackEvent(Entity entity) {
        this.entity = entity;
    }
}

