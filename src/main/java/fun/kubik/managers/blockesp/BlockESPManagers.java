package fun.kubik.managers.blockesp;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BlockESPManagers extends ConcurrentHashMap<Block, Integer> {
    private final Set<BlockPos> cachedBlockPos = new HashSet<>();
    private final Object lock = new Object();
    private volatile boolean updating = false;
    private BlockPos lastPlayerPos = BlockPos.ZERO;
    private int tickCounter = 0;

    public boolean addBlock(Block block, int color) {
        return this.putIfAbsent(block, color) == null;
    }

    public boolean removeBlock(Block block) {
        return this.remove(block) != null;
    }

    public int getColorFor(Block block) {
        return this.getOrDefault(block, -1);
    }

    public void updateCacheAsync(World world, BlockPos playerPos) {
        if (updating || (tickCounter++ < 10 && playerPos.equals(lastPlayerPos))) {
            return;
        }
        updating = true;
        lastPlayerPos = playerPos;
        tickCounter = 0;

        new Thread(() -> {
            Set<BlockPos> newCache = new HashSet<>();
            int radius = 60;
            BlockPos min = playerPos.add(-radius, -100, -radius);
            BlockPos max = playerPos.add(radius, 100, radius);

            BlockPos.getAllInBox(min, max).forEach(pos -> {
                if (world.isBlockLoaded(pos)) {
                    Block block = world.getBlockState(pos).getBlock();
                    if (this.containsKey(block)) {
                        newCache.add(pos.toImmutable());
                    }
                }
            });

            synchronized (lock) {
                cachedBlockPos.clear();
                cachedBlockPos.addAll(newCache);
            }
            updating = false;
        }, "BlockESP Cache Updater").start();
    }

    public Set<BlockPos> getCachedBlockPos() {
        return cachedBlockPos;
    }

    public Object getLock() {
        return lock;
    }

    public boolean isUpdating() {
        return updating;
    }

    public BlockPos getLastPlayerPos() {
        return lastPlayerPos;
    }

    public int getTickCounter() {
        return tickCounter;
    }
}