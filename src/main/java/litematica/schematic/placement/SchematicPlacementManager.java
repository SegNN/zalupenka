/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package litematica.schematic.placement;

import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;

import java.util.ArrayList;
import java.util.List;

public class SchematicPlacementManager {
    private final List<SchematicPlacement> schematicPlacements = new ArrayList<SchematicPlacement>();

    public List<SchematicPlacement> getAllSchematicsPlacements() {
        return this.schematicPlacements;
    }
}

