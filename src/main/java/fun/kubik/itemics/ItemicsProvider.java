/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics;

import fun.kubik.itemics.api.IItemics;
import fun.kubik.itemics.api.IItemicsProvider;
import fun.kubik.itemics.api.cache.IWorldScanner;
import fun.kubik.itemics.api.command.ICommandSystem;
import fun.kubik.itemics.api.schematic.ISchematicSystem;
import fun.kubik.itemics.cache.WorldScanner;
import fun.kubik.itemics.command.CommandSystem;
import fun.kubik.itemics.command.ExampleItemicsControl;
import fun.kubik.itemics.utils.schematic.SchematicSystem;
import java.util.Collections;
import java.util.List;

public final class ItemicsProvider
implements IItemicsProvider {
    private final Itemics primary = new Itemics();
    private final List<IItemics> all = Collections.singletonList(this.primary);

    public ItemicsProvider() {
        new ExampleItemicsControl(this.primary);
    }

    @Override
    public IItemics getPrimaryItemics() {
        return this.primary;
    }

    @Override
    public List<IItemics> getAllItemics() {
        return this.all;
    }

    @Override
    public IWorldScanner getWorldScanner() {
        return WorldScanner.INSTANCE;
    }

    @Override
    public ICommandSystem getCommandSystem() {
        return CommandSystem.INSTANCE;
    }

    @Override
    public ISchematicSystem getSchematicSystem() {
        return SchematicSystem.INSTANCE;
    }
}

