/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package fun.kubik.itemics.api.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public final class RayTraceUtils {
    private RayTraceUtils() {
    }

    public static RayTraceResult rayTraceTowards(Entity entity, Rotation rotation, double blockReachDistance) {
        return RayTraceUtils.rayTraceTowards(entity, rotation, blockReachDistance, false);
    }

    public static boolean rayTraceSingleEntity(float yaw, float pitch, double distance, Entity entity) {
        Vector3d eyeVec = Helper.mc.player.getEyePosition(1.0f);
        Vector3d lookVec = Helper.mc.player.getVectorForRotation(pitch, yaw);
        Vector3d extendedVec = eyeVec.add(lookVec.scale(distance));
        AxisAlignedBB AABB = entity.getBoundingBox();
        return AABB.contains(eyeVec) || AABB.rayTrace(eyeVec, extendedVec).isPresent();
    }

    public static RayTraceResult rayTraceTowards(Entity entity, Rotation rotation, double blockReachDistance, boolean wouldSneak) {
        Vector3d start = wouldSneak ? RayTraceUtils.inferSneakingEyePosition(entity) : entity.getEyePosition(1.0f);
        Vector3d direction = RotationUtils.calcVector3dFromRotation(rotation);
        Vector3d end = start.add(direction.x * blockReachDistance, direction.y * blockReachDistance, direction.z * blockReachDistance);
        return entity.world.rayTraceBlocks(new RayTraceContext(start, end, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, entity));
    }

    public static Vector3d inferSneakingEyePosition(Entity entity) {
        return new Vector3d(entity.getPosX(), entity.getPosY() + IPlayerContext.eyeHeight(true), entity.getPosZ());
    }
}

