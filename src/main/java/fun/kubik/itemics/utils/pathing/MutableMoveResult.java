/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.utils.pathing;

public final class MutableMoveResult {
    public int x;
    public int y;
    public int z;
    public double cost;

    public MutableMoveResult() {
        this.reset();
    }

    public final void reset() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.cost = 1000000.0;
    }
}

