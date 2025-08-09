/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.behavior;

import fun.kubik.itemics.Itemics;
import fun.kubik.itemics.api.behavior.IBehavior;
import fun.kubik.itemics.api.utils.IPlayerContext;

public class Behavior
implements IBehavior {
    public final Itemics itemics;
    public final IPlayerContext ctx;

    protected Behavior(Itemics itemics) {
        this.itemics = itemics;
        this.ctx = itemics.getPlayerContext();
        itemics.registerBehavior(this);
    }
}

