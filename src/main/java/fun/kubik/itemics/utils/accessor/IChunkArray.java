/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.utils.accessor;

import java.util.concurrent.atomic.AtomicReferenceArray;
import net.minecraft.world.chunk.Chunk;

public interface IChunkArray {
    public void copyFrom(IChunkArray var1);

    public AtomicReferenceArray<Chunk> getChunks();

    public int centerX();

    public int centerZ();

    public int viewDistance();
}

