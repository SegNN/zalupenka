/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.schematic;

import net.minecraft.block.BlockState;

public class WallsSchematic
extends MaskSchematic {
    public WallsSchematic(ISchematic schematic) {
        super(schematic);
    }

    @Override
    protected boolean partOfMask(int x, int y, int z, BlockState currentState) {
        return x == 0 || z == 0 || x == this.widthX() - 1 || z == this.lengthZ() - 1;
    }
}

