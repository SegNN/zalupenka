/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.schematic.format;

import fun.kubik.itemics.api.schematic.IStaticSchematic;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface ISchematicFormat {
    public IStaticSchematic parse(InputStream var1) throws IOException;

    public boolean isFileType(File var1);
}

