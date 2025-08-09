/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.utils;

import com.google.common.collect.ImmutableSet;
// import io.netty.util.concurrent.ThreadPerTaskExecutor;
// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.HashMap;
import java.util.HashSet;
// import java.util.List;
// import java.util.Map;
// import java.util.Random;
import java.util.Set;
// import java.util.concurrent.CompletableFuture;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
// import net.minecraft.loot.LootContext;
// import net.minecraft.loot.LootParameterSets;
// import net.minecraft.loot.LootParameters;
// import net.minecraft.loot.LootPredicateManager;
// import net.minecraft.loot.LootTableManager;
// import net.minecraft.loot.LootTables;
// import net.minecraft.resources.IResourcePack;
// import net.minecraft.resources.ResourcePackInfo;
// import net.minecraft.resources.ResourcePackList;
// import net.minecraft.resources.ResourcePackType;
// import net.minecraft.resources.ServerPackFinder;
// import net.minecraft.resources.SimpleReloadableResourceManager;
// import net.minecraft.util.IItemProvider;
// import net.minecraft.util.ResourceLocation;
// import net.minecraft.util.Unit;
// import net.minecraft.util.math.BlockPos;
// import net.minecraft.util.math.vector.Vector3d;

public final class BlockOptionalMeta {
    private final Block block;
    private final Set<BlockState> blockstates;
    private final ImmutableSet<Integer> stateHashes;
    private final ImmutableSet<Integer> stackHashes;
    private static final Pattern pattern = Pattern.compile("^(.+?)(?::(\\d+))?$");
    // private static LootTableManager manager;
    // private static LootPredicateManager predicate;
    // private static Map<Block, List<Item>> drops;

    public BlockOptionalMeta(@Nonnull Block block) {
        this.block = block;
        this.blockstates = BlockOptionalMeta.getStates(block);
        this.stateHashes = BlockOptionalMeta.getStateHashes(this.blockstates);
        this.stackHashes = BlockOptionalMeta.getStackHashes(this.blockstates);
    }

    public BlockOptionalMeta(@Nonnull String selector) {
        Matcher matcher = pattern.matcher(selector);
        if (!matcher.find()) {
            throw new IllegalArgumentException("invalid block selector");
        }
        MatchResult matchResult = matcher.toMatchResult();
        this.block = BlockUtils.stringToBlockRequired(matchResult.group(1));
        this.blockstates = BlockOptionalMeta.getStates(this.block);
        this.stateHashes = BlockOptionalMeta.getStateHashes(this.blockstates);
        this.stackHashes = BlockOptionalMeta.getStackHashes(this.blockstates);
    }

    private static Set<BlockState> getStates(@Nonnull Block block) {
        return new HashSet<BlockState>(block.getStateContainer().getValidStates());
    }

    private static ImmutableSet<Integer> getStateHashes(Set<BlockState> blockstates) {
        return ImmutableSet.copyOf((Integer[])blockstates.stream().map(Object::hashCode).toArray(Integer[]::new));
    }

    private static ImmutableSet<Integer> getStackHashes(Set<BlockState> blockstates) {
        // Compute a safe set of item IDs corresponding to this block's item form.
        // Avoids client-side loot table generation which requires server context and
        // was causing command parse failures.
        return ImmutableSet.copyOf(
                blockstates.stream()
                        .map(state -> Item.getItemFromBlock(state.getBlock()))
                        .filter(item -> item != null && item != Items.AIR)
                        .map(Item::getIdFromItem)
                        .toArray(Integer[]::new)
        );
    }

    public Block getBlock() {
        return this.block;
    }

    public boolean matches(@Nonnull Block block) {
        return block == this.block;
    }

    public boolean matches(@Nonnull BlockState blockstate) {
        Block block = blockstate.getBlock();
        return block == this.block && this.stateHashes.contains(blockstate.hashCode());
    }

    public boolean matches(ItemStack stack) {
        int hash = Item.getIdFromItem(stack.getItem());
        return this.stackHashes.contains(hash);
    }

    public String toString() {
        return String.format("BlockOptionalMeta{block=%s}", this.block);
    }

    public BlockState getAnyBlockState() {
        if (this.blockstates.size() > 0) {
            return this.blockstates.iterator().next();
        }
        return null;
    }

    // public static LootTableManager getManager() {
    //     if (manager == null) {
    //         ResourcePackList rpl = new ResourcePackList(ResourcePackInfo::new, new ServerPackFinder());
    //         rpl.reloadPacksFromFinders();
    //         IResourcePack thePack = rpl.getAllPacks().iterator().next().getResourcePack();
    //         SimpleReloadableResourceManager resourceManager = new SimpleReloadableResourceManager(ResourcePackType.SERVER_DATA);
    //         manager = new LootTableManager(predicate);
    //         resourceManager.addReloadListener(manager);
    //         try {
    //             resourceManager.reloadResourcesAndThen(new ThreadPerTaskExecutor(Thread::new), new ThreadPerTaskExecutor(Thread::new), Collections.singletonList(thePack), CompletableFuture.completedFuture(Unit.INSTANCE)).get();
    //         } catch (Exception exception) {
    //             throw new RuntimeException(exception);
    //         }
    //     }
    //     return manager;
    // }

    // public static LootPredicateManager getPredicateManager() {
    //     return predicate;
    // }

    // private static synchronized List<Item> drops(Block b) {
    //     return drops.computeIfAbsent(b, block -> {
    //         ResourceLocation lootTableLocation = block.getLootTable();
    //         if (lootTableLocation == LootTables.EMPTY) {
    //             return Collections.emptyList();
    //         }
    //         ArrayList<Item> items = new ArrayList<>();
    //         BlockOptionalMeta.getManager().getLootTableFromLocation(lootTableLocation).generate(new LootContext.Builder(null).withRandom(new Random()).withParameter(LootParameters.field_237457_g_, Vector3d.copy(BlockPos.NULL_VECTOR)).withParameter(LootParameters.TOOL, ItemStack.EMPTY).withNullableParameter(LootParameters.BLOCK_ENTITY, null).withParameter(LootParameters.BLOCK_STATE, block.getDefaultState()).build(LootParameterSets.BLOCK), stack -> items.add(stack.getItem()));
    //         return items;
    //     });
    // }

    static {
        // predicate = new LootPredicateManager();
        // drops = new HashMap<Block, List<Item>>();
    }
}
