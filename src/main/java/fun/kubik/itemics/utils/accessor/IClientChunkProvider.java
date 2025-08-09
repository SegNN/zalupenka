/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.utils.accessor;

import net.minecraft.client.multiplayer.ClientChunkProvider;

public interface IClientChunkProvider {
    public ClientChunkProvider createThreadSafeCopy();

    public IChunkArray extractReferenceArray();
}

