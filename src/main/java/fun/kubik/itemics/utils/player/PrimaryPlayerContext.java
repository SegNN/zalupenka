/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.utils.player;

import fun.kubik.itemics.api.ItemicsAPI;
import fun.kubik.itemics.api.cache.IWorldData;
import fun.kubik.itemics.api.utils.Helper;
import fun.kubik.itemics.api.utils.IPlayerContext;
import fun.kubik.itemics.api.utils.IPlayerController;
import fun.kubik.itemics.api.utils.RayTraceUtils;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public enum PrimaryPlayerContext implements IPlayerContext,
Helper
{
    INSTANCE;


    @Override
    public ClientPlayerEntity player() {
        return PrimaryPlayerContext.mc.player;
    }

    @Override
    public IPlayerController playerController() {
        return PrimaryPlayerController.INSTANCE;
    }

    @Override
    public World world() {
        return PrimaryPlayerContext.mc.world;
    }

    @Override
    public IWorldData worldData() {
        return ItemicsAPI.getProvider().getPrimaryItemics().getWorldProvider().getCurrentWorld();
    }

    @Override
    public RayTraceResult objectMouseOver() {
        return RayTraceUtils.rayTraceTowards(this.player(), this.playerRotations(), this.playerController().getBlockReachDistance());
    }
}

