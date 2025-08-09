package fun.kubik.itemics.utils;

import fun.kubik.itemics.Itemics;
import fun.kubik.itemics.api.utils.IPlayerContext;
import fun.kubik.itemics.cache.CachedRegion;
import fun.kubik.itemics.cache.WorldData;
import fun.kubik.itemics.utils.pathing.BetterWorldBorder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;

public class BlockStateInterface {
    private final ClientChunkProvider provider;
    private final WorldData worldData;
    protected final IBlockReader world;
    public final BlockPos.Mutable isPassableBlockPos;
    public final IBlockReader access;
    public final BetterWorldBorder worldBorder;
    private Chunk prev = null;
    private CachedRegion prevCached = null;
    private final boolean useTheRealWorld;
    private static final BlockState AIR = Blocks.AIR.getDefaultState();

    public BlockStateInterface(IPlayerContext ctx) {
        this(ctx, false);
    }

    public BlockStateInterface(IPlayerContext ctx, boolean copyLoadedChunks) {
        this(ctx.world(), (WorldData)ctx.worldData(), copyLoadedChunks);
    }

    public BlockStateInterface(World world, WorldData worldData, boolean copyLoadedChunks) {
        this.world = world;
        this.worldBorder = new BetterWorldBorder(world.getWorldBorder());
        this.worldData = worldData;
        this.provider = (ClientChunkProvider)world.getChunkProvider();
        this.useTheRealWorld = !(Boolean)Itemics.settings().pathThroughCachedOnly.value;
        if (!Minecraft.getInstance().isOnExecutionThread()) {
            throw new IllegalStateException();
        }
        this.isPassableBlockPos = new BlockPos.Mutable();
        this.access = new BlockStateInterfaceAccessWrapper(this);
    }

    public boolean worldContainsLoadedChunk(int blockX, int blockZ) {
        return this.provider.getChunk(blockX >> 4, blockZ >> 4, ChunkStatus.FULL, false) != null; // Замена chunkExists на getChunk
    }

    public static Block getBlock(IPlayerContext ctx, BlockPos pos) {
        return BlockStateInterface.get(ctx, pos).getBlock();
    }

    public static BlockState get(IPlayerContext ctx, BlockPos pos) {
        return new BlockStateInterface(ctx).get0(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockState get0(BlockPos pos) {
        return this.get0(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockState get0(int x, int y, int z) {
        if (y < 0 || y >= 256) {
            return AIR;
        }
        if (this.useTheRealWorld) {
            Chunk chunk = this.prev;
            if (chunk != null && chunk.getPos().x == x >> 4 && chunk.getPos().z == z >> 4) {
                return BlockStateInterface.getFromChunk(chunk, x, y, z);
            }
            chunk = this.provider.getChunk(x >> 4, z >> 4, ChunkStatus.FULL, false); // Замена getLoadedChunk на getChunk
            if (chunk != null && !chunk.isEmpty()) {
                this.prev = chunk;
                return BlockStateInterface.getFromChunk(chunk, x, y, z);
            }
        }
        CachedRegion cached = this.prevCached;
        if (cached == null || cached.getX() != x >> 9 || cached.getZ() != z >> 9) {
            if (this.worldData == null) {
                return AIR;
            }
            CachedRegion region = this.worldData.cache.getRegion(x >> 9, z >> 9);
            if (region == null) {
                return AIR;
            }
            this.prevCached = region;
            cached = region;
        }
        BlockState type = cached.getBlock(x & 0x1FF, y, z & 0x1FF);
        return type == null ? AIR : type;
    }

    public boolean isLoaded(int x, int z) {
        Chunk prevChunk = this.prev;
        if (prevChunk != null && prevChunk.getPos().x == x >> 4 && prevChunk.getPos().z == z >> 4) {
            return true;
        }
        prevChunk = this.provider.getChunk(x >> 4, z >> 4, ChunkStatus.FULL, false); // Замена getLoadedChunk на getChunk
        if (prevChunk != null && !prevChunk.isEmpty()) {
            this.prev = prevChunk;
            return true;
        }
        CachedRegion prevRegion = this.prevCached;
        if (prevRegion != null && prevRegion.getX() == x >> 9 && prevRegion.getZ() == z >> 9) {
            return prevRegion.isCached(x & 0x1FF, z & 0x1FF);
        }
        if (this.worldData == null) {
            return false;
        }
        prevRegion = this.worldData.cache.getRegion(x >> 9, z >> 9);
        if (prevRegion == null) {
            return false;
        }
        this.prevCached = prevRegion;
        return prevRegion.isCached(x & 0x1FF, z & 0x1FF);
    }

    public static BlockState getFromChunk(Chunk chunk, int x, int y, int z) {
        ChunkSection section = chunk.getSections()[y >> 4];
        if (ChunkSection.isEmpty(section)) {
            return AIR;
        }
        return section.getBlockState(x & 0xF, y & 0xF, z & 0xF);
    }
}