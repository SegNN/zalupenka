/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.selection;

import fun.kubik.itemics.api.utils.BetterBlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3i;

public interface ISelection {
    public BetterBlockPos pos1();

    public BetterBlockPos pos2();

    public BetterBlockPos min();

    public BetterBlockPos max();

    public Vector3i size();

    public AxisAlignedBB aabb();

    public ISelection expand(Direction var1, int var2);

    public ISelection contract(Direction var1, int var2);

    public ISelection shift(Direction var1, int var2);
}

