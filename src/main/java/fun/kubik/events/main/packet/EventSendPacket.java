/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.events.main.packet;

import fun.kubik.events.api.main.callables.EventCancellable;
import net.minecraft.network.IPacket;

public class EventSendPacket
        extends EventCancellable {
    private final IPacket<?> packet;

    public EventSendPacket(IPacket packet) {
        this.packet = packet;
    }

    public IPacket getPacket() {
        return this.packet;
    }
}

