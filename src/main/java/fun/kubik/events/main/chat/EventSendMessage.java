/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.events.main.chat;

import fun.kubik.events.api.main.callables.EventCancellable;
import lombok.Generated;

public class EventSendMessage
extends EventCancellable {
    private final String message;

    @Generated
    public String getMessage() {
        return this.message;
    }

    @Generated
    public EventSendMessage(String message) {
        this.message = message;
    }
}

