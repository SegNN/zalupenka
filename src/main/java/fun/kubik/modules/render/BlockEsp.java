/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.modules.render;

import fun.kubik.Load;
import fun.kubik.events.api.EventHook;
import fun.kubik.events.main.render.EventRender3D;
import fun.kubik.helpers.visual.VisualHelpers;
import fun.kubik.managers.blockesp.BlockESPManagers;
import fun.kubik.managers.module.Module;
import fun.kubik.managers.module.main.Category;
import net.minecraft.util.math.BlockPos;

public class BlockEsp
extends Module {
    public BlockEsp() {
        super("BlockEsp", Category.RENDER);
    }
@EventHook
    public void render(EventRender3D.Post event) {
        if (BlockEsp.mc.player != null && BlockEsp.mc.world != null) {
            BlockESPManagers blockESPManagers = Load.getInstance().getHooks().getBlockESPManagers();
            blockESPManagers.updateCacheAsync(BlockEsp.mc.world, BlockEsp.mc.player.getPosition());
            for (BlockPos pos : blockESPManagers.getCachedBlockPos()) {
                int color = blockESPManagers.getColorFor(BlockEsp.mc.world.getBlockState(pos).getBlock());
                VisualHelpers.drawBlockBox(pos, color);
            }
        }
    }
}

