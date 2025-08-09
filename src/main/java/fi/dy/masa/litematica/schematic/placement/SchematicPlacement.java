/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fi.dy.masa.litematica.schematic.placement;

import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

public class SchematicPlacement
extends SchematicPlacementUnloaded {
    private Rotation rotation;
    private Mirror mirror;

    public Rotation getRotation() {
        return this.rotation;
    }

    public Mirror getMirror() {
        return this.mirror;
    }
}

