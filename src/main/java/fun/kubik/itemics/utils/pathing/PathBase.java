/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.utils.pathing;

import fun.kubik.itemics.Itemics;
import fun.kubik.itemics.api.ItemicsAPI;
import fun.kubik.itemics.api.pathing.calc.IPath;
import fun.kubik.itemics.api.pathing.goals.Goal;
import fun.kubik.itemics.pathing.path.CutoffPath;
import fun.kubik.itemics.utils.BlockStateInterface;
import net.minecraft.util.math.BlockPos;

public abstract class PathBase
implements IPath {
    @Override
    public PathBase cutoffAtLoadedChunks(Object bsi0) {
        if (!((Boolean)Itemics.settings().cutoffAtLoadBoundary.value).booleanValue()) {
            return this;
        }
        BlockStateInterface bsi = (BlockStateInterface)bsi0;
        for (int i = 0; i < this.positions().size(); ++i) {
            BlockPos pos = this.positions().get(i);
            if (bsi.worldContainsLoadedChunk(pos.getX(), pos.getZ())) continue;
            return new CutoffPath(this, i);
        }
        return this;
    }

    @Override
    public PathBase staticCutoff(Goal destination) {
        int min = (Integer)ItemicsAPI.getSettings().pathCutoffMinimumLength.value;
        if (this.length() < min) {
            return this;
        }
        if (destination == null || destination.isInGoal(this.getDest())) {
            return this;
        }
        double factor = (Double)ItemicsAPI.getSettings().pathCutoffFactor.value;
        int newLength = (int)((double)(this.length() - min) * factor) + min - 1;
        return new CutoffPath(this, newLength);
    }
}

