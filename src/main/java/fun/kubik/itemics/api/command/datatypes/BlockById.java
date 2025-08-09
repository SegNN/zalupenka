/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.command.datatypes;

import fun.kubik.itemics.api.command.exception.CommandException;
import fun.kubik.itemics.api.command.helpers.TabCompleteHelper;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public enum BlockById implements IDatatypeFor<Block>
{
    INSTANCE;


    @Override
    public Block get(IDatatypeContext ctx) throws CommandException {
        ResourceLocation id = new ResourceLocation(ctx.getConsumer().getString());
        Block block = Registry.BLOCK.getOptional(id).orElse(null);
        if (block == null) {
            throw new IllegalArgumentException("no block found by that id");
        }
        return block;
    }

    @Override
    public Stream<String> tabComplete(IDatatypeContext ctx) throws CommandException {
        return new TabCompleteHelper().append(Registry.BLOCK.keySet().stream().map(Object::toString)).filterPrefixNamespaced(ctx.getConsumer().getString()).sortAlphabetically().stream();
    }
}

