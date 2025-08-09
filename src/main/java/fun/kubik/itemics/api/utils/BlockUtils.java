/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.utils;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class BlockUtils {
    private static transient Map<String, Block> resourceCache = new HashMap<String, Block>();

    public static String blockToString(Block block) {
        ResourceLocation loc = Registry.BLOCK.getKey(block);
        String name = loc.getPath();
        if (!loc.getNamespace().equals("minecraft")) {
            name = loc.toString();
        }
        return name;
    }

    public static Block stringToBlockRequired(String name) {
        Block block = BlockUtils.stringToBlockNullable(name);
        if (block == null) {
            throw new IllegalArgumentException(String.format("Invalid block name %s", name));
        }
        return block;
    }

    public static Block stringToBlockNullable(String name) {
        Block block = resourceCache.get(name);
        if (block != null) {
            return block;
        }
        if (resourceCache.containsKey(name)) {
            return null;
        }
        block = Registry.BLOCK.getOptional(ResourceLocation.tryCreate((String)(name.contains(":") ? name : "minecraft:" + name))).orElse(null);
        HashMap<String, Block> copy = new HashMap<String, Block>(resourceCache);
        copy.put(name, block);
        resourceCache = copy;
        return block;
    }

    private BlockUtils() {
    }
}

