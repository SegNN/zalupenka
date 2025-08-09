/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.event.events;

import fun.kubik.itemics.api.event.events.type.Cancellable;

public final class ChatEvent
extends Cancellable {
    private final String message;

    public ChatEvent(String message) {
        this.message = message;
    }

    public final String getMessage() {
        return this.message;
    }
}

