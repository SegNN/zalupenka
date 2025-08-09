/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.pathing.movement;

import fun.kubik.itemics.api.utils.BetterBlockPos;
import net.minecraft.util.math.BlockPos;

public interface IMovement {
    public double getCost();

    public MovementStatus update();

    public void reset();

    public void resetBlockCache();

    public boolean safeToCancel();

    public boolean calculatedWhileLoaded();

    public BetterBlockPos getSrc();

    public BetterBlockPos getDest();

    public BlockPos getDirection();
}

