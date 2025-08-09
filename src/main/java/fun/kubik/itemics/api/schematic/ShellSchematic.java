/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.schematic;

import net.minecraft.block.BlockState;

public class ShellSchematic
extends MaskSchematic {
    public ShellSchematic(ISchematic schematic) {
        super(schematic);
    }

    @Override
    protected boolean partOfMask(int x, int y, int z, BlockState currentState) {
        return x == 0 || y == 0 || z == 0 || x == this.widthX() - 1 || y == this.heightY() - 1 || z == this.lengthZ() - 1;
    }
}

