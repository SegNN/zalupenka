/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.cache;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface IBlockTypeAccess {
    public BlockState getBlock(int var1, int var2, int var3);

    default public BlockState getBlock(BlockPos pos) {
        return this.getBlock(pos.getX(), pos.getY(), pos.getZ());
    }
}

