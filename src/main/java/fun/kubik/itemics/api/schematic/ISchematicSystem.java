/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.schematic;

import fun.kubik.itemics.api.command.registry.Registry;
import fun.kubik.itemics.api.schematic.format.ISchematicFormat;
import java.io.File;
import java.util.Optional;

public interface ISchematicSystem {
    public Registry<ISchematicFormat> getRegistry();

    public Optional<ISchematicFormat> getByFile(File var1);
}

