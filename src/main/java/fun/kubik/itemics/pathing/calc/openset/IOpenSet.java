/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.pathing.calc.openset;

import fun.kubik.itemics.pathing.calc.PathNode;

public interface IOpenSet {
    public void insert(PathNode var1);

    public boolean isEmpty();

    public PathNode removeLowest();

    public void update(PathNode var1);
}

