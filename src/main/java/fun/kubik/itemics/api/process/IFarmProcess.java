/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.process;

import net.minecraft.util.math.BlockPos;

public interface IFarmProcess
extends IItemicsProcess {
    public void farm(int var1, BlockPos var2);

    default public void farm() {
        this.farm(0, null);
    }

    default public void farm(int range) {
        this.farm(range, null);
    }
}

