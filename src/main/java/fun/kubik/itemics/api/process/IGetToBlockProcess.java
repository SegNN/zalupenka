/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.process;

import fun.kubik.itemics.api.utils.BlockOptionalMeta;
import net.minecraft.block.Block;

public interface IGetToBlockProcess
extends IItemicsProcess {
    public void getToBlock(BlockOptionalMeta var1);

    default public void getToBlock(Block block) {
        this.getToBlock(new BlockOptionalMeta(block));
    }

    public boolean blacklistClosest();
}

