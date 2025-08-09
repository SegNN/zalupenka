/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.utils.accessor;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

public interface ISodiumChunkArray
extends IChunkArray {
    public ObjectIterator<Long2ObjectMap.Entry<Object>> callIterator();
}

