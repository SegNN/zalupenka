/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.utils.schematic.schematica;

import com.github.lunatrius.schematica.client.world.SchematicWorld;
import java.util.List;

import fun.kubik.itemics.api.schematic.IStaticSchematic;
import net.minecraft.block.BlockState;

public final class SchematicAdapter
        implements IStaticSchematic {
    private final SchematicWorld schematic;

    public SchematicAdapter(SchematicWorld schematicWorld) {
        this.schematic = schematicWorld;
    }

    @Override
    public BlockState desiredState(int x, int y, int z, BlockState current, List<BlockState> approxPlaceable) {
        return this.getDirect(x, y, z);
    }

    @Override
    public BlockState getDirect(int x, int y, int z) {
        //return this.schematic.getSchematic().getBlockState(new BlockPos(x, y, z));
        return null;
    }

    @Override
    public int widthX() {
        return 0;
    }

    @Override
    public int heightY() {
        return 0;
    }

    @Override
    public int lengthZ() {
        return 0;
    }
}

