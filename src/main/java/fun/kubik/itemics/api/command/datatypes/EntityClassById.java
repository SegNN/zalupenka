/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.command.datatypes;

import fun.kubik.itemics.api.command.exception.CommandException;
import fun.kubik.itemics.api.command.helpers.TabCompleteHelper;
import java.util.stream.Stream;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public enum EntityClassById implements IDatatypeFor<EntityType>
{
    INSTANCE;


    @Override
    public EntityType get(IDatatypeContext ctx) throws CommandException {
        ResourceLocation id = new ResourceLocation(ctx.getConsumer().getString());
        EntityType entity = Registry.ENTITY_TYPE.getOptional(id).orElse(null);
        if (entity == null) {
            throw new IllegalArgumentException("no entity found by that id");
        }
        return entity;
    }

    @Override
    public Stream<String> tabComplete(IDatatypeContext ctx) throws CommandException {
        return new TabCompleteHelper().append(Registry.ENTITY_TYPE.stream().map(Object::toString)).filterPrefixNamespaced(ctx.getConsumer().getString()).sortAlphabetically().stream();
    }
}

