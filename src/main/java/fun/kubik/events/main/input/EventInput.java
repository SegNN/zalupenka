/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.events.main.input;

import fun.kubik.events.api.main.Event;
import fun.kubik.events.api.main.callables.EventCancellable;
import lombok.Generated;

public class EventInput
extends EventCancellable
implements Event {
    private int key;

    @Generated
    public int getKey() {
        return this.key;
    }

    @Generated
    public void setKey(int key) {
        this.key = key;
    }

    @Generated
    public EventInput(int key) {
        this.key = key;
    }
}

