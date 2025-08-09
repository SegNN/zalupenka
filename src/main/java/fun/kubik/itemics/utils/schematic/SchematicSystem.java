/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.utils.schematic;

import fun.kubik.itemics.api.command.registry.Registry;
import fun.kubik.itemics.api.schematic.ISchematicSystem;
import fun.kubik.itemics.api.schematic.format.ISchematicFormat;
import fun.kubik.itemics.utils.schematic.format.DefaultSchematicFormats;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;

public enum SchematicSystem implements ISchematicSystem
{
    INSTANCE;

    private final Registry<ISchematicFormat> registry = new Registry();

    private SchematicSystem() {
        Arrays.stream(DefaultSchematicFormats.values()).forEach(this.registry::register);
    }

    @Override
    public Registry<ISchematicFormat> getRegistry() {
        return this.registry;
    }

    @Override
    public Optional<ISchematicFormat> getByFile(File file) {
        return this.registry.stream().filter(format -> format.isFileType(file)).findFirst();
    }
}

