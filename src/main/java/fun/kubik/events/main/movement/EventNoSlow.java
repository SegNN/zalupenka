/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.events.main.movement;

import fun.kubik.events.api.main.Event;
import fun.kubik.events.api.main.callables.EventCancellable;
import lombok.Generated;

public class EventNoSlow
extends EventCancellable
implements Event {
    private float speed;

    @Generated
    public float getSpeed() {
        return this.speed;
    }

    @Generated
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Generated
    public EventNoSlow(float speed) {
        this.speed = speed;
    }
}

