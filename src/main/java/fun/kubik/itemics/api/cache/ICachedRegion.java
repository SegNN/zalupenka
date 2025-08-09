/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.cache;

public interface ICachedRegion
extends IBlockTypeAccess {
    public boolean isCached(int var1, int var2);

    public int getX();

    public int getZ();
}

