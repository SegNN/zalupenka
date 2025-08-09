/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.event.events;

import net.minecraft.util.math.BlockPos;

public final class BlockInteractEvent {
    private final BlockPos pos;
    private final Type type;

    public BlockInteractEvent(BlockPos pos, Type type) {
        this.pos = pos;
        this.type = type;
    }

    public final BlockPos getPos() {
        return this.pos;
    }

    public final Type getType() {
        return this.type;
    }

    public static enum Type {
        START_BREAK,
        USE;

    }
}

