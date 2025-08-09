/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.schematic;

import net.minecraft.block.BlockState;

public interface IStaticSchematic
extends ISchematic {
    public BlockState getDirect(int var1, int var2, int var3);

    default public BlockState[] getColumn(int x, int z) {
        BlockState[] column = new BlockState[this.heightY()];
        for (int i = 0; i < this.heightY(); ++i) {
            column[i] = this.getDirect(x, i, z);
        }
        return column;
    }
}

