/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.utils;

import fun.kubik.itemics.Itemics;
import fun.kubik.itemics.api.process.IItemicsProcess;
import fun.kubik.itemics.api.utils.Helper;
import fun.kubik.itemics.api.utils.IPlayerContext;

public abstract class ItemicsProcessHelper
implements IItemicsProcess,
Helper {
    protected final Itemics itemics;
    protected final IPlayerContext ctx;

    public ItemicsProcessHelper(Itemics itemics) {
        this.itemics = itemics;
        this.ctx = itemics.getPlayerContext();
    }

    @Override
    public boolean isTemporary() {
        return false;
    }
}

