/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.event.events;

import fun.kubik.itemics.api.event.events.type.EventState;
import net.minecraft.client.world.ClientWorld;

public final class WorldEvent {
    private final ClientWorld world;
    private final EventState state;

    public WorldEvent(ClientWorld world, EventState state) {
        this.world = world;
        this.state = state;
    }

    public final ClientWorld getWorld() {
        return this.world;
    }

    public final EventState getState() {
        return this.state;
    }
}

