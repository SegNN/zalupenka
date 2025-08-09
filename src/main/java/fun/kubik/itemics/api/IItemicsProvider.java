/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api;

import fun.kubik.itemics.api.cache.IWorldScanner;
import fun.kubik.itemics.api.command.ICommandSystem;
import fun.kubik.itemics.api.schematic.ISchematicSystem;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.entity.player.ClientPlayerEntity;

public interface IItemicsProvider {
    public IItemics getPrimaryItemics();

    public List<IItemics> getAllItemics();

    default public IItemics getItemicsForPlayer(ClientPlayerEntity player) {
        for (IItemics itemics : this.getAllItemics()) {
            if (!Objects.equals(player, itemics.getPlayerContext().player())) continue;
            return itemics;
        }
        return null;
    }

    public IWorldScanner getWorldScanner();

    public ICommandSystem getCommandSystem();

    public ISchematicSystem getSchematicSystem();
}

