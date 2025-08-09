/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.pathing.goals;

import fun.kubik.itemics.api.utils.SettingsUtil;
import fun.kubik.itemics.api.utils.interfaces.IGoalRenderPos;
import net.minecraft.util.math.BlockPos;

public class GoalTwoBlocks
implements Goal,
IGoalRenderPos {
    protected final int x;
    protected final int y;
    protected final int z;

    public GoalTwoBlocks(BlockPos pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public GoalTwoBlocks(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean isInGoal(int x, int y, int z) {
        return x == this.x && (y == this.y || y == this.y - 1) && z == this.z;
    }

    @Override
    public double heuristic(int x, int y, int z) {
        int xDiff = x - this.x;
        int yDiff = y - this.y;
        int zDiff = z - this.z;
        return GoalBlock.calculate(xDiff, yDiff < 0 ? yDiff + 1 : yDiff, zDiff);
    }

    @Override
    public BlockPos getGoalPos() {
        return new BlockPos(this.x, this.y, this.z);
    }

    public String toString() {
        return String.format("GoalTwoBlocks{x=%s,y=%s,z=%s}", SettingsUtil.maybeCensor(this.x), SettingsUtil.maybeCensor(this.y), SettingsUtil.maybeCensor(this.z));
    }
}

