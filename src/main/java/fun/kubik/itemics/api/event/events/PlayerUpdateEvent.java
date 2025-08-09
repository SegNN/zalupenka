/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.event.events;

import fun.kubik.itemics.api.event.events.type.EventState;

public final class PlayerUpdateEvent {
    private final EventState state;

    public PlayerUpdateEvent(EventState state) {
        this.state = state;
    }

    public final EventState getState() {
        return this.state;
    }
}

