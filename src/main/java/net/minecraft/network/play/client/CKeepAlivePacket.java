/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CKeepAlivePacket
        implements IPacket<IServerPlayNetHandler> {
    private long key;

    public CKeepAlivePacket() {
    }

    public CKeepAlivePacket(long idIn) {
        this.key = idIn;
    }

    @Override
    public void processPacket(IServerPlayNetHandler handler) {
        handler.processKeepAlive(this);
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.key = buf.readLong();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeLong(this.key);
    }

    public long getKey() {
        return this.key;
    }
}

