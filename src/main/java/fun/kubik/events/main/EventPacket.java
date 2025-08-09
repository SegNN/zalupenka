package fun.kubik.events.main;

import fun.kubik.itemics.api.event.events.type.Cancellable;
import net.minecraft.network.IPacket;

public class EventPacket extends Cancellable {
    private IPacket<?> packet;
    private EventPacket.Type type;

    public boolean isSend() {
        return this.type == EventPacket.Type.SEND;
    }

    public boolean isReceive() {
        return this.type == EventPacket.Type.RECEIVE;
    }

    public IPacket<?> getPacket() {
        return this.packet;
    }

    public EventPacket.Type getType() {
        return this.type;
    }

    public void setPacket(IPacket<?> packet) {
        this.packet = packet;
    }

    public void setType(EventPacket.Type type) {
        this.type = type;
    }

    public EventPacket(IPacket<?> packet, EventPacket.Type type) {
        this.packet = packet;
        this.type = type;
    }

    public static enum Type {
        RECEIVE,
        SEND;
    }
}
