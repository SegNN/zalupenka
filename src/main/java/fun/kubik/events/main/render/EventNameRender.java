/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.events.main.render;

import fun.kubik.events.api.main.Event;
import fun.kubik.events.api.main.callables.EventCancellable;
import lombok.Generated;

public class EventNameRender
extends EventCancellable
implements Event {
    private final Type type;

    @Generated
    public Type getType() {
        return this.type;
    }

    @Generated
    public EventNameRender(Type type) {
        this.type = type;
    }

    public static enum Type {
        PlayerName;

    }
}

