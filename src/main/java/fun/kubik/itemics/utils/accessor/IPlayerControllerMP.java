/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.utils.accessor;

import net.minecraft.util.math.BlockPos;

public interface IPlayerControllerMP {
    public void setIsHittingBlock(boolean var1);

    public BlockPos getCurrentBlock();

    public void callSyncCurrentPlayItem();
}

