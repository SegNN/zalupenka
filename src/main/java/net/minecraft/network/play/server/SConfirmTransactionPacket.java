/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

public class SConfirmTransactionPacket
        implements IPacket<IClientPlayNetHandler> {
    private int windowId;
    private short actionNumber;
    private boolean accepted;

    public SConfirmTransactionPacket() {
    }

    public SConfirmTransactionPacket(int windowIdIn, short actionNumberIn, boolean acceptedIn) {
        this.windowId = windowIdIn;
        this.actionNumber = actionNumberIn;
        this.accepted = acceptedIn;
    }

    @Override
    public void processPacket(IClientPlayNetHandler handler) {
        handler.handleConfirmTransaction(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.windowId = buf.readUnsignedByte();
        this.actionNumber = buf.readShort();
        this.accepted = buf.readBoolean();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeByte(this.windowId);
        buf.writeShort(this.actionNumber);
        buf.writeBoolean(this.accepted);
    }

    public int getWindowId() {
        return this.windowId;
    }

    public short getActionNumber() {
        return this.actionNumber;
    }

    public boolean wasAccepted() {
        return this.accepted;
    }
}

